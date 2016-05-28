package com.example.gongzhiyao.twonote03.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.passwd.Cancel_passwd_sound;
import com.example.gongzhiyao.twonote03.passwd.Change_passwd_sound;
import com.example.gongzhiyao.twonote03.passwd.Forget_passwd_sound;
import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.passwd.Set_passwd_sound;


public class Setting_safe extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch switch_for_passwd_sound;
    private Button btn_change_passwd,btn_forget_passwd_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_safe);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);
        if (night_on) {
            change_to_night();
        }


        btn_change_passwd= (Button) findViewById(R.id.next_passwd_for_sound);
        btn_forget_passwd_sound= (Button) findViewById(R.id.forget_passwd_sound);
        toolbar = (Toolbar) findViewById(R.id.toolbar_Setting_safe);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        switch_for_passwd_sound = (Switch) findViewById(R.id.switch_passwd_for_sound);
        boolean isOpen_for_passed=MainActivity.sp.getBoolean("need_passwd_sound",false);
        if(isOpen_for_passed){
            switch_for_passwd_sound.setChecked(true);
        }else{
            switch_for_passwd_sound.setChecked(false);
        }

        switch_for_passwd_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /**
                     *即将进入设置密码界面
                     */

                    String passwd=MainActivity.sp.getString("passwd_for_sound",null);
                    if(passwd!=null){
                        MainActivity.sp.edit().putBoolean("need_passwd_sound",true).commit();
                        Toast.makeText(getApplicationContext(),"密码保护已打开",Toast.LENGTH_SHORT).show();
                    }else {

                        Intent i = new Intent(Setting_safe.this, Set_passwd_sound.class);
                        i.putExtra("op", 1);
                        startActivity(i);
                    }




                } else {
                    /**
                     * 即将进入取消密码界面
                     */

                    Intent i=new Intent(Setting_safe.this,Cancel_passwd_sound.class);
                    startActivity(i);

                    /**
                     * 在成功取消之后才能写到sp中
                     */


                }
            }
        });



        btn_change_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean need_passwd = MainActivity.sp.getBoolean("need_passwd_sound", false);
                if (need_passwd) {

                    startActivity(new Intent(Setting_safe.this, Change_passwd_sound.class));
                } else {
                    Toast.makeText(getApplicationContext(), "密码保护未开启", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_forget_passwd_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean need_passwd = MainActivity.sp.getBoolean("need_passwd_sound", false);

                if(need_passwd){

                    startActivity(new Intent(Setting_safe.this,Forget_passwd_sound.class));

                }else {
                    Toast.makeText(getApplicationContext(),"密码保护没有开启",Toast.LENGTH_SHORT).show();
                }
            }
        });





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
    protected void onResume() {
        super.onResume();
        boolean isOpen_for_passed=MainActivity.sp.getBoolean("need_passwd_sound",false);
        if(isOpen_for_passed){
            switch_for_passwd_sound.setChecked(true);
        }else{
            switch_for_passwd_sound.setChecked(false);
        }
    }
}
