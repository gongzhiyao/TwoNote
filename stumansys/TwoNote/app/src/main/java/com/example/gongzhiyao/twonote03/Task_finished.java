package com.example.gongzhiyao.twonote03;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.gongzhiyao.twonote03.database.MyDB;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Task_finished extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private SwipeMenuListView lv_task_finished;
    AppAdapter adapter;
    private MyDB db;
    private SQLiteDatabase dbread, dbwrite;
    private Cursor c;
    private static final String TAG = "task_finished";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finished);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);

        if (night_on) {
            change_to_night();
        }

        db = new MyDB(this);
        dbread = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();
        toolbar = (Toolbar) findViewById(R.id.toolbar_task_finished);
        lv_task_finished = (SwipeMenuListView) findViewById(R.id.lv_task_finished);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchView = (SearchView) findViewById(R.id.search_task_finished);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("在“已完成任务”中搜索");

        String sq = "select * from reminds where is_check =?";
//        Cursor c=dbread.query("reminds",null,null,null,null,null,null);
        Cursor c = dbread.rawQuery(sq, new String[]{"true"});
        adapter = new AppAdapter(c, this);
        lv_task_finished.setAdapter(adapter);


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(R.color.Indigo_colorPrimary);
                item1.setWidth(dp2px(120));

//                item1.setIcon(R.drawable.ic_action_important);
                item1.setTitle("还原为未完成");
                item1.setTitleSize(20);
                item1.setTitleColor(R.color.black);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                item2.setWidth(dp2px(60));
                item2.setTitle("删除");
                item2.setTitleSize(20);
                item2.setTitleColor(R.color.red);
//                item2.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(item2);
            }
        };

        lv_task_finished.setMenuCreator(creator);


        lv_task_finished.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        /**
                         * 这个还原为未完成
                         */
                        // open
//                        int id=adapter.get_id();
                        int id = adapter.sp.getInt("" + position, -1);
                        Log.i(TAG, "此时的id是" + id);
                        String sq = "update reminds set is_check=? where _id=?";
                        dbwrite.execSQL(sq, new String[]{"false",id + ""});

                        adapter.notifyDataSetChanged();
                        refreshList(Task_finished.this);
                        Toast.makeText(getApplicationContext(), "已还原", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:

//                        int id2=adapter.get_id();
                        int id2 = adapter.sp.getInt("" + position, -1);
                        Log.i(TAG, "此时的id是" + id2);
                        String sq2 = "delete from reminds where _id=?";
                        dbwrite.execSQL(sq2, new String[]{"" + id2});
                        adapter.notifyDataSetChanged();
                        refreshList(Task_finished.this);
                        Toast.makeText(getApplicationContext(), "已删除", Toast.LENGTH_SHORT).show();
                        // delete
//					delete(item);
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String sq = "select * from reminds where title=? and is_check =?";
//                Cursor cc = dbread.rawQuery(sq, new String[]{"%" +selectWord+ "%","true"});
                Cursor cc = dbread.rawQuery(sq, new String[]{selectWord,"true"});
                Log.i(TAG,"长度是"+cc.getCount());
                adapter = new AppAdapter(cc, Task_finished.this);
                lv_task_finished.setAdapter(adapter);
                Log.i(TAG,"得到的selectword是"+selectWord);


                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectWord = newText;

                return false;
            }
        });


        refreshList(this);


    }

    String selectWord;
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_task_finished, menu);
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

    public void refreshList(Context context) {


        String sq = "select * from reminds where is_check =?";
        Cursor c = dbread.rawQuery(sq, new String[]{"true"});
        adapter = new AppAdapter(c, context);
        adapter.notifyDataSetChanged();
        lv_task_finished.setAdapter(adapter);

        Log.i(TAG, "刷新了一次");
        adapter.notifyDataSetChanged();
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
        refreshList(this);
    }


    class AppAdapter extends BaseAdapter {


        public Cursor cursor;
        Context context;
        int id_get = 0;
        public SharedPreferences sp;

        public AppAdapter(Cursor cursor, Context context) {
            this.cursor = cursor;
            this.context = context;
            sp = getSharedPreferences("sp", MODE_PRIVATE);
        }


        public int get_id() {
            return id_get;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

//        @Override
//        public ApplicationInfo getItem(int position) {
//            return mAppList.get(position);
//        }

        @Override
        public long getItemId(int position) {
            return position;
        }

//
//@Override
//        public int getViewTypeCount() {
//            // menu type count
//            return 3;
//        }
//        @Override
//        public int getItemViewType(int position) {
//            // current menu type
//            return position % 3;
//        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_remind, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
//            ApplicationInfo item = getItem(position);
//            holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
//            holder.tv_name.setText(item.loadLabel(getPackageManager()));


            cursor.moveToPosition(position);
            final int id = cursor.getInt(cursor.getColumnIndex("_id"));
            sp.edit().putInt("" + position, id).commit();
            id_get = id;
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String isCollecteds = cursor.getString(cursor.getColumnIndex("is_collection"));
//            String sq_2 = "select * from sub_tasks where father_id=?";
//            Cursor c1 = Reminds_date.dbread.rawQuery(sq_2, new String[]{"" + id});

            holder.tv_name.setText(title);
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                //            @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Edit_Remind.class);

                    intent.putExtra("ids", id);

                    context.startActivity(intent);
                }
            });


            if (isCollecteds.equals("true")) {
                holder.btn_collect.setBackgroundResource(R.drawable.collection);

            } else {

            }


            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;
            Button btn_collect;


            public ViewHolder(View view) {

                iv_icon = (ImageView) view.findViewById(R.id.reminds_sub);
                tv_name = (TextView) view.findViewById(R.id.reminds_title);
                btn_collect = (Button) view.findViewById(R.id.reminds_collect);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


}
