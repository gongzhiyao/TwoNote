package com.example.gongzhiyao.twonote03;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.Adapter.Adapter;
import com.example.gongzhiyao.twonote03.Edit_log.Edit_Text;
import com.example.gongzhiyao.twonote03.Search_log.Search_Result;
import com.example.gongzhiyao.twonote03.Service.infoService;
import com.example.gongzhiyao.twonote03.database.MyDB;
import com.example.gongzhiyao.twonote03.passwd.Input_passwd_for_sound;
import com.example.gongzhiyao.twonote03.recordsoundActivity.Record_Sound_list;
import com.example.gongzhiyao.twonote03.reminds.Collection_list;
import com.example.gongzhiyao.twonote03.reminds.Reminds_date;
import com.example.gongzhiyao.twonote03.setting.Setting_feedBack;
import com.example.gongzhiyao.twonote03.setting.Settings;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    //声明相关变量
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Adapter adapter1;
    private Button mbSetting;
     Handler handler;
    private Button mbAdd;
    private MyDB db;
    private SQLiteDatabase dbwrite, dbRead;
    //    private SimpleCursorAdapter adapter;
//    private ListView lv;
    private PullToRefreshListView lv;
    public static final int Request_edit_code = 1;
    public static final int Request_add_code = 2;
    public static final String TAG = "Main";
    private RelativeLayout slider_view;
    private LinearLayout root_View;
    private Button mbRecent_log, mbRecord_Sound, mbCollection,mbReminds,mbSync,btn_send_back;
    private int delete_id;
    private String delete_name, delete_passwd, delete_content;
    private Cursor c;
    public static SharedPreferences sp;
    //    private Button mblack;
    private Switch night_switch;
    //    private int light;
    private boolean is_night_on;
    private LinearLayout windowLayout;
    private View mNightView = null;
    private WindowManager mWindowManager;
    private boolean is_drawer_on = false;
    private boolean onlytitle=false;
    private TextView name_get,sign_get;

    /****************************************************************************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.activity_main);
        findViews(); //获取控件

        sp = getSharedPreferences("Main_sp",MODE_PRIVATE);

        try {
            is_night_on = sp.getBoolean("night_light", false);
//            onlytitle=sp.getBoolean("only_title",false);
            Log.i(TAG, "此时的开关是" + is_night_on);
        } catch (Exception e) {
        }

        if (is_night_on) {
            night_switch.setChecked(true);
            change_to_night();
        }


        bindListener();
        try {
            Intent i = getIntent();
            if (i != null) {
                int op = i.getIntExtra("op", -1);
                delete_id = i.getIntExtra("id", -1);
                delete_name = i.getStringExtra("name");
                delete_passwd = i.getStringExtra("passwd");
                delete_content = i.getStringExtra("content");
                if (op == 1 && delete_passwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("提醒");
                    dialog.setMessage("您确定要删除" + delete_name + ".txt吗？");
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sq = "delete from notes where _id=?";
                            dbwrite.execSQL(sq, new String[]{"" + delete_id});
                            deleteFile(delete_content);
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });


                    dialog.create().show();

                } else if (op == 1 && !delete_passwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("删除需要权限!");
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View edit = li.inflate(R.layout.edit_passwd, null);
                    final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                    TextView tv_passwd = (TextView) edit.findViewById(R.id.tv_passwd);
                    /**
                     * 这一句非常重要不能忘记
                     */
                    dialog.setView(edit);
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_passwd = et_passwd.getText().toString();
                            if (delete_passwd.equals(input_passwd)) {
                                String sq = "delete from notes where _id=?";
                                dbwrite.execSQL(sq, new String[]{"" + delete_id});
                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                /**
                                 * 把内部文件删除
                                 */
                                deleteFile(delete_content);
                            } else {
                                Toast.makeText(getApplicationContext(), "密码输入错误,获取权限失败！", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });
                    dialog.create().show();

                } else if (op == 2) {
                    //在这里要创建密码
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("设置密码");
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View edit = li.inflate(R.layout.edit_passwd, null);
                    final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                    TextView tv_passwd = (TextView) edit.findViewById(R.id.tv_passwd);
                    /**
                     * 这一句非常重要不能忘记
                     */
                    dialog.setView(edit);//
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String passwd = et_passwd.getText().toString();
                            if (passwd.equals("")) {
                                Toast.makeText(getApplicationContext(), "密码设置失败,密码不能为空!", Toast.LENGTH_SHORT).show();

                            } else {
                                Log.i(TAG, "获得的密码是" + passwd);
                                String sq = "update notes set passwd=? where _id=?";
                                dbwrite.execSQL(sq, new String[]{passwd, "" + delete_id});
                                Toast.makeText(getApplicationContext(), "密码设置成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                    dialog.create().show();


                } else if (op == 3) {
                    Log.i(TAG, "需要清除密码");
                    /**
                     * 清除密码暂时不写，先把查看需要密码和删除需要密码写了
                     *
                     * 现在开始写取消密码
                     */

                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("是否要取消密码？");
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View edit = li.inflate(R.layout.edit_passwd, null);
                    final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                    TextView tv_passwd = (TextView) edit.findViewById(R.id.tv_passwd);
                    /**
                     * 这一句非常重要不能忘记
                     */
                    dialog.setView(edit);//
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String passwd = et_passwd.getText().toString();
                            if (delete_passwd.equals(passwd)) {
                                String sq = "update notes set passwd=? where _id=?";
                                dbwrite.execSQL(sq, new String[]{"", "" + delete_id});
                                Toast.makeText(getApplicationContext(), "密码已取消", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "密码输入错误", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        }
                    });
                    dialog.create().show();


                }
            }
        } catch (Exception e) {
            Log.i(TAG, "有异常发生");
        }
        slider_view = (RelativeLayout) findViewById(R.id.slider_content);

        db = new MyDB(this);
        dbRead = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();


