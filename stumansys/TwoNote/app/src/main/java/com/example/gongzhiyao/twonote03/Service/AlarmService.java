package com.example.gongzhiyao.twonote03.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import com.example.gongzhiyao.twonote03.R;

public class AlarmService extends Service {
    MediaPlayer mediaPlayer;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        System.out.println("服务已启动");
        mediaPlayer=new MediaPlayer();
        /**
         * 初始化音频文件
         */
        mediaPlayer=MediaPlayer.create(this, R.raw.alarm);
        /**
         * 设置为循环播放
         */
        mediaPlayer.setLooping(true);


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.release();

    }
}
