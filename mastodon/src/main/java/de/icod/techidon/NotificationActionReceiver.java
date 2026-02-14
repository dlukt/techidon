package de.icod.techidon;

import static de.icod.techidon.GlobalUserPreferences.PrefixRepliesMode.ALWAYS;
import static de.icod.techidon.GlobalUserPreferences.PrefixRepliesMode.TO_OTHERS;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import de.icod.techidon.api.requests.accounts.SetAccountFollowed;
import de.icod.techidon.api.requests.statuses.CreateStatus;
import de.icod.techidon.api.requests.statuses.SetStatusBookmarked;
import de.icod.techidon.api.requests.statuses.SetStatusFavorited;
import de.icod.techidon.api.requests.statuses.SetStatusReblogged;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.Mention;
import de.icod.techidon.model.NotificationAction;
import de.icod.techidon.model.Preferences;
import de.icod.techidon.model.Status;
import de.icod.techidon.model.StatusPrivacy;
import de.icod.techidon.ui.utils.UiUtils;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.UUID;

import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;

@SuppressWarnings("deprecation")
public class NotificationActionReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationActionReceiver";
    public static final String ACTION_KEY_TEXT_REPLY = "ACTION_KEY_TEXT_REPLY";

    @Override
    public void onReceive(Context context, Intent intent) {
        UiUtils.setUserPreferredTheme(context);
        if (intent.getBooleanExtra("fromNotificationAction", false)) {
            String accountID = intent.getStringExtra("accountID");
            int notificationId = intent.getIntExtra("notificationId", -1);

            if (notificationId >= 0) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(accountID, notificationId);
            }

            if (intent.hasExtra("notification")) {
                de.icod.techidon.model.Notification notification = Parcels.unwrap(intent.getParcelableExtra("notification"));

                String statusID = null;
                if (notification != null && notification.status != null)
                    statusID = notification.status.id;

                if (statusID != null) {
                    AccountSessionManager accountSessionManager = AccountSessionManager.getInstance();
                    Preferences preferences = accountSessionManager.getAccount(accountID).preferences;

                    switch (NotificationAction.values()[intent.getIntExtra("notificationAction", 0)]) {
                        case FAVORITE -> new SetStatusFavorited(statusID, true).exec(accountID);
                        case BOOKMARK -> new SetStatusBookmarked(statusID, true).exec(accountID);
                        case BOOST -> new SetStatusReblogged(notification.status.id, true, preferences.postingDefaultVisibility).exec(accountID);
                        case UNBOOST -> new SetStatusReblogged(notification.status.id, false, preferences.postingDefaultVisibility).exec(accountID);
                        case REPLY -> handleReplyAction(context, accountID, intent, notification, notificationId, preferences);
                        case FOLLOW_BACK -> new SetAccountFollowed(notification.account.id, true, true, false).exec(accountID);
                        default -> {
                            if (BuildConfig.DEBUG)
                                Log.w(TAG, "onReceive: Failed to get NotificationAction");
                        }
                    }
                }
            } else {
                if (BuildConfig.DEBUG)
                    Log.e(TAG, "onReceive: Failed to load notification");
            }
        }
    }

    private void handleReplyAction(Context context, String accountID, Intent intent, de.icod.techidon.model.Notification notification, int notificationId, Preferences preferences) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput == null) {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "handleReplyAction: Could not get reply input");
            return;
        }
        CharSequence input = remoteInput.getCharSequence(ACTION_KEY_TEXT_REPLY);

        // copied from ComposeFragment - TODO: generalize?
        ArrayList<String> mentions = new ArrayList<>();
        Status status = notification.status;
        String ownID = AccountSessionManager.getInstance().getAccount(accountID).self.id;
        if (!status.account.id.equals(ownID))
            mentions.add('@' + status.account.acct);
        for (Mention mention : status.mentions) {
            if (mention.id.equals(ownID))
                continue;
            String m = '@' + mention.acct;
            if (!mentions.contains(m))
                mentions.add(m);
        }
        String initialText = mentions.isEmpty() ? "" : TextUtils.join(" ", mentions) + " ";

        CreateStatus.Request req = new CreateStatus.Request();
        req.status = initialText + input.toString();
        req.language = notification.status.language;
        req.visibility = (notification.status.visibility == StatusPrivacy.PUBLIC && GlobalUserPreferences.defaultToUnlistedReplies ? StatusPrivacy.UNLISTED : notification.status.visibility);
        req.inReplyToId = notification.status.id;

        if (notification.status.hasSpoiler() &&
                (GlobalUserPreferences.prefixReplies == ALWAYS
                        || (GlobalUserPreferences.prefixReplies == TO_OTHERS && !ownID.equals(notification.status.account.id)))
                && !notification.status.spoilerText.startsWith("re: ")) {
            req.spoilerText = "re: " + notification.status.spoilerText;
        }

        new CreateStatus(req, UUID.randomUUID().toString()).setCallback(new Callback<>() {
            @Override
            public void onSuccess(Status status) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        new Notification.Builder(context, accountID + "_" + notification.type) :
                        new Notification.Builder(context)
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notification.status = status;
                Intent contentIntent = new Intent(context, MainActivity.class);
                contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                contentIntent.putExtra("fromNotification", true);
                contentIntent.putExtra("accountID", accountID);
                contentIntent.putExtra("notification", Parcels.wrap(notification));

                Notification repliedNotification = builder.setSmallIcon(R.drawable.ic_ntf_logo)
                        .setContentTitle(context.getString(R.string.sk_notification_action_replied, notification.status.account.displayName))
                        .setContentText(status.getStrippedText())
                        .setCategory(Notification.CATEGORY_SOCIAL)
                        .setContentIntent(PendingIntent.getActivity(context, notificationId, contentIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT))
                        .build();
                notificationManager.notify(accountID, notificationId, repliedNotification);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {

            }
        }).exec(accountID);
    }
}