/*************************************************************************************************************************************************************************/
/********************长按上下文菜单也在这******************************************************************************************************************************************************************/
        /*******这一部分是下拉列表的刷新，里面有部分网络通信，没有写******/

        //此处缺少一个布局文件，用于显示Listview的Item
//        adapter = new SimpleCursorAdapter(this, R.layout.list_cell_item, null, new String[]{"name", "content", "date"}, new int[]{R.id.tv_title, R.id.tv_content, R.id.tv_date});
//        adapter = new SimpleCursorAdapter(this, R.layout.list_cell_item, null, new String[]{"name", "date"}, new int[]{R.id.tv_title, R.id.tv_date});
//        Cursor c=dbRead.query("notes",null,null,null,null,null,null);
        /**
         * 更改前的adapter
         */
        // adapter = new SimpleCursorAdapter(this, R.layout.list_cell_item, null, new String[]{"name", "summary", "date"}, new int[]{R.id.tv_title, R.id.tv_content, R.id.tv_date});

        Cursor cc = dbRead.query("notes", null, null, null, null, null, null);
        adapter1 = new Adapter(this, R.layout.list_cell_item, cc);


        lv.setAdapter(adapter1);
        //lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击一个Item时，查询数据库，发送消息
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // c = adapter.getCursor();
                c = adapter1.getCursor();
                c.moveToPosition(position - 1);
                final String passwd = c.getString(c.getColumnIndex("passwd"));
                final String titie = c.getString(c.getColumnIndex("name"));
                final String content = c.getString(c.getColumnIndex("content"));
                final String date = c.getString(c.getColumnIndex("date"));
                final int id1 = c.getInt(c.getColumnIndex("_id"));

                Log.i(TAG, "此时的密码是" + passwd);
                if (!passwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("文件已加密!");
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View edit = li.inflate(R.layout.edit_passwd, null);
                    final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                    TextView tv_passwd = (TextView) edit.findViewById(R.id.tv_passwd);
                    /**
                     * 这一句非常重要不能忘记
                     */
                    dialog.setView(edit);
                    dialog.setNegativeButton("取消", null);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_passwd = et_passwd.getText().toString();
                            if (passwd.equals(input_passwd)) {

                                Intent i = new Intent(MainActivity.this, Edit_Text.class);
                                i.putExtra(Edit_Text.Title, titie);
                                i.putExtra(Edit_Text.Content, content);
                                Log.i(TAG, "要发送的content是" + content);
                                i.putExtra(Edit_Text.Date, date);
                                i.putExtra(Edit_Text.Id, id1);
                                i.putExtra(Edit_Text.passwd, passwd);
                                startActivityForResult(i, Request_edit_code);
                            } else {
                                Toast.makeText(getApplicationContext(), "密码输入错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.create().show();

                } else {
                    Intent i = new Intent(MainActivity.this, Edit_Text.class);
                    i.putExtra(Edit_Text.Title, c.getString(c.getColumnIndex("name")));
                    i.putExtra(Edit_Text.Content, c.getString(c.getColumnIndex("content")));
                    Log.i(TAG, "要发送的content是" + c.getString(c.getColumnIndex("content")));
                    i.putExtra(Edit_Text.Date, c.getString(c.getColumnIndex("date")));
                    i.putExtra(Edit_Text.Id, c.getInt(c.getColumnIndex("_id")));
                    i.putExtra(Edit_Text.passwd, passwd);
                    startActivityForResult(i, Request_edit_code);
                }
            }
        });


        ListView actualListView = lv.getRefreshableView();

        actualListView.setOnItemLongClickListener(longClick);

        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                                    @Override
                                    public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
                                        lv.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                                        lv.getLoadingLayoutProxy().setPullLabel("下拉刷新数据");
                                        lv.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
                                        //模拟加载数据线程休息3秒
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                try {
                                                    Thread.sleep(1000);//模拟网络通信 ，在这里休眠了一秒


                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                //这里是后台做的事
                                                //在背后连接服务器刷新数据

                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void result) {
                                                super.onPostExecute(result);
                                                //完成对下拉刷新ListView的更新操作
                                                /**
                                                 *
                                                 * 这个打算先不写
                                                 */
                                                refreshList();
                                                Toast.makeText(getApplicationContext(),"刷新成功",Toast.LENGTH_SHORT).show();
//                                                adapter1.notifyDataSetChanged();
                                                //将下拉视图收起
                                                lv.onRefreshComplete();
                                            }
                                        }.execute();
                                    }


                                    @Override
                                    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                                        lv.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                                        lv.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                                        lv.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
                                        //模拟加载数据线程休息3秒
                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                try {
                                                    Thread.sleep(3000);//数据写在这里
//                                                    data.add("更多数据1");
//                                                    data.add("更多数据2");
//                                                    data.add("更多数据3");
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }

                                            @Override
                                            protected void onPostExecute(Void result) {
                                                super.onPostExecute(result);
                                                //完成对下拉刷新ListView的更新操作
//                                                adapter1.notifyDataSetChanged();
                                                //将下拉视图收起
                                                lv.onRefreshComplete();
                                            }
                                        }.execute();

                                    }
                                }

        );
