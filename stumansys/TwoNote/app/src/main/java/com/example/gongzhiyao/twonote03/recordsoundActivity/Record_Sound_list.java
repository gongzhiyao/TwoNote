package com.example.gongzhiyao.twonote03.recordsoundActivity;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gongzhiyao.twonote03.Fragment_main;
import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.Recordlist_fragment.Record_sound_fragment;
import com.example.gongzhiyao.twonote03.Recordlist_fragment.Record_sound_list;
import com.example.gongzhiyao.twonote03.Recordlist_fragment.ViewPager_adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Record_Sound_list extends AppCompatActivity implements Fragment_main.OnFragmentInteractionListener {

    private ViewPager viewPager;
    public static List<String> titleList;

    private static final String TAG = "List";
    private boolean need_notify=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record__sound_list);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);

        if(night_on){
            change_to_night();
        }


        NotificationManager manager= (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.cancel(1);


        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new Record_sound_fragment());
        fragments.add(new Record_sound_list());


        titleList = new ArrayList<String>();
        titleList.add("添加录音");
        titleList.add("录音列表");


        ViewPager_adapter adapter = new ViewPager_adapter(getSupportFragmentManager(), fragments);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i(TAG,"onPageScrolled"+position);
            }

            @Override
            public void onPageSelected(int position) {
//                Log.i(TAG,"onPageSelected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG,"onPageScrollStateChanged");
                Record_sound_list.refreshList();


            }
        });


    }

    private LinearLayout windowLayout;


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
            Log.i(TAG,"即将退出录音程序，确定要这么做吗？");
            if(Record_sound_fragment.mediaRecorder!=null){
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("提示：");
                dialog.setMessage("您即将退出录音程序，可是您的录音还未保存");
                dialog.setNegativeButton("取消", null);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         *在这里实现录音资源的释放，并且要关闭这个Activity
                         */
                        need_notify=false;
                        Log.i(TAG, "释放资源");
                        Record_sound_fragment.mediaRecorder.stop();
                        Record_sound_fragment.mediaRecorder.release();
                        String needToRemove = Record_sound_fragment.soundList.get(Record_sound_fragment.length_list - 1);
                        Log.i(TAG, "退出录音程序,此时应该把这个删除" + needToRemove);
                        File need_remove = new File(Environment.getExternalStorageDirectory() + "/TwoNote_Sound", needToRemove);
                        need_remove.delete();
                        Log.i(TAG, "录音已强制取消，文件已删除");
                        finish();

                    }
                });
                dialog.create().show();
            }else{
                finish();
            }

        }




        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.i(TAG, "fragment 所附着的Activity start");

    }


//    @TargetApi(Build.VERSION_CODES.MNC)

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "fragment 所附着的Activity pause");

        }





    @Override
    protected void onResume() {
        super.onResume();
//        Log.i(TAG, "fragment 所附着的Activity Resume");
    }

    @Override
    protected void onDestroy() {
//        Log.i(TAG,"fragment 所附着的Activity destroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record__sound_list, menu);
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


    /*************************************************/
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
