package com.ffi.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ffi.App;
import com.ffi.HomeActivity;
import com.ffi.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String cataloguename = "";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "", message = "", notificationType = "";
        String id, image;
        Log.e(TAG, remoteMessage.getData().toString() + "");
        Log.e(TAG, "onMessageReceived called");

  /*      title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("text");
*/


        try {
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Message data payload: " + remoteMessage.getData());
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("body");
                if (message != null && TextUtils.isEmpty(message)) {
                    title = remoteMessage.getData().get("title");
                    message = remoteMessage.getData().get("text");
                }
            } else {
                if (remoteMessage.getNotification() != null) {
                    Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    title = remoteMessage.getNotification().getTitle();
                    message = remoteMessage.getNotification().getBody();
                }
            }
        } catch (Exception e) {
        }

        try {
            notificationType = remoteMessage.getData().get("notificationType");

            if (notificationType != null) {

                if (notificationType.equals(Const.NOTI_NEW_CATALOGUE) || notificationType.equals(Const.NOTI_NEW_PRODUCT)) {
                    id = remoteMessage.getData().get("id");
                    image = remoteMessage.getData().get("image");
                    if (notificationType.equals(Const.NOTI_NEW_CATALOGUE)) {
                        cataloguename = remoteMessage.getData().get("Catalogname");
                    }
                    sendNotification(title, message, notificationType, id, image);
                } else {
                    sendNotification(title, message, notificationType, "", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void sendNotification(String title, String messageBody, String notificationType, String id, String image) {
        int uniquenotificationid = App.notificationid;
        Bitmap bitmap = null;

        if (notificationType.equals(Const.NOTI_NEW_CATALOGUE) || notificationType.equals(Const.NOTI_NEW_PRODUCT)) {
            if (image != null && !image.isEmpty()) {
                bitmap = getBitmapFromURL(image);
            }
        }

        Uri defaultSound;

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        if (notificationType.equals(Const.NOTI_ORDER)) {
            intent.putExtra(Const.ORDER_ID, "1");
        } else if (notificationType.equals(Const.NOTI_USER_REGISTER)) {
            intent.putExtra(Const.NOTI_USER_REGISTER, true);
        } else if (notificationType.equals(Const.NOTI_ADD_WALET)) {
            intent.putExtra(Const.NOTI_ADD_WALET, true);
        } else if (notificationType.equals(Const.NOTI_NEW_PRODUCT)) {
            intent.putExtra(Const.NOTI_NEW_PRODUCT, id);
        } else if (notificationType.equals(Const.NOTI_NEW_CATALOGUE)) {
            Log.e("notification",id +"--" + cataloguename + "line no 116");
            intent.putExtra(Const.NOTI_NEW_CATALOGUE, id);
            intent.putExtra(Const.TITLE, cataloguename);
        } else {
            intent.putExtra(Const.NOTI_DEFUALT, true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), iUniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            defaultSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/oreo");
            notificationChannel.setDescription("Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(defaultSound, attributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = null;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ffi)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setColor(this.getResources().getColor(R.color.color_white))
                        .setContentText(messageBody)
                        .setContentIntent(pendingIntent)
                        .setCustomBigContentView(remoteView(messageBody, bitmap, title))
                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_DEFAULT);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                 notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ffi)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(messageBody)
                        .setContentIntent(pendingIntent)
                        .setCustomBigContentView(remoteView(messageBody, bitmap, title))
                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_DEFAULT);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //doStuff();
                break;
        }


        if (notificationType.equals(Const.NOTI_NEW_CATALOGUE) || notificationType.equals(Const.NOTI_NEW_PRODUCT)) {
            if (image != null && !image.isEmpty()) {
                notificationBuilder.setLargeIcon(bitmap);
            }
        }

        notificationManager.notify(uniquenotificationid, notificationBuilder.build());
        uniquenotificationid++;
        App.notificationid = uniquenotificationid;
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private RemoteViews remoteView(String message, Bitmap bitmap, String title) {
        RemoteViews views;
        FirebaseApp.initializeApp(this);

        views = new RemoteViews(getPackageName(), R.layout.layout_notification);
        views.setImageViewBitmap(R.id.ivmainimg, bitmap);
        views.setImageViewBitmap(R.id.ivlauncher, BitmapFactory.decodeResource(getResources(), R.drawable.ffi));
        views.setTextViewText(R.id.text, message);
        views.setTextViewText(R.id.title, title);
        return views;
    }
}