/****************************************************************************************************************************/
/*************************************************************************************************************************************/


        refreshList();


          handler=new Handler(){
              @Override
              public void handleMessage(Message msg) {
                  super.handleMessage(msg);
                  switch (msg.what){

                      case 1:
                          Toast.makeText(MainActivity.this,"已同步",Toast.LENGTH_SHORT).show();

                          break;
                  }
              }
          };
















//在这里写主布局

/************************************************************************************************************
 /******************************************************************************************************/
        /**************这里是Toolbar的设置***************/

        toolbar.setTitle("");//设置Toolbar标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色

        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new

                ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
                    @Override
                    public void onDrawerOpened(View drawerView) {//抽屉打开后，做的操作
                        super.onDrawerOpened(drawerView);
                        Log.i(TAG, "抽屉已打开");
                        slider_view.setClickable(true);
                        is_drawer_on = true;
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {//抽屉关闭后做的操作
                        super.onDrawerClosed(drawerView);
                        is_drawer_on = false;
//                mAnimationDrawable.start();
                    }
                }

        ;
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }//这是oncreate的最后

    /*************************************************************************************************/

    private void bindListener() {
        mbSync.setOnClickListener(this);
        mbAdd.setOnClickListener(this);
        mbSetting.setOnClickListener(this);
        mbRecent_log.setOnClickListener(this);
        mbAdd.setOnClickListener(this);
        mbRecord_Sound.setOnClickListener(this);
        mbCollection.setOnClickListener(this);
        mbReminds.setOnClickListener(this);
        btn_send_back.setOnClickListener(this);
        night_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    change_to_night();

                    sp.edit().putBoolean("night_light", true).commit();


//                        Log.i(TAG,"开关打开");
                    night_switch.setChecked(true);
//
//                        light=getSystemBrightness(MainActivity.this);
//                        Log.i(TAG, "此时的系统亮度是" + light);
//                        sp.edit().putInt("light", light).commit();
//                        boolean autobrightness=isAutoBrightness(getContentResolver());
//                        sp.edit().putBoolean("isAuto",autobrightness).commit();
//                        Log.i(TAG, "此时的自动调节是" + autobrightness);
//                        if(autobrightness==true){
//                            closeAutoBrightness(MainActivity.this);
//                            Log.i(TAG,"自动亮度调节已关闭");
//                        }
//
//                        light=10;
//                        saveBrightness(getContentResolver(), light);
                } else {
                    /**
                     * 在这里要还原设置操作
                     */

                    change_to_day();


                    night_switch.setChecked(false);

//                        Log.i(TAG,"开关关闭");
                    sp.edit().putBoolean("night_light", false).commit();
//                        boolean auto=sp.getBoolean("isAuto", false);
//                        if(auto==true){
//                            openAutoBrightness(MainActivity.this);
//                        }else{
//                            int light_pre=sp.getInt("light",255);
//                            saveBrightness(getContentResolver(),light_pre);
//                        }


                }
            }
        });
