package com.example.gongzhiyao.twonote03.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.gongzhiyao.twonote03.alarm.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        /**
         * 接收到广播后所要做的事
         */
//        Toast.makeText(context,"闹铃响起",Toast.LENGTH_SHORT).show();
//        System.out.println("闹铃响起");
        /**
         * 这里需要一个服务，在服务中打开一个activity
         */
//        Intent i=new Intent(context, AlarmService.class);
//        context.startService(i);
        int id=intent.getIntExtra("id",0);
//        System.out.println("此时获得的id是"+id);
        Intent i=new Intent(context, Alarm.class);
        i.putExtra("id_send",id);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
