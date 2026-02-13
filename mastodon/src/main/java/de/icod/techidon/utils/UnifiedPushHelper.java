package de.icod.techidon.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import de.icod.techidon.api.session.AccountSession;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.PushSubscription;
import org.unifiedpush.android.connector.UnifiedPush;

public class UnifiedPushHelper {

	/**
	 * @param context
	 * @return `true` if UnifiedPush is used
	 */
	public static boolean isUnifiedPushEnabled(@NonNull Context context) {
		return UnifiedPush.getAckDistributor(context) != null;
	}

	/**
	 * If any distributor is installed on the device
	 * @param context
	 * @return `true` if at least one is installed
	 */
	public static boolean hasAnyDistributorInstalled(@NonNull Context context) {
		return !UnifiedPush.getDistributors(context).isEmpty();
	}

	public static void registerAllAccounts(@NonNull Context context) {
		for (AccountSession accountSession : AccountSessionManager.getInstance().getLoggedInAccounts()){
			// 🛡️ Sentinel: Generate random token for UnifiedPush instance ID to prevent spoofing
			// This prevents attackers from guessing the instance ID (which was previously predictable)
			// and sending fake push messages to the app.
			if (accountSession.unifiedPushToken == null) {
				// Unregister legacy registration that used getID()
				UnifiedPush.unregister(context, accountSession.getID());
				accountSession.unifiedPushToken = java.util.UUID.randomUUID().toString();
				AccountSessionManager.getInstance().writeAccountsFile();
			}

			String vapidKey = accountSession.app.vapidKey;
			// Sometimes this is null when the account's server has died (don't ask me how I know this)
			if (vapidKey == null) {
				// TODO: throw this on a translatable string and tell the user to log out and back in
				Toast.makeText(context, "Error on unified push subscription: no valid vapid key for account " + accountSession.getFullUsername(), Toast.LENGTH_LONG).show();
				break;
			}
			PushSubscription sub = accountSession.pushSubscription;
			if (sub == null || sub.standard) {
				vapidKey = vapidKey.replaceAll("=","");
			} else {
				// If we know the server doesn't support the _standard_ VAPID,
				// we register without vapid
				vapidKey = null;
			}
			UnifiedPush.register(
					context,
					accountSession.unifiedPushToken,
					accountSession.self.fqn,
					vapidKey
			);
		}
	}

	public static void unregisterAllAccounts(@NonNull Context context) {
		for (AccountSession accountSession : AccountSessionManager.getInstance().getLoggedInAccounts()){
			UnifiedPush.unregister(
				context,
				accountSession.unifiedPushToken != null ? accountSession.unifiedPushToken : accountSession.getID()
			);
			// use FCM again
			accountSession.getPushSubscriptionManager().registerAccountForPush(null);
		}
	}
}
