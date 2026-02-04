package de.icod.techidon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import de.icod.techidon.api.requests.oauth.RevokeOauthToken;
import de.icod.techidon.api.session.AccountSession;
import de.icod.techidon.api.session.AccountSessionManager;

import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;


public class PanicResponderActivity extends Activity {
    public static final String PANIC_TRIGGER_ACTION = "info.guardianproject.panic.action.TRIGGER";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        if (intent != null && PANIC_TRIGGER_ACTION.equals(intent.getAction())) {
            AccountSessionManager.getInstance().getLoggedInAccounts().forEach(accountSession -> logOut(accountSession.getID()));
            ExitActivity.exit(this);
        }
        finishAndRemoveTask();
    }

    private void logOut(String accountID){
        AccountSession session=AccountSessionManager.getInstance().getAccount(accountID);
        new RevokeOauthToken(session.app.clientId, session.app.clientSecret, session.token.accessToken)
                .setCallback(new Callback<>(){
                    @Override
                    public void onSuccess(Object result){
                        onLoggedOut(accountID);
                    }

                    @Override
                    public void onError(ErrorResponse error){
                        onLoggedOut(accountID);
                    }
                })
                .exec(accountID);
    }

    private void onLoggedOut(String accountID){
        AccountSessionManager.getInstance().removeAccount(accountID);
    }
}