package com.example.gongzhiyao.twonote03.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.gongzhiyao.twonote03.Edit_log.Edit_Text;
import com.example.gongzhiyao.twonote03.R;

public class Service_night extends Service {
    public static NotificationManager mNotificationManager;
    private static final String TAG="Service_receiver";
    public Service_night() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Bitmap btm = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setContentTitle("TwoNote")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText("今天又是完美而充实的一天，赶快把它写到日记里吧");
        mBuilder.setTicker("美好的一天结束了");//第一次提示消息的时候显示在通知栏上
        mBuilder.setNumber(12);
        mBuilder.setLargeIcon(btm);
        Intent resultIntent = new Intent(this,Edit_Text.class);

//封装一个Intent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        resultIntent.putExtra("id",1);
// 设置通知主题的意图
        mBuilder.setContentIntent(resultPendingIntent);
//获取通知管理器对象
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

        Log.i(TAG,"系统时间到达后，另一服务被开启");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
