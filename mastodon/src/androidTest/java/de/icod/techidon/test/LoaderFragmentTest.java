package de.icod.techidon.test;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.icod.techidon.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.fragments.LoaderFragment;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoaderFragmentTest{
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule=new ActivityScenarioRule<>(MainActivity.class);

	@Test
	public void errorIsNotRetriedWhileNetworkStaysTheSame(){
		FailingLoaderFragment fragment=new FailingLoaderFragment();
		activityScenarioRule.getScenario().onActivity(activity->activity.showFragment(fragment));

		// After an error, the fragment listens for the network coming back. The current network is reported
		// as soon as it starts listening, which must not trigger a retry, or a persistent error would loop.
		SystemClock.sleep(3000);

		assertEquals(1, fragment.loadCount.get());
	}

	public static class FailingLoaderFragment extends LoaderFragment{
		final AtomicInteger loadCount=new AtomicInteger();

		@Override
		public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			return new View(getActivity());
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState){
			super.onViewCreated(view, savedInstanceState);
			loadData();
		}

		@Override
		protected void doLoadData(){
			loadCount.incrementAndGet();
			onError(new ErrorResponse(){
				@Override
				public void bindErrorView(View view){}

				@Override
				public void showToast(Context context){}
			});
		}

		@Override
		public void onRefresh(){}
	}
}
