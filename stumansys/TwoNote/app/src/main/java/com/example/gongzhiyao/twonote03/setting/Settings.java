package com.example.gongzhiyao.twonote03.setting;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Button btn_icon,setting_info,setting_safe,setting_help,setting_feedBack,setting_about;
    TextView tv_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);
        if (night_on) {
            change_to_night();
        }


        /**
         * 引用
         */
        toolbar = (Toolbar) findViewById(R.id.toolbar_Settings);
        btn_icon= (Button) findViewById(R.id.btn_icon);
        tv_name= (TextView) findViewById(R.id.tv_name);
        setting_info= (Button) findViewById(R.id.setting_info);
        setting_safe= (Button) findViewById(R.id.setting_safe);
        setting_help= (Button) findViewById(R.id.setting_help);
        setting_feedBack= (Button) findViewById(R.id.setting_feedBack);
        setting_about= (Button) findViewById(R.id.setting_about);
//        iv= (ImageView) findViewById(R.id.iv);
        /**
         * 监听器
         */
        btn_icon.setOnClickListener(this);
        setting_info.setOnClickListener(this);
        setting_safe.setOnClickListener(this);
        setting_help.setOnClickListener(this);
        setting_feedBack.setOnClickListener(this);
        setting_about.setOnClickListener(this);

//        refreshheadicon();



        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void refreshheadicon() {

        Drawable d=Drawable.createFromPath("/sdcard/TwoNote/headicon/"  + "head_icon.png");

        btn_icon.setBackground(d);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_info:
            case R.id.btn_icon:
                startActivity(new Intent(Settings.this, Set_hearderIcon.class));
//                Toast.makeText(getApplicationContext(),"点击了头像",Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_safe:
                startActivity(new Intent(Settings.this,Setting_safe.class));
                break;

            case R.id.setting_help:
                startActivity(new Intent(Settings.this,Setting_help.class));
                break;
            case R.id.setting_feedBack:
                startActivity(new Intent(Settings.this,Setting_feedBack.class));
                break;
            case R.id.setting_about:
                startActivity(new Intent(Settings.this,Setting_about.class));
                break;

        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        /**
         * 在pause中finish 然后再后面start
         *
         * 或是在start 的时候finish
         */

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("被调用");
        String name= MainActivity.sp.getString("name_changed","TwoNote");
        tv_name.setText(name);
        refreshheadicon();
        refreshheadicon();
    }
}