//        mblack.setOnClickListener(this);
    }

    /***********************************************************************************************************/
    private void findViews() {
        btn_send_back= (Button) findViewById(R.id.btn_send_back);
        mbSync= (Button) findViewById(R.id.btn_snyc);
        windowLayout = (LinearLayout) findViewById(R.id.layout);
        mbRecent_log = (Button) findViewById(R.id.btn_recent_log);
        night_switch = (Switch) findViewById(R.id.swich_night);
        root_View = (LinearLayout) findViewById(R.id.root_view);
        mbCollection = (Button) findViewById(R.id.drawer_my_collection);
        lv = (PullToRefreshListView) findViewById(R.id.lv);
        mbAdd = (Button) findViewById(R.id.btn_add);
        mbAdd = (Button) findViewById(R.id.btn_add);
        mbSetting = (Button) findViewById(R.id.btn_setting);
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);
        mbRecent_log = (Button) findViewById(R.id.btn_recent_log);
        mbReminds= (Button) findViewById(R.id.reminds);
//        mblack= (Button) findViewById(R.id.btn_black);
        mbRecord_Sound = (Button) findViewById(R.id.btn_record_sound_log);
        name_get= (TextView) findViewById(R.id.name_get);
        sign_get= (TextView) findViewById(R.id.sign_get);
    }


    private TextView tv;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {


            case Request_edit_code:
            case Request_add_code: //无论是新建还是修改都要刷新一下列表

                if (resultCode == Activity.RESULT_OK) {
                    //如果是add操作，就刷.................................新列表
                    Log.i(TAG, "刷新列表成功");


                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
        Log.i(TAG, "主界面恢复");
        String name=sp.getString("name_changed","TwoNote");
        String sign=sp.getString("sign_changed","everything is ok ");
        name_get.setText(name);
        sign_get.setText(sign);

//        mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "主界面暂停");
//        mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "主界面销毁");
        super.onDestroy();
        dbRead.close();
        dbwrite.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**
         * 在这里启动服务
         */
        Log.i(TAG, "主界面启动");
        Intent i = new Intent(this, infoService.class);
        startService(i);


    }

    private void refreshList() {
//        Cursor c1 = dbRead.query("notes", null, null, null, null, null, null);
//        adapter1.changeCursor(c1);

        Cursor cc = dbRead.query("notes", null, null, null, null, null, null);
        adapter1 = new Adapter(this, R.layout.list_cell_item, cc);
        lv.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        Log.i(TAG, "刷新了一次列表");
    }

    /**********************
     * 监听器,左边界面的监听器也在里面
     *********************************************************************************/


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting:
                mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
                /**
                 * 在这里写一个线程，让抽屉延时关闭，把startActivity写到线程中
                 */
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(300);
                            startActivity(new Intent(MainActivity.this,Settings.class));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                break;
            case R.id.btn_recent_log:
                mDrawerLayout.closeDrawer(slider_view);

                break;
            case R.id.btn_add:
                add_new_one();
                break;
            case R.id.btn_record_sound_log:
                Log.i(TAG, "点击了录音笔记");
                boolean need_passwd=sp.getBoolean("need_passwd_sound",false);
//                String passwd=sp.getString("passwd_for_sound",null);
                if(need_passwd){

//                    Log.i(TAG,"此时需要密码，并且密码是"+passwd);

                    mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
                    /**
                     * 在这里写一个线程，让抽屉延时关闭，把startActivity写到线程中
                     */
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(200);
                                startActivity(new Intent(MainActivity.this,Input_passwd_for_sound.class));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();









                }else {

                    mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
                    /**
                     * 在这里写一个线程，让抽屉延时关闭，把startActivity写到线程中
                     */
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(300);
                                startActivity(new Intent(MainActivity.this, Record_Sound_list.class));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

                break;
            case R.id.drawer_my_collection:
                mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
                /**
                 * 在这里写一个线程，让抽屉延时关闭，把startActivity写到线程中
                 */
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(300);
                            startActivity(new Intent(MainActivity.this, Collection_list.class));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.reminds:
                mDrawerLayout.closeDrawer(slider_view);//这就是代码关闭抽屉
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(300);
                            startActivity(new Intent(MainActivity.this, Reminds_date.class));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;



            case R.id.btn_snyc:
                mDrawerLayout.closeDrawer(slider_view);
                final ProgressDialog dialog=new ProgressDialog(MainActivity.this);

                dialog.setTitle("正在同步...");
                dialog.setMessage("请稍后");
                dialog.create();
                dialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(1200);
                            dialog.dismiss();

                            /**
                             * 在这里使用了handler进行ui视图的改变
                             *
                             */

                            handler.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    }
                }.start();

                break;

            case R.id.btn_send_back:
                startActivity(new Intent(MainActivity.this,Setting_feedBack.class));
                break;




        }
    }

    private void add_new_one() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");
        String time = f.format(new Date());
        Intent i = new Intent(MainActivity.this, Edit_Text.class);
        i.putExtra(Edit_Text.Date, time);
        startActivityForResult(i, Request_add_code);
        Log.i(TAG, "获取到的时间是" + time);
    }

    /*********************************
     * 后面是自定义menu和menuitem监听器（SearchView）的设置
     ***************************************************************************/
    public static void setSearchViewOnClickListener(View v, View.OnClickListener listener) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = group.getChildAt(i);
                if (child instanceof LinearLayout || child instanceof RelativeLayout) {
                    setSearchViewOnClickListener(child, listener);
                }

                if (child instanceof TextView) {
                    TextView text = (TextView) child;
                    text.setFocusable(false);
                }
                child.setOnClickListener(listener);
            }
        }
    }

    final int Menu_Add_new_one = Menu.FIRST;
    final int Menu_My_collection = Menu.FIRST + 1;
    final int Menu_Sync = Menu.FIRST + 2;
    final int Menu_setting = Menu.FIRST + 3;
    final int Menu_only_title = Menu.FIRST + 4;
    final int Menu_summary = Menu.FIRST + 5;
    MenuItem searchViewButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.add(0, Menu_Add_new_one, 0, "新建");

        menu.add(0, Menu_My_collection, 0, "我的收藏");
        SubMenu subMenu = menu.addSubMenu("显示方式");
        subMenu.add(0, Menu_only_title, 0, "只显示标题");
        subMenu.add(0, Menu_summary, 0, "显示摘要");
        menu.add(0, Menu_setting, 0, "设置");

        searchViewButton = (MenuItem) menu.findItem(R.id.ab_search);
        searchViewButton.setIcon(R.drawable.toolbar_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewButton);
        setSearchViewOnClickListener(searchView, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Search_Result.class));
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case Menu_Add_new_one:
                add_new_one();
