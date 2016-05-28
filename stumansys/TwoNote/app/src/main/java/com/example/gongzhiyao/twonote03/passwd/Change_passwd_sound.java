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

public class Change_passwd_sound extends AppCompatActivity {

    EditText ed_passwd_old,ed_passwd_new;
    Button btn_change_passwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd_sound);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);

        if(night_on){
            change_to_night();
        }
        final String passwd_old=MainActivity.sp.getString("passwd_for_sound",null);

        ed_passwd_old= (EditText) findViewById(R.id.ed_passwd_sound_old);
        ed_passwd_new= (EditText) findViewById(R.id.ed_passwd_sound_new);
        btn_change_passwd= (Button) findViewById(R.id.btn_change_passwd_for_sound);

        btn_change_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_passwd_old=ed_passwd_old.getText().toString();
                if(input_passwd_old.equals(passwd_old)){
                    String input_passwd_new =ed_passwd_new.getText().toString();
                    MainActivity.sp.edit().putString("passwd_for_sound",input_passwd_new).commit();
                    Toast.makeText(getApplicationContext(),"密码已更改",Toast.LENGTH_SHORT).show();
//                    ed_passwd_old.setText("");
//                    ed_passwd_new.setText("");
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"原始密码错误",Toast.LENGTH_SHORT).show();
                    ed_passwd_old.setText("");
                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_passwd_sound, menu);
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
