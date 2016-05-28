package com.example.gongzhiyao.twonote03.reminds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.gongzhiyao.twonote03.Edit_Remind;
import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.Task_finished;
import com.example.gongzhiyao.twonote03.database.MyDB;

public class Reminds_date extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    //    private static PullToRefreshListView lv_remind;
//    private static ListView lv_remind;
    private SwipeMenuListView lv_remind;
    private Button addReminds;
    private EditText titleReminds;
    private Button collectReminds;
    public static MyDB db;

    public static SQLiteDatabase dbread, dbwrite;
    AppAdapter adapter;
//    private static Remind_Adapter adapter;
    private static final String TAG = "reminds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminds_date);
        findView();
        bindListener();
        db = new MyDB(this);
        dbread = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);

        if (night_on) {
            change_to_night();
        }


        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        /**
         * 在这里还要新建一个自定义的Adapter。在数据库中获取信息
         * 并通过getview设置到响应的布局组件中
         */
        String sq = "select * from reminds where is_check =?";
        Cursor c = dbread.rawQuery(sq, new String[]{"false"});
        adapter = new AppAdapter(c,this);
        lv_remind.setAdapter(adapter);

            /**
             * 当前显示未完成的情况
             */
            SwipeMenuCreator creator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem item1 = new SwipeMenuItem(
                            getApplicationContext());
                    item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0,
                            0x3F)));
                    item1.setWidth(dp2px(120));

                    item1.setTitle("标记为已完成");
                    item1.setTitleSize(20);
                    item1.setTitleColor(R.color.Indigo_colorPrimary);
                    menu.addMenuItem(item1);
                    SwipeMenuItem item2 = new SwipeMenuItem(
                            getApplicationContext());
                    item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    item2.setWidth(dp2px(60));
                    item2.setTitle("删除");
                    item2.setTitleSize(20);
                    item2.setTitleColor(R.color.red);

                    menu.addMenuItem(item2);
                }
            };

            lv_remind.setMenuCreator(creator);






        lv_remind.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    /**
                     * 标记为已完成和删除都要把闹铃取消
                     */
                    case 0:
                        /**
                         * 这个是标记为已完成
                         */

                        int id = adapter.sp.getInt("" + position, -1);

                        /**
                         * 取消闹铃
                         */
                        MainActivity.sp.edit().putBoolean("reminds" + id, false).commit();
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        Intent i = new Intent("alarm");
                        PendingIntent pi = PendingIntent.getBroadcast(Reminds_date.this, Integer.valueOf(id), i, 0);
                        am.cancel(pi);//取消闹钟

                        Log.i(TAG, "此时的id是" + id);
                        String sq = "update reminds set is_check=? where _id=?";
                        dbwrite.execSQL(sq, new String[]{"true", id + ""});
                        adapter.notifyDataSetChanged();
                        refreshList(Reminds_date.this);
                        break;
                    case 1:





                        int id2 = adapter.sp.getInt("" + position, -1);

//                        Log.i(TAG, "此时的id是" + id2);
/**
 * 同样也需要取消闹钟
 */
                        MainActivity.sp.edit().putBoolean("reminds" + id2, false).commit();
                        AlarmManager am1 = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        Intent i1 = new Intent("alarm");
                        PendingIntent pi1 = PendingIntent.getBroadcast(Reminds_date.this, Integer.valueOf(id2), i1, 0);
                        am1.cancel(pi1);//取消闹钟


                        String sq2 = "delete from reminds where _id=?";
                        dbwrite.execSQL(sq2,new String[]{""+id2});
                        adapter.notifyDataSetChanged();
                        refreshList(Reminds_date.this);


                        break;
                }
                return false;
            }
        });





        refreshList(this);

    }

    private void bindListener() {
        addReminds.setOnClickListener(this);
        collectReminds.setOnClickListener(this);
    }

    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_reminds);
        lv_remind = (SwipeMenuListView) findViewById(R.id.lv_reminds);
        addReminds = (Button) findViewById(R.id.add_reminds);
        titleReminds = (EditText) findViewById(R.id.title_reminds);
        collectReminds = (Button) findViewById(R.id.collect_reminds);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem change = menu.findItem(R.id.task_finished);


        change.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                startActivity(new Intent(Reminds_date.this,Task_finished.class));
                return false;
            }
        });


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminds_date, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            /**+
             * 监听器
             */

            case android.R.id.home:
                finish();
                break;

        }
        return true;
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

    int count = 0;
    boolean isCollect = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reminds:
                String title = titleReminds.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入日程安排", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("title", title);
                    if (isCollect == true) {
                        cv.put("is_collection", "true");
                    } else {
                        cv.put("is_collection", "f.alse");
                    }
                    dbwrite.insert("reminds", null, cv);
                    titleReminds.setText("");
                    collectReminds.setBackgroundResource(R.drawable.remind_collection);
                    isCollect = false;
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    refreshList(this);

                }

                break;
            case R.id.collect_reminds:
                count++;
                if (count % 2 == 1) {
                    collectReminds.setBackgroundResource(R.drawable.collection);
                    isCollect = true;
                } else {
                    collectReminds.setBackgroundResource(R.drawable.remind_collection);
                    isCollect = false;
                }

                break;
        }

    }

    public void refreshList(Context context) {
//
//        boolean finished = MainActivity.sp.getBoolean("is_finished", false);
//        if (finished == false) {

//        Cursor c=dbread.query("reminds",null,null,null,null,null,null);
            String sq = "select * from reminds where is_check =?";
            Cursor c = dbread.rawQuery(sq, new String[]{"false"});
            adapter = new AppAdapter(c,context);
            adapter.notifyDataSetChanged();
            lv_remind.setAdapter(adapter);

            Log.i(TAG, "刷新了一次");

//        } else {
//
//            String sq = "select * from reminds where is_check =?";
//            Cursor c = dbread.rawQuery(sq, new String[]{"true"});
//            adapter = new AppAdapter(c,context);
//            adapter.notifyDataSetChanged();
//            lv_remind.setAdapter(adapter);
//
//            Log.i(TAG, "刷新了一次");
//        }
        adapter.notifyDataSetChanged();

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() + 1));
        listView.setLayoutParams(params);
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
        int id_get=0;
        public SharedPreferences sp;
        public AppAdapter(Cursor cursor,Context context){
            this.cursor=cursor;
            this.context=context;
            sp=getSharedPreferences("sp",MODE_PRIVATE);
        }


        public int  get_id(){
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

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_remind, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            cursor.moveToPosition(position);
            final int id = cursor.getInt(cursor.getColumnIndex("_id"));
            sp.edit().putInt(""+position,id).commit();
            id_get=id;
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String isCollecteds = cursor.getString(cursor.getColumnIndex("is_collection"));
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
//                    System.out.println("调用了一次");
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
                btn_collect= (Button) view.findViewById(R.id.reminds_collect);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}