//                Toast.makeText(getApplicationContext(),"点击了新建按钮",Toast.LENGTH_SHORT).show();
                break;
            case Menu_My_collection:
//                Toast.makeText(getApplicationContext(), "点击了我的收藏", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,Collection_list.class));
                break;

            case Menu_setting:
              startActivity(new Intent(MainActivity.this,Settings.class));

                break;
            case Menu_only_title:

                sp.edit().putBoolean("only_title",true).commit();
                refreshList();
                break;
            case Menu_summary:
                sp.edit().putBoolean("only_title",false).commit();
                refreshList();
                break;


        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * 这里的代码是“再按一次退出”
     */

    long waitTime = 2000;
    long touchTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
            if (is_drawer_on) {
                mDrawerLayout.closeDrawer(slider_view);
                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime) {
                    //让Toast的显示时间和等待时间相同
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    touchTime = currentTime;
                } else {
                    finish();


                }
                return true;


            } else {


                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime) {
                    //让Toast的显示时间和等待时间相同
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    touchTime = currentTime;
                } else {
                    finish();


                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


/******************************************************************************************************************/
    /*********************************************************************************************/
    private AdapterView.OnItemLongClickListener longClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Intent i = new Intent(MainActivity.this, SelectPicPopupWindow.class);
            i.putExtra("position", position);
            Log.i(TAG, "即将传递过去的position是" + position);
            startActivity(i);

            Log.i(TAG, "长按操作已执行");

            return true;
        }
    };


