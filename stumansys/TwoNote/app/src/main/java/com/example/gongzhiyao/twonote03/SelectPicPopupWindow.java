package com.example.gongzhiyao.twonote03;

/**
 * Created by 宫智耀 on 2016/4/12.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.database.MyDB;
import com.example.gongzhiyao.twonote03.passwd.Shake_for_passwd_notes;

public class SelectPicPopupWindow extends Activity implements OnClickListener {

    private Button btn_name, btn_cancel,btn_collect,btn_delete,btn_passwd,btn_forget_passwd;
    private LinearLayout layout;
    private MyDB db = new MyDB(this);
    private SQLiteDatabase dbwrite,dbread;
    private String is_collect;
    private static final String TAG="bottom";
    private int position;
    private int id;
    private String name;
    private String passwd;
    private String content;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_bottom);

        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_name= (Button) this.findViewById(R.id.bottom_name);
        btn_collect= (Button) findViewById(R.id.btn_bottom_collect);
        btn_delete= (Button) findViewById(R.id.btn_bottom_delete);
        btn_passwd= (Button) findViewById(R.id.btn_bottom_passwd);
        btn_forget_passwd= (Button) findViewById(R.id.btn_bottom_passwd_forget);


        dbread=db.getReadableDatabase();
        dbwrite=db.getWritableDatabase();
        Intent i=getIntent();
        position=i.getIntExtra("position", -1);
        Log.i(TAG,"此时的position是"+position);
        Cursor c=dbread.query("notes", null, null, null, null, null, null);
        /**
         * 还是由于pulltorefresh的原因，在原来的position上+1
         */
        c.moveToPosition(position -1);

        name=c.getString(c.getColumnIndex("name"));
        btn_name.setText("文件名： "+name+" .txt");
        is_collect=c.getString(c.getColumnIndex("is_collect"));
        passwd=c.getString(c.getColumnIndex("passwd"));
        id=c.getInt(c.getColumnIndex("_id"));
        content=c.getString(c.getColumnIndex("content"));
        Log.i(TAG,"此时的id是"+id);
        Log.i(TAG,"is_collect的值是"+is_collect);
        if(is_collect.equals("true")){
            btn_collect.setText("取消收藏");
        }
        if(!passwd.equals("")){
            btn_passwd.setText("关闭密码");
        }


        layout = (LinearLayout) findViewById(R.id.pop_layout);

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_name.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_passwd.setOnClickListener(this);
        btn_forget_passwd.setOnClickListener(this);
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        finish();
        Log.i(TAG,"这里结束");
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom_collect:
                Log.i(TAG,"收藏被点击");
                if(is_collect.equals("false")){
                  Log.i(TAG,"已经收藏");
                    String sq="update notes set is_collect=? where _id=?";
                    dbwrite.execSQL(sq,new String[]{"true",""+id});
                    Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                }else if(is_collect.equals("true")){
                    String sq="update notes set is_collect=? where _id=?";
                    dbwrite.execSQL(sq,new String[]{"false",""+id});
                    Toast.makeText(getApplicationContext(),"取消收藏",Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"取消收藏成功");
                }

                break;
            case R.id.btn_bottom_delete:

               Intent i=new Intent(this,MainActivity.class);
                i.putExtra("op",1);
                i.putExtra("id",id);
                i.putExtra("name",name);
                i.putExtra("passwd",passwd);
                i.putExtra("content",content);
                i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                startActivity(i);


                break;
            case R.id.btn_bottom_passwd:
                Intent i1=new Intent(this,MainActivity.class);
                i1.putExtra("id",id);
                i1.putExtra("name",name);
                i1.putExtra("passwd",passwd);
                if(passwd.equals("")){
                    //密码为空，创建新密码

                    i1.putExtra("op", 2);



                }else {

                    //存在密码，清除密码的操作
                    i1.putExtra("op", 3);
                }
                i1.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                startActivity(i1);
                break;


            case R.id.btn_bottom_passwd_forget:

                /**
                 * 前提是有密码
                 */

                if(passwd.equals("")){}
                else {
                    Intent intent=new Intent(this, Shake_for_passwd_notes.class);
                    intent.putExtra("passwd",passwd);
                    Log.i(TAG,"此时的密码是"+passwd);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbwrite.close();
        dbread.close();
    }
}