package de.icod.techidon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import de.icod.techidon.api.requests.accounts.GetOwnAccount;
import de.icod.techidon.api.requests.oauth.GetOauthToken;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.Account;
import de.icod.techidon.model.Application;
import de.icod.techidon.model.Instance;
import de.icod.techidon.model.Token;
import de.icod.techidon.ui.utils.UiUtils;

import androidx.annotation.Nullable;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;

@SuppressWarnings("deprecation")

public class OAuthActivity extends Activity{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		UiUtils.setUserPreferredTheme(this);
		super.onCreate(savedInstanceState);
		Uri uri=getIntent().getData();
		if(uri==null || isTaskRoot()){
			finish();
			return;
		}
		if(uri.getQueryParameter("error")!=null){
			String error=uri.getQueryParameter("error_description");
			if(TextUtils.isEmpty(error))
				error=uri.getQueryParameter("error");
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			finish();
			restartMainActivity();
			return;
		}
		String code=uri.getQueryParameter("code");
		if(TextUtils.isEmpty(code)){
			finish();
			return;
		}
		Instance instance=AccountSessionManager.getInstance().getAuthenticatingInstance();
		Application app=AccountSessionManager.getInstance().getAuthenticatingApp();
		if(instance==null || app==null){
			finish();
			return;
		}
		ProgressDialog progress=new ProgressDialog(this);
		progress.setMessage(getString(R.string.finishing_auth));
		progress.setCancelable(false);
		progress.show();
		new GetOauthToken(app.clientId, app.clientSecret, code, GetOauthToken.GrantType.AUTHORIZATION_CODE)
				.setCallback(new Callback<>(){
					@Override
					public void onSuccess(Token token){
						new GetOwnAccount()
								// in case the instance (looking at pixelfed) wants to redirect to a
								// website, we need to pass a context so we can launch a browser
								.setContext(OAuthActivity.this)
								.setCallback(new Callback<>(){
									@Override
									public void onSuccess(Account account){
										AccountSessionManager.getInstance().addAccount(instance, token, account, app, null);
										progress.dismiss();
										finish();
										// not calling restartMainActivity() here on purpose to have it recreated (notice different flags)
										Intent intent=new Intent(OAuthActivity.this, MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									}

									@Override
									public void onError(ErrorResponse error){
										handleError(error);
										progress.dismiss();
									}
								})
								.exec(instance.uri, token);
					}

					@Override
					public void onError(ErrorResponse error){
						handleError(error);
						progress.dismiss();
					}
				})
				.execNoAuth(instance.uri);
	}

	private void handleError(ErrorResponse error){
		error.showToast(OAuthActivity.this);
		finish();
		restartMainActivity();
	}

	private void restartMainActivity(){
		Intent intent=new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
