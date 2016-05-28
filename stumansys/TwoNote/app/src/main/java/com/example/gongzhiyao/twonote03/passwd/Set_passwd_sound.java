package com.example.gongzhiyao.twonote03.passwd;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;

public class Set_passwd_sound extends AppCompatActivity {

    EditText ed_setpasswd_sound,ed_setAgain;
    Button btn_setPasswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_passwd_sound);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);

        if(night_on){
            change_to_night();
        }



        ed_setpasswd_sound= (EditText) findViewById(R.id.ed_passwd_sound);
        ed_setAgain= (EditText) findViewById(R.id.ed_passwd_sound_again);
        btn_setPasswd= (Button) findViewById(R.id.btn_set_passwd_for_sound);

        btn_setPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd1=ed_setpasswd_sound.getText().toString();
                String passwd2=ed_setAgain.getText().toString();

                if(passwd1.equals(passwd2)){
                    if(!passwd1.equals("")){
                        Toast.makeText(getApplicationContext(),"密码设置成功",Toast.LENGTH_SHORT).show();
                        MainActivity.sp.edit().putString("passwd_for_sound", passwd1).commit();
                        MainActivity.sp.edit().putBoolean("need_passwd_sound", true).commit();
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"密码不能为空，请重新输入",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"密码不一致，请检查后重新输入",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_passwd_sound, menu);
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
