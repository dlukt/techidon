package de.icod.techidon.test;

import android.app.Instrumentation;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;

import de.icod.techidon.MainActivity;
import de.icod.techidon.model.Attachment;
import de.icod.techidon.ui.photoviewer.PhotoViewer;
import de.icod.techidon.ui.viewcontrollers.ToolbarDropdownMenuController;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
		TestDropdownHost[] host={null};
		TestDropdownSubmenu[] submenu={null};
		waitForActivityWindowFocus(true);
		activityScenarioRule.getScenario().onActivity(activity->{
			host[0]=new TestDropdownHost(activity);
			ToolbarDropdownMenuController controller=new ToolbarDropdownMenuController(host[0]);
			controller.show(new TestDropdownSubmenu(controller, null));
			submenu[0]=new TestDropdownSubmenu(controller, "Back");
			controller.pushSubmenuController(submenu[0]);
		});

		pressBackInOverlayWindow();
		assertTrue("Submenu wasn't popped on back", submenu[0].dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
		assertEquals(1, host[0].willDismiss.getCount());

		instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		assertTrue("Dropdown wasn't dismissed on back", host[0].dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
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
}
