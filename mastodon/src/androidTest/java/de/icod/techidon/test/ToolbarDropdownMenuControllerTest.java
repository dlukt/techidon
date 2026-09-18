package de.icod.techidon.test;

import android.os.SystemClock;

import de.icod.techidon.MainActivity;
import de.icod.techidon.ui.viewcontrollers.ToolbarDropdownMenuController;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ToolbarDropdownMenuControllerTest{
	private static final long TIMEOUT_MS=5000;

	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule=new ActivityScenarioRule<>(MainActivity.class);

	private TestDropdownHost host;
	private ToolbarDropdownMenuController controller;
	private TestDropdownSubmenu submenu;

	@Test
	public void dismissingDuringSubmenuTransitionDoesNotCrash() throws InterruptedException{
		showDropdownWithSubmenu();
		// Let the push transition finish
		SystemClock.sleep(500);

		activityScenarioRule.getScenario().onActivity(activity->{
			controller.popSubmenuController();
			controller.dismiss();
		});
		assertTrue(host.dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));

		// The pop transition outlasts the dismiss animation. Its end listener used to run after the views were gone and crash the app.
		SystemClock.sleep(500);
		assertEquals(Lifecycle.State.RESUMED, activityScenarioRule.getScenario().getState());
	}

	@Test
	public void dismissingDropdownDismissesOpenSubmenus() throws InterruptedException{
		showDropdownWithSubmenu();

		activityScenarioRule.getScenario().onActivity(activity->controller.dismiss());

		// Submenus cancel their pending requests in onDismiss(). Otherwise a failed request pops the submenu after the dropdown is gone.
		assertTrue("Open submenu wasn't dismissed with the dropdown", submenu.dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
		assertTrue(host.dismissed.await(TIMEOUT_MS, TimeUnit.MILLISECONDS));
	}

	private void showDropdownWithSubmenu(){
		activityScenarioRule.getScenario().onActivity(activity->{
			host=new TestDropdownHost(activity);
			controller=new ToolbarDropdownMenuController(host);
			controller.show(new TestDropdownSubmenu(controller, null));
			submenu=new TestDropdownSubmenu(controller, "Back");
			controller.pushSubmenuController(submenu);
		});
	}
}
