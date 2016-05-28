package com.example.gongzhiyao.twonote03.setting;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;

public class Setting_feedBack extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_feed_back);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);
        if(night_on){
            change_to_night();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_feed_back, menu);
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
