package com.example.gongzhiyao.twonote03.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gongzhiyao.twonote03.Service.Service_night;
import com.example.gongzhiyao.twonote03.Service.Service_receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class infoReceiver extends BroadcastReceiver {
    private static final String TAG="Receiver_info";
    private  Intent i;
    public infoReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        Log.i(TAG,"监听器已启动");
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)) {
            SimpleDateFormat format=new SimpleDateFormat("HH:mm");//HH为24小时制，hh为12小时制
            String time=format.format(new Date()).toString();
            Log.i(TAG, "此时的系统时间是" + time);

            if(time.equals("08:00")){

                i=new Intent(context, Service_receiver.class);
                Log.i(TAG,"设定时间到达");
                context.startService(i);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(3000);
                            context.stopService(i);
//                            Log.i(TAG,"ServiceReciver已经关闭");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
            else if(time.equals("20:30")){
                i=new Intent(context, Service_night.class);
//                Log.i(TAG,"设定晚间时间到达");
                context.startService(i);
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(3000);
                            context.stopService(i);
//                            Log.i(TAG,"ServiceReciver已经关闭");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }

    }
}
