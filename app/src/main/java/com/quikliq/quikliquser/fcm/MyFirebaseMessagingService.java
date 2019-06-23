package com.quikliq.quikliquser.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quikliq.quikliquser.utilities.Prefs;
import com.quikliq.quikliquser.activities.HomeActivity;
import com.quikliq.quikliquser.constants.Constant;
import org.json.JSONObject;
import java.util.Map;

import static com.quikliq.quikliquser.utilities.BadgeUtils.setBadge;


@SuppressLint("Registered")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    String CHANNEL_ID = "com.tamakoshi.app";
    NotificationChannel mChannel;
    private NotificationManager mManager;
    private String title, msg, actionCode;
    private int badge = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            title = object.optString("title","");
            actionCode = object.optString("action_code", "");
            msg = object.optString("body", "");
            if (remoteMessage.getData().containsKey("badge")) {
                badge = Integer.parseInt(remoteMessage.getData().get("badge"));
                setBadge(getApplicationContext(), badge);
                Prefs.putBoolean(Constant.INSTANCE.getHAS_BADGE(),true);
            }
            if (!(title.equals("") && msg.equals("") && actionCode.equals(""))) {
                createNotification(actionCode, msg, title);
            }
        }

    }

    public void createNotification(String action_code, String msg, String title) {
        Intent intent = null;
        intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constant.INSTANCE.getACTION_CODE(), action_code);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
//                    title, NotificationManager.IMPORTANCE_DEFAULT);
//            androidChannel.enableLights(true);
//            androidChannel.enableVibration(true);
//            androidChannel.setLightColor(Color.GREEN);
//            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            getManager().createNotificationChannel(androidChannel);
//            Notification.Builder nb = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
//                    .setContentTitle(title)
//                    .setContentText(msg)
//                    .setTicker(title)
//                    .setShowWhen(true)
//                    .setSmallIcon(R.mipmap.ic_small_notification)
//                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
//                            R.mipmap.ic_launcher_round))
//                    .setAutoCancel(true)
//                    .setContentIntent(contentIntent);
//            getManager().notify(101, nb.build());

        } else {
//            try {
//                @SuppressLint({"NewApi", "LocalSuppress"}) android.support.v4.app.NotificationCompat.Builder notificationBuilder = new android.support.v4.app.NotificationCompat.Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                        .setSmallIcon(R.mipmap.ic_small_notification)
//                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
//                                R.mipmap.ic_launcher_round))
//                        .setContentTitle(title)
//                        .setTicker(title)
//                        .setContentText(msg)
//                        .setShowWhen(true)
//                        .setContentIntent(contentIntent)
//                        .setLights(0xFF760193, 300, 1000)
//                        .setAutoCancel(true).setVibrate(new long[]{200, 400});
//                        /*.setSound(Uri.parse("android.resource://"
//                                + getApplicationContext().getPackageName() + "/" + R.raw.tone));*/
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());
//            } catch (SecurityException se) {
//                se.printStackTrace();
//            }
        }
    }


    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }


}