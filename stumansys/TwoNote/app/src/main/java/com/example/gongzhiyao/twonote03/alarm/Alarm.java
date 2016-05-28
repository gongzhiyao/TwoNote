package com.example.gongzhiyao.twonote03.alarm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.Service.AlarmService;
import com.example.gongzhiyao.twonote03.database.MyDB;

public class Alarm extends AppCompatActivity {

    private Button btn_cancel,btn_reset_alarm;
    private MyDB db;
    private SQLiteDatabase dbread,dbwrite;
    private TextView tv_show_alarm;
    private Vibrator vibrator;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_alarm);
try {
    mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    boolean night_on = MainActivity.sp.getBoolean("night_light", false);

    if (night_on) {
        change_to_night();
    }
}catch(Exception e){

        }


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        boolean hasvib=vibrator.hasVibrator();
        long [] pattern={300,200,300,200};
        vibrator.vibrate(pattern,2);

        db=new MyDB(this);
        dbread=db.getReadableDatabase();
        dbwrite=db.getWritableDatabase();
        id=getIntent().getIntExtra("id_send",0);
//        System.out.println("传到闹钟界面的id是"+id);
        btn_cancel= (Button) findViewById(R.id.cancel_alarm);
        tv_show_alarm= (TextView) findViewById(R.id.tv_show_alarm);
        btn_reset_alarm= (Button) findViewById(R.id.btn_reset_alarm);


        String sq="select * from reminds where _id=?";
        Cursor cursor1=dbread.rawQuery(sq,new String[]{id+""});
        cursor1.moveToFirst();
        String title =cursor1.getString(cursor1.getColumnIndex("title"));
        tv_show_alarm.setText(title);
        /**
         * 设置到activity中
         */

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sq = "update reminds set is_check=? where _id=?";
                dbwrite.execSQL(sq,new String[]{"true",""+id});
                Toast.makeText(getApplicationContext(),"本次提醒已完成",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Alarm.this, AlarmService.class);
                stopService(i);
                vibrator.cancel();
                finish();


            }
        });


        btn_reset_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"请更改提醒日期",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Alarm.this, AlarmService.class);
                stopService(i);
                vibrator.cancel();
                finish();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbwrite.close();
        dbread.close();
        db.close();
        vibrator.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i=new Intent(Alarm.this, AlarmService.class);
        startService(i);
    }

    private View mNightView = null;

    private WindowManager mWindowManager;
    public void change_to_night() {


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(

                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,

                WindowManager.LayoutParams.TYPE_APPLICATION,

                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT);


        lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置

        lp.y = 10;

        if (mNightView == null) {

            mNightView = new TextView(this);
            mNightView.setBackgroundColor(0x80000000);


        }

        try {

            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    this, R.anim.aurora_menu_cover_enter);
            mNightView.startAnimation(hyperspaceJumpAnimation);

            mWindowManager.addView(mNightView, lp);


            /**
             * 标识
             */


        } catch (Exception ex) {
        }


    }


    public void change_to_day() {
        try {

            /***
             * 标识
             */


            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    this, R.anim.aurora_menu_cover_exit);
            mNightView.startAnimation(hyperspaceJumpAnimation);


            mWindowManager.removeView(mNightView);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){

            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("此日程提醒将被标记为未完成，并保存在未完成列表中！");
            dialog.setNegativeButton("标记为已完成", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String sq = "update reminds set is_check=? where _id=?";
                    dbwrite.execSQL(sq, new String[]{"true", "" + id});
                    Toast.makeText(getApplicationContext(), "本次提醒已完成", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Alarm.this, AlarmService.class);
                    stopService(i);
                    vibrator.cancel();
                    finish();
                }
            });


            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(getApplicationContext(),"请更改提醒日期",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Alarm.this, AlarmService.class);
                    stopService(i);
                    vibrator.cancel();
                    finish();
                }
            });

            dialog.create().show();
        }

        return super.onKeyDown(keyCode, event);
    }


}
