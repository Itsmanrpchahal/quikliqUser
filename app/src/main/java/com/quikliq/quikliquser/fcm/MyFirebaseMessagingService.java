package com.quikliq.quikliquser.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quikliq.quikliquser.R;
import com.quikliq.quikliquser.activities.AboutActivity;
import com.quikliq.quikliquser.activities.CartActivity;
import com.quikliq.quikliquser.activities.ChangePasswordActivity;
import com.quikliq.quikliquser.fragment.ProfileFragment;
import com.quikliq.quikliquser.utilities.Prefs;
import com.quikliq.quikliquser.activities.HomeActivity;
import com.quikliq.quikliquser.constants.Constant;
import org.json.JSONObject;
import java.util.Map;

import static com.quikliq.quikliquser.utilities.BadgeUtils.setBadge;


@SuppressLint("Registered")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    String CHANNEL_ID = "com.quikliq.quikliquser";
    NotificationChannel mChannel;
    private NotificationManager mManager;
    private String title, msg, actionCode,click;
    private int badge = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("noti",remoteMessage.getData().toString());
        if (remoteMessage.getData() != null) {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            title = object.optString("title","");
            actionCode = object.optString("action_code", "");
            msg = object.optString("body", "");
            click = object.optString("click","");
            if (!(title.equals("") && msg.equals("") && actionCode.equals(""))) {
                createNotification(actionCode, msg, title,click);
            }

            Log.d("notification",title);
        }

    }

    public void createNotification(String action_code, String msg, String title,String click) {
        Intent intent = null;
        Log.d("notify1",click);
        if (click.equals("home"))
        {
            Log.d("check","HERE");
            intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra(Constant.INSTANCE.getACTION_CODE(), action_code);
        }else {
            Log.d("check","NO");
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Constant.INSTANCE.getACTION_CODE(), action_code);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                    title, NotificationManager.IMPORTANCE_DEFAULT);
            androidChannel.enableLights(true);
            androidChannel.enableVibration(true);
            androidChannel.setLightColor(Color.GREEN);
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(androidChannel);
            Notification.Builder nb = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(title)
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);
            getManager().notify(101, nb.build());
            Log.d("notify",title);

        } else {
            try {
                @SuppressLint({"NewApi", "LocalSuppress"}) NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.mipmap.ic_launcher_round))
                        .setContentTitle(title)
                        .setTicker(title)
                        .setContentText(msg)
                        .setShowWhen(true)
                        .setContentIntent(contentIntent)
                        .setLights(0xFF760193, 300, 1000)
                        .setAutoCancel(true).setVibrate(new long[]{200, 400});
                        /*.setSound(Uri.parse("android.resource://"
                                + getApplicationContext().getPackageName() + "/" + R.raw.tone));*/
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());


            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }


    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}