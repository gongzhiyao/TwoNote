package com.example.gongzhiyao.twonote03.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gongzhiyao.twonote03.Service.Service_receiver;
import com.example.gongzhiyao.twonote03.Service.infoService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG="bootReceiver";
    public BootReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        final Intent i=new Intent(context, Service_receiver.class);
//        Log.i(TAG, "设定时间到达");
//        context.startService(i);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    sleep(3000);
//                    context.stopService(i);
//                    Log.i(TAG,"ServiceReciver已经关闭");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
        Intent i=new Intent(context,infoService.class);
        context.startService(i);
    }
}
