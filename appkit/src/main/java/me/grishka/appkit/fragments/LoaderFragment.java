package me.grishka.appkit.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.LayoutRes;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.grishka.appkit.R;
import me.grishka.appkit.api.APIRequest;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.utils.V;

/**
 * Created by grishka on 11.06.15.
 */
public abstract class LoaderFragment extends AppKitFragment implements SwipeRefreshLayout.OnRefreshListener {

	private int layoutID;
	protected View errorView;
	protected View progress;
	protected View content;
	protected ViewGroup contentView;
	public boolean loaded;
	public boolean dataLoading;
	protected APIRequest currentRequest;

	private ConnectivityManager connectivityManager;
	private ConnectivityManager.NetworkCallback networkCallback;
	private boolean networkCallbackRegistered=false;

	private final Runnable networkRetryRunnable=()->{
		if(isAdded())
			onErrorRetryClick();
	};

	private ConnectivityManager.NetworkCallback getNetworkCallback(){
		if(networkCallback==null){
			networkCallback=new ConnectivityManager.NetworkCallback(){
				@Override
				public void onAvailable(Network network){
					if(getActivity()!=null)
						getActivity().runOnUiThread(networkRetryRunnable);
				}
			};
		}
		return networkCallback;
	}

	private void registerNetworkCallback(){
		if(networkCallbackRegistered || !autoRetry || getActivity()==null)
			return;
		if(connectivityManager==null)
			connectivityManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager==null)
			return;
		try{
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
				connectivityManager.registerDefaultNetworkCallback(getNetworkCallback());
			}else{
				NetworkRequest request=new NetworkRequest.Builder()
						.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
						.build();
				connectivityManager.registerNetworkCallback(request, getNetworkCallback());
			}
			networkCallbackRegistered=true;
		}catch(Exception ignore){}
	}

	private void unregisterNetworkCallback(){
		if(!networkCallbackRegistered || connectivityManager==null || networkCallback==null)
			return;
		try{
			connectivityManager.unregisterNetworkCallback(networkCallback);
		}catch(Exception ignore){}
		networkCallbackRegistered=false;
	}
	private boolean autoRetry=true;

	public LoaderFragment(){
		this(R.layout.loader_fragment);
	}

	protected LoaderFragment(@LayoutRes int layout){
		layoutID=layout;
	}

	@LayoutRes
	protected int getLayout() {
		return layoutID;
	}

	protected void setLayout(int id){
		if(content!=null)
			throw new IllegalStateException("Can't set layout when view is already created");
		layoutID=id;
	}

	public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	protected abstract void doLoadData();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView= (ViewGroup) inflater.inflate(layoutID, null);
		View stub=contentView.findViewById(R.id.content_stub);
		ViewGroup stubParent=(ViewGroup)stub.getParent();
		content=onCreateContentView(inflater, stubParent, savedInstanceState);
		content.setLayoutParams(stub.getLayoutParams());
		stubParent.addView(content, stubParent.indexOfChild(stub));
		stubParent.removeView(stub);
		progress=contentView.findViewById(R.id.loading);
		errorView=contentView.findViewById(R.id.error);
		if(errorView instanceof ViewStub){
			errorView=((ViewStub) errorView).inflate();
			errorView.setVisibility(View.GONE);
		}
		content.setVisibility(loaded ? View.VISIBLE : View.GONE);
		progress.setVisibility(loaded ? View.GONE : View.VISIBLE);

		View retryBtn=errorView.findViewById(R.id.error_retry);
		if(retryBtn!=null){
			retryBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onErrorRetryClick();
				}
			});
		}

		return contentView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		content=null;
		errorView=null;
		progress=null;
		contentView=null;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(currentRequest!=null){
			currentRequest.cancel();
			currentRequest=null;
		}
		unregisterNetworkCallback();
	}

	protected void onErrorRetryClick(){
		V.setVisibilityAnimated(errorView, View.GONE);
		V.setVisibilityAnimated(progress, View.VISIBLE);
		loadData();
	}

	public void loadData(){
		showProgress();
		dataLoading=true;
		doLoadData();
	}

	public void dataLoaded(){
		loaded=true;
		showContent();
	}

	protected void showContent(){
		if(content!=null){
			V.setVisibilityAnimated(content, View.VISIBLE);
			V.setVisibilityAnimated(errorView, View.GONE);
			V.setVisibilityAnimated(progress, View.GONE);
		}
		if(networkCallbackRegistered)
			unregisterNetworkCallback();
	}

	protected void showProgress(){
		if(content!=null){
			V.setVisibilityAnimated(content, View.GONE);
			V.setVisibilityAnimated(errorView, View.GONE);
			V.setVisibilityAnimated(progress, View.VISIBLE);
		}
		if(networkCallbackRegistered)
			unregisterNetworkCallback();
	}

	public void onError(ErrorResponse error){
		dataLoading=false;
		currentRequest=null;
		if(errorView==null) return;
		error.bindErrorView(errorView);
		V.setVisibilityAnimated(errorView, View.VISIBLE);
		V.setVisibilityAnimated(progress, View.GONE);
		V.setVisibilityAnimated(content, View.GONE);
		registerNetworkCallback();
	}

	public void setRetryOnNetworkConnect(boolean retry){
		autoRetry=retry;
	}

	public boolean isRetryInNetworkConnect(){
		return autoRetry;
	}
}
