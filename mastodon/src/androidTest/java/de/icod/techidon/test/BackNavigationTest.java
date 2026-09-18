package de.icod.techidon.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.Toolbar;

import de.icod.techidon.MainActivity;
import de.icod.techidon.model.Attachment;
import de.icod.techidon.ui.photoviewer.PhotoViewer;
import de.icod.techidon.ui.viewcontrollers.DropdownSubmenuController;
import de.icod.techidon.ui.viewcontrollers.ToolbarDropdownMenuController;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;

/**
 * Back must keep working when the system delivers it through OnBackInvokedCallback instead of
 * onBackPressed()/KEYCODE_BACK, which is the default for apps targeting API 36 on Android 16+.
 * There, an injected back key is handed to the window's topmost OnBackInvokedCallback, the same one a back gesture invokes.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackNavigationTest{
	private static final long TIMEOUT_MS=5000;

	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule=new ActivityScenarioRule<>(MainActivity.class);

	private final Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();

	@Test
	public void backOnStandaloneFragmentIsHandledByMainActivity(){
		// Fragments opened on their own (e.g. from a notification) rely on MainActivity to go back to the home screen.
		// With no account to go back to, it consumes the event instead of letting the activity close.
		waitForActivityWindowFocus(true);
		activityScenarioRule.getScenario().onActivity(activity->{
			Bundle args=new Bundle();
			args.putBoolean("_can_go_back", true);
			Fragment fragment=new Fragment();
			fragment.setArguments(args);
			activity.showFragmentClearingBackStack(fragment);
		});

		instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		SystemClock.sleep(1000);

		assertEquals(Lifecycle.State.RESUMED, activityScenarioRule.getScenario().getState());
	}

	@Test
	public void backClosesPhotoViewer() throws InterruptedException{
		CountDownLatch dismissing=new CountDownLatch(1);
		waitForActivityWindowFocus(true);
		activityScenarioRule.getScenario().onActivity(activity->{
			Attachment attachment=new Attachment();
			attachment.type=Attachment.Type.IMAGE;
			attachment.url="http://127.0.0.1/image.jpg";
			new PhotoViewer(activity, Collections.singletonList(attachment), 0, null, null, new PhotoViewerListener(){
				@Override
				public void setPhotoViewVisibility(int index, boolean visible){
					// Only happens when the viewer starts closing
					if(visible)
						dismissing.countDown();
				}
			});
		});

		pressBackInOverlayWindow();

		assertTrue("Photo viewer didn't close on back", dismissing.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
		// Let the closing animation remove the window before the activity goes away
		waitForActivityWindowFocus(true);
		SystemClock.sleep(500);
	}

	@Test
	public void backPopsDropdownSubmenuThenDismissesDropdown() throws InterruptedException{
		DropdownHost host=new DropdownHost();
		TestSubmenu[] submenu={null};
		waitForActivityWindowFocus(true);
		activityScenarioRule.getScenario().onActivity(activity->{
			host.activity=activity;
			host.toolbar=new Toolbar(activity);
			ToolbarDropdownMenuController controller=new ToolbarDropdownMenuController(host);
			controller.show(new TestSubmenu(controller, null));
			submenu[0]=new TestSubmenu(controller, "Back");
			controller.pushSubmenuController(submenu[0]);
		});

		pressBackInOverlayWindow();
		assertTrue("Submenu wasn't popped on back", submenu[0].dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
		assertEquals(1, host.willDismiss.getCount());

		// Let the submenu transition finish before pressing back again
		SystemClock.sleep(500);
		instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		assertTrue("Dropdown wasn't dismissed on back", host.dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
	}

	/**
	 * Presses back once a window added on top of the activity has taken input focus from it.
	 */
	private void pressBackInOverlayWindow(){
		waitForActivityWindowFocus(false);
		instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
	}

	private void waitForActivityWindowFocus(boolean focused){
		long deadline=SystemClock.uptimeMillis()+TIMEOUT_MS;
		boolean[] hasFocus={!focused};
		while(true){
			activityScenarioRule.getScenario().onActivity(activity->hasFocus[0]=activity.hasWindowFocus());
			if(hasFocus[0]==focused)
				return;
			if(SystemClock.uptimeMillis()>deadline)
				fail("Timed out waiting for activity window focus="+focused);
			SystemClock.sleep(50);
		}
	}

	private static abstract class PhotoViewerListener implements PhotoViewer.Listener{
		@Override
		public boolean startPhotoViewTransition(int index, @NonNull Rect outRect, @NonNull int[] outCornerRadius){
			return false;
		}

		@Override
		public void setTransitioningViewTransform(float translateX, float translateY, float scale){}

		@Override
		public void endPhotoViewTransition(){}

		@Nullable
		@Override
		public Drawable getPhotoViewCurrentDrawable(int index){
			return null;
		}

		@Override
		public void photoViewerDismissed(){}

		@Override
		public void onRequestPermissions(String[] permissions){}
	}

	private static class DropdownHost implements ToolbarDropdownMenuController.HostFragment{
		final CountDownLatch willDismiss=new CountDownLatch(1);
		final CountDownLatch dismissed=new CountDownLatch(1);
		Activity activity;
		Toolbar toolbar;

		@Override
		public Activity getActivity(){
			return activity;
		}

		@Override
		public Resources getResources(){
			return activity.getResources();
		}

		@Override
		public Toolbar getToolbar(){
			return toolbar;
		}

		@Override
		public String getAccountID(){
			return null;
		}

		@Override
		public void onDropdownWillDismiss(){
			willDismiss.countDown();
		}

		@Override
		public void onDropdownDismissed(){
			dismissed.countDown();
		}
	}

	private static class TestSubmenu extends DropdownSubmenuController{
		final CountDownLatch dismissed=new CountDownLatch(1);
		private final CharSequence backItemTitle;

		TestSubmenu(ToolbarDropdownMenuController dropdownController, CharSequence backItemTitle){
			super(dropdownController);
			this.backItemTitle=backItemTitle;
			items=new ArrayList<>();
		}

		@Override
		protected CharSequence getBackItemTitle(){
			return backItemTitle;
		}

		@Override
		public void onDismiss(){
			dismissed.countDown();
		}
	}
}