//    public  String getAbsoluteImagePath(Uri uri)
//    {
//        // can post image
//        String [] proj={MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery( uri,
//                proj,                 // Which columns to return
//                null,       // WHERE clause; which rows to return (all rows)
//                null,       // WHERE clause selection arguments (none)
//                null);                 // Order-by clause (ascending by name)
//
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//
//        return cursor.getString(column_index);
//    }

/***************************************************************************************/
    /***********
     * 这里是夜间模式的方法
     *******************************************************************/


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


//    /**
//     * 获取系统亮度
//     * @param context
//     * @return
//     */
//    public static int getSystemBrightness(Context context)
//    {
//        int brightnessValue = -1;
//        try
//        {
//            brightnessValue = Settings.System.
//                    getInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return brightnessValue;
//    }
//
//    /**
//     * 检测是否开启的自动调节亮度
//     * @param contentResolver
//     * @return
//     */
//    public static boolean isAutoBrightness(ContentResolver contentResolver)
//    {
//        boolean autoBrightness = false;
//        try
//        {
//            autoBrightness
//                    = Settings.System.getInt(contentResolver ,
//                    Settings.System.SCREEN_BRIGHTNESS_MODE)
//                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return autoBrightness;
//    }
//
//    /**
//     * 关闭自动调节亮度
//     * @param activity
//     */
//    public static void closeAutoBrightness(Activity activity)
//    {
//        Settings.System.putInt(activity.getContentResolver(),
//                Settings.System.SCREEN_BRIGHTNESS_MODE,
//                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//    }
//
//    /**
//     * 打开亮度调节
//     * @param activity
//     */
//    public static void openAutoBrightness(Activity activity)
//    {
//        Settings.System.putInt(activity.getContentResolver(),
//                Settings.System.SCREEN_BRIGHTNESS_MODE,
//                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
//    }
//
//    public static void saveBrightness
//            (ContentResolver contentResolver , int brightnessValue)
//    {
//        Uri uri = android.provider.
//                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
//        android.provider.Settings.System.putInt(contentResolver,
//                Settings.System.SCREEN_BRIGHTNESS , brightnessValue);
//        contentResolver.notifyChange(uri, null);
//    }
//
//
//
//


}


/**
 * Created by 宫智耀 on 2016/4/24.
 */
