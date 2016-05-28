package com.example.gongzhiyao.twonote03.Search_log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.Adapter.Adapter;
import com.example.gongzhiyao.twonote03.Edit_log.Edit_Text;
import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.SelectPicPopupWindow;
import com.example.gongzhiyao.twonote03.database.MyDB;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Search_Result extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private PullToRefreshListView lv_search;
    private MyDB db;
    private SQLiteDatabase dbread,dbwrite;
    private  Adapter adapter;
    private Cursor c;
    private static final String TAG="search";
    public static final int Request_edit_code = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__result);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on= MainActivity.sp.getBoolean("night_light",false);

        if(night_on){
            change_to_night();
        }
        db=new MyDB(this);
        dbread=db.getReadableDatabase();
        dbwrite=db.getWritableDatabase();

        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        lv_search= (PullToRefreshListView) findViewById(R.id.lv_search);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchView= (SearchView)findViewById(R.id.search_log);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("在“TwoNote”中搜索");

        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击一个Item时，查询数据库，发送消息
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // c = adapter.getCursor();
                c = adapter.getCursor();
                c.moveToPosition(position - 1);
                final String passwd = c.getString(c.getColumnIndex("passwd"));
                final String titie = c.getString(c.getColumnIndex("name"));
                final String content = c.getString(c.getColumnIndex("content"));
                final String date = c.getString(c.getColumnIndex("date"));
                final int id1 = c.getInt(c.getColumnIndex("_id"));

//                Log.i(TAG, "此时的密码是" + passwd);
                if (!passwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Search_Result.this);
                    dialog.setTitle("文件已加密!");
                    LayoutInflater li = LayoutInflater.from(getApplicationContext());
                    View edit = li.inflate(R.layout.edit_passwd, null);
                    final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                    TextView tv_passwd = (TextView) edit.findViewById(R.id.tv_passwd);
                    dialog.setView(edit);
                    dialog.setNegativeButton("取消", null);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_passwd = et_passwd.getText().toString();
                            if (passwd.equals(input_passwd)) {

                                Intent i = new Intent(Search_Result.this, Edit_Text.class);
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
                    Intent i = new Intent(Search_Result.this, Edit_Text.class);
                    i.putExtra(Edit_Text.Title, c.getString(c.getColumnIndex("name")));
                    i.putExtra(Edit_Text.Content, c.getString(c.getColumnIndex("content")));
//                    Log.i(TAG, "要发送的content是" + c.getString(c.getColumnIndex("content")));
                    i.putExtra(Edit_Text.Date, c.getString(c.getColumnIndex("date")));
                    i.putExtra(Edit_Text.Id, c.getInt(c.getColumnIndex("_id")));
                    i.putExtra(Edit_Text.passwd, passwd);
                    startActivityForResult(i, Request_edit_code);
                }
            }
        });


        ListView actualListView = lv_search.getRefreshableView();

        actualListView.setOnItemLongClickListener(longClick);

        lv_search.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                                               @Override
                                               public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
                                                   lv_search.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                                                   lv_search.getLoadingLayoutProxy().setPullLabel("下拉刷新数据");
                                                   lv_search.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
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
                                                           Toast.makeText(getApplicationContext(), "刷新成功", Toast.LENGTH_SHORT).show();
//                                                adapter1.notifyDataSetChanged();
                                                           //将下拉视图收起
                                                           lv_search.onRefreshComplete();
                                                       }
                                                   }.execute();
                                               }


                                               @Override
                                               public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                                                   lv_search.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                                                   lv_search.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                                                   lv_search.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
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
                                                           lv_search.onRefreshComplete();
                                                       }
                                                   }.execute();

                                               }
                                           }

        );



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String sq = "select *from notes where  date like? or name like?or summary like ?";
                Cursor c = dbread.rawQuery(sq, new String[]{"%" + selectWord + "%", "%" + selectWord + "%", "%" + selectWord + "%"});
                adapter = new Adapter(Search_Result.this, R.layout.list_cell_item, c);
                lv_search.setAdapter(adapter);


                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectWord = newText;
                return false;
            }
        });














        refreshList();



    }
    String selectWord;
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_search__result, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private AdapterView.OnItemLongClickListener longClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Intent i = new Intent(Search_Result.this, SelectPicPopupWindow.class);
            i.putExtra("position", position);
//            Log.i(TAG, "即将传递过去的position是" + position);
            startActivity(i);

//            Log.i(TAG, "长按操作已执行");

            return true;
        }
    };



    private void refreshList(){
        Cursor cursor=dbread.query("notes",null,null,null,null,null,null);
        adapter=new Adapter(this,R.layout.list_cell_item,cursor);
        lv_search.setAdapter(adapter);
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
    protected void onDestroy() {
        super.onDestroy();
        dbwrite.close();
        dbread.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

}
