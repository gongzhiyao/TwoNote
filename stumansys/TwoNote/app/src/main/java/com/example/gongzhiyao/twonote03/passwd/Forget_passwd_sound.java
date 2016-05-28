package com.example.gongzhiyao.twonote03.passwd;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.Sensors.ShakeHelper;

import java.util.Timer;
import java.util.TimerTask;

public class Forget_passwd_sound extends AppCompatActivity implements View.OnClickListener {
    TextView tv_count,tv_need_count;
    Button btn_help;
    ShakeHelper helper;
    int need_count;
    Timer timer;
    String passwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd_sound);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);

        if(night_on){
            change_to_night();
        }


        tv_count= (TextView) findViewById(R.id.shake_count_for_soundPasswd);
        tv_need_count= (TextView) findViewById(R.id.shake_need_count_soundPasswd);
        btn_help= (Button) findViewById(R.id.shake_help_soundPasswd);
        btn_help.setOnClickListener(this);
        need_count=MainActivity.sp.getInt("need_count_passwd_note",100);
        tv_need_count.setText("本次需要" + need_count + "下");
        passwd=MainActivity.sp.getString("passwd_for_sound",null);
//        System.out.println("得到的密码是" + passwd);
        helper=new ShakeHelper(this);
        timer=new Timer( );
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);

            }


        },0,200);








    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int messageId=msg.what;
            switch (messageId){

                case 1:
                    int i=helper.getCount();
                    int real= (int) (i/2.545);
                    tv_count.setText(""+real);
                    if(real==need_count) {
                        tv_count.setText("密码是\n"+passwd);
                        need_count=need_count+50;
                        if(need_count>=500){
                            need_count=500;
                        }
                        MainActivity.sp.edit().putInt("need_count_passwd_note",need_count).commit();
                        timer.cancel();
                    }
                    break;
                case 2:

                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forget_passwd_sound, menu);
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
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.shake_help_soundPasswd:
                String warn="1.第一次忘记密码需要摇晃100次\n2.以后每次忘记密码都会增加50次\n3.直到达到最高次数500次";
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("帮助")
                        .setMessage(warn).setPositiveButton("确定",null);
                dialog.create().show();

                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.Stop();


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

}
