package de.icod.techidon.test;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.Toolbar;

import de.icod.techidon.ui.viewcontrollers.ToolbarDropdownMenuController;

import java.util.concurrent.CountDownLatch;

class TestDropdownHost implements ToolbarDropdownMenuController.HostFragment{
	final CountDownLatch willDismiss=new CountDownLatch(1);
	final CountDownLatch dismissed=new CountDownLatch(1);
	private final Activity activity;
	private final Toolbar toolbar;

	TestDropdownHost(Activity activity){
		this.activity=activity;
		toolbar=new Toolbar(activity);
	}

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
