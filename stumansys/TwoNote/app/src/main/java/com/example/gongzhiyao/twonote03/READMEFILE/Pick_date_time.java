//package com.example.gongzhiyao.twonote03;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.graphics.PixelFormat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class Pick_date_time extends AppCompatActivity {
//
//    Toolbar toolbar;
//    Button btn_start_set;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pick_date_time);
//        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        boolean night_on = MainActivity.sp.getBoolean("night_light", false);
//
//        if (night_on) {
//            change_to_night();
//        }
//
//        toolbar= (Toolbar) findViewById(R.id.toolbar_Settings_date);
//
//
//
//        setSupportActionBar(toolbar);
////        getSupportActionBar().setHomeButtonEnabled(true);
////        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);//设置活动名不显示
//
//        btn_start_set= (Button) findViewById(R.id.btn_start_set);
//        btn_start_set.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog=new AlertDialog.Builder(Pick_date_time.this);
//                dialog.setTitle("设置日期和时间");
//                dialog.setView(R.id.);
//            }
//        });
//
//
//
//
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        System.out.println("onStart");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        System.out.println("onPause");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        System.out.println("onDestroy");
//    }
//
//
//    private View mNightView = null;
//
//    private WindowManager mWindowManager;
//
//    public void change_to_night() {
//
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//
//                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT,
//
//                WindowManager.LayoutParams.TYPE_APPLICATION,
//
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//
//                PixelFormat.TRANSLUCENT);
//
//
//        lp.gravity = Gravity.BOTTOM;// 可以自定义显示的位置
//
//        lp.y = 10;
//
//        if (mNightView == null) {
//
//            mNightView = new TextView(this);
//            mNightView.setBackgroundColor(0x80000000);
//
//
//        }
//
//        try {
//
//            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                    this, R.anim.aurora_menu_cover_enter);
//            mNightView.startAnimation(hyperspaceJumpAnimation);
//
//            mWindowManager.addView(mNightView, lp);
//
//
//            /**
//             * 标识
//             */
//
//
//        } catch (Exception ex) {
//        }
//
//
//    }
//
//
//    public void change_to_day() {
//        try {
//
//            /***
//             * 标识
//             */
//
//
//            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                    this, R.anim.aurora_menu_cover_exit);
//            mNightView.startAnimation(hyperspaceJumpAnimation);
//
//
//            mWindowManager.removeView(mNightView);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//
//    }
//
//
//}
