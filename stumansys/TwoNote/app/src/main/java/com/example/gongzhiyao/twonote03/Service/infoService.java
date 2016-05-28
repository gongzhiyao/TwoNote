package com.example.gongzhiyao.twonote03.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.gongzhiyao.twonote03.Receiver.infoReceiver;

public class infoService extends Service {
    private static final String TAG="Service_info";

    public infoService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
//        Log.i(TAG,"服务已启动");
        IntentFilter filter=new IntentFilter();
        infoReceiver receiver=new infoReceiver();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);

    }





    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i(TAG,"服务已关闭");
    }
}
