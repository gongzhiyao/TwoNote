package com.example.gongzhiyao.twonote03;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.gongzhiyao.twonote03.Edit_log.Edit_Text;
import com.example.gongzhiyao.twonote03.Receiver.AlarmReceiver;
import com.example.gongzhiyao.twonote03.database.MyDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class Edit_Remind extends AppCompatActivity implements View.OnClickListener {
    public static SharedPreferences sp;
    private static final String TAG = "reminds_edit";
    private Toolbar toolbar;
    private EditText tv_reminds_edit;
    private Button btn_reminds_edit;
    private static int id;
    String title;
    String iscollection;
    private MyDB db;
    public static SQLiteDatabase dbread;
    public static SQLiteDatabase dbwrite;
//    private ListView lv;
//    private SimpleAdapter adapter;
//    private List<Map<String, String>> listItems = new ArrayList<>();

    private Button setDate, setTime, add_sub_task, add_summary, relate_note, cancel_time;
    private TextView tv_show_date, tv_show_time, tv_show_summary;
    private static ListView lv_sub_task;
//    private SimpleCursorAdapter adapter;

    private static Sub_task_Adapter adapter;
    private List<Integer> list_id = new ArrayList<Integer>();

    String isCollection_or_not;
    List<String> list_name = new ArrayList<String>();
    private ListView lv_relates;
    private SimpleCursorAdapter simpleCursorAdapter;
    String is_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__remind);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);
        MainActivity.sp.edit().putBoolean("reminds_changed" + id, false).commit();
        if (night_on) {
            change_to_night();
        }
        db = new MyDB(this);
        dbread = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();


        new Thread() {
            @Override
            public void run() {
                super.run();

                Cursor c = dbread.query("notes", null, null, null, null, null, null);
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    String s = c.getString(c.getColumnIndex("name"));
                    if (list_name.contains(s)) {
                    } else {
                        list_name.add(s);
                    }
                }


            }
        }.start();


        /**
         * 用于引用
         */
        toolbar = (Toolbar) findViewById(R.id.toobar_reminds_edit);
        tv_reminds_edit = (EditText) findViewById(R.id.tv_reminds_edit);
        btn_reminds_edit = (Button) findViewById(R.id.btn_reminds_edit);
        setDate = (Button) findViewById(R.id.set_date);
        tv_show_date = (TextView) findViewById(R.id.edit_reminds_show_date);
        setTime = (Button) findViewById(R.id.set_time);
        tv_show_time = (TextView) findViewById(R.id.edit_reminds_show_time);
        add_sub_task = (Button) findViewById(R.id.set_sub_task);
        lv_sub_task = (ListView) findViewById(R.id.lv_sub_task);
        add_summary = (Button) findViewById(R.id.edit_reminds_summary);
        tv_show_summary = (TextView) findViewById(R.id.show_summary);
        relate_note = (Button) findViewById(R.id.edit_reminds_relate);
        lv_relates = (ListView) findViewById(R.id.lv_relates);
        cancel_time = (Button) findViewById(R.id.cancel_time);

        /**
         * 用于绑定监听器
         */
        setDate.setOnClickListener(this);
        setTime.setOnClickListener(this);
        add_sub_task.setOnClickListener(this);
        add_summary.setOnClickListener(this);
        btn_reminds_edit.setOnClickListener(this);
        relate_note.setOnClickListener(this);
        cancel_time.setOnClickListener(this);

        /**
         * 这里涉及了simpleadapter的使用，还有就是要写一个刷新的方法，用于在后面的选择监听器中刷新列表
         * 因为使用的是swipemenuListview所以后面还要写关于它的一些方法
         */
        id = getIntent().getIntExtra("ids", -1);
        Log.i(TAG, "此时传过来的id的值是" + id);
        String sq0="select * from reminds where _id=?";
        Cursor cursor0=dbread.rawQuery(sq0, new String[]{"" + id});
        cursor0.moveToFirst();
        is_check=cursor0.getString(cursor0.getColumnIndex("is_check"));
        sp = getSharedPreferences("" + id, MODE_PRIVATE);


        String sq = "select * from relates where father_id=?";
        Cursor c1 = dbread.rawQuery(sq, new String[]{"" + id});

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.lv_relate_cell, c1, new String[]{"title"}, new int[]{R.id.tv_relate});

        lv_relates.setAdapter(simpleCursorAdapter);
        lv_relates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id2) {

                String sq = "select * from relates where father_id=?";
                Cursor cursor = dbread.rawQuery(sq, new String[]{"" + id});
                cursor.moveToPosition(position);
                int id_relate = cursor.getInt(cursor.getColumnIndex("relates_id"));
                /**
                 * 把要传的传给editlog
                 */

                String sq_1 = "select * from notes where _id=?";
                Cursor c = dbread.rawQuery(sq_1, new String[]{"" + id_relate});
                System.out.println("第一次的id是" + id_relate);
                c.moveToFirst();
                final String passwd = c.getString(c.getColumnIndex("passwd"));
                final String titie = c.getString(c.getColumnIndex("name"));
                final String content = c.getString(c.getColumnIndex("content"));
                final String date = c.getString(c.getColumnIndex("date"));
                final int id1 = c.getInt(c.getColumnIndex("_id"));
                System.out.println("第二次的id是" + id1);
                Log.i(TAG, "此时的密码是" + passwd);
                if (!passwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Edit_Remind.this);
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

                                Intent i = new Intent(Edit_Remind.this, Edit_Text.class);
                                i.putExtra(Edit_Text.Title, titie);
                                i.putExtra(Edit_Text.Content, content);
                                Log.i(TAG, "要发送的content是" + content);
                                i.putExtra(Edit_Text.Date, date);
                                i.putExtra(Edit_Text.Id, id1);
                                i.putExtra(Edit_Text.passwd, passwd);
                                startActivityForResult(i, 1);
                            } else {
                                Toast.makeText(getApplicationContext(), "密码输入错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.create().show();

                } else {
                    Intent i = new Intent(Edit_Remind.this, Edit_Text.class);
                    i.putExtra(Edit_Text.Title, c.getString(c.getColumnIndex("name")));
                    i.putExtra(Edit_Text.Content, c.getString(c.getColumnIndex("content")));
                    Log.i(TAG, "要发送的content是" + c.getString(c.getColumnIndex("content")));
                    i.putExtra(Edit_Text.Date, c.getString(c.getColumnIndex("date")));
                    i.putExtra(Edit_Text.Id, c.getInt(c.getColumnIndex("_id")));
                    i.putExtra(Edit_Text.passwd, passwd);
                    startActivityForResult(i, 2);
                }
            }
        });

        lv_relates.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id2) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Edit_Remind.this);
                dialog.setTitle("提示").setMessage("您即将取消关联该Note文件!").setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sq = "select * from relates where father_id=?";
                                Cursor cursor = dbread.rawQuery(sq, new String[]{"" + id});
                                cursor.moveToPosition(position);
                                int id_relate = cursor.getInt(cursor.getColumnIndex("relates_id"));
                                String sq2 = "delete from relates where relates_id=?and father_id=?";
                                dbwrite.execSQL(sq2, new String[]{id_relate + "", id + ""});

                                refresh_lv_relate();


                            }
                        }).create().show();

                return true;
            }
        });


        String sq1 = "select * from reminds where _id=?";
        Cursor c = dbread.rawQuery(sq1, new String[]{"" + id});
        /**
         * 刚才查询的时候总是出现游标越界的情况，把游标移到First就解决了
         */
        c.moveToFirst();
        title = c.getString(c.getColumnIndex("title"));
        iscollection = c.getString(c.getColumnIndex("is_collection"));
        isCollection_or_not = iscollection;

        tv_reminds_edit.setText(title);
        if (!iscollection.equals("true")) {
            btn_reminds_edit.setBackgroundResource(R.drawable.remind_collection);
        } else {
            btn_reminds_edit.setBackgroundResource(R.drawable.collection);
        }

        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加返回键 图标

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        refresh();
        refresh_lv_relate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit__remind, menu);
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


    String sqs;
    String sqq;

    @Override
    protected void onPause() {
        super.onPause();


        sqq = "update reminds set is_collection=? where _id=?";
        dbwrite.execSQL(sqq, new String[]{isCollection_or_not, "" + id});
        String s = tv_reminds_edit.getText().toString();
        sqs = "update reminds set title =? where _id=?";
        dbwrite.execSQL(sqs, new String[]{s, "" + id});


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * 讨论已完成的任务的情况
         */





        boolean has_alarm = MainActivity.sp.getBoolean("reminds" + id, false);
        /**
         * 在添加一个是否修改了
         */
        boolean has_changed_alarm = MainActivity.sp.getBoolean("reminds_changed" + id, false);


        if(is_check.equals("true")){

            if(has_changed_alarm){
                Intent i = new Intent("alarm");
                i.putExtra("id", id);
                PendingIntent pi = PendingIntent.getBroadcast(Edit_Remind.this, Integer.valueOf(id), i, 0);
                AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);//设置闹铃设置成功

                String sq = "update reminds set is_check=? where _id=?";
                dbwrite.execSQL(sq, new String[]{"false",id + ""});
//                Toast.makeText(getApplicationContext(), "闹钟已设置成功", Toast.LENGTH_SHORT).show();

                Toast.makeText(getApplicationContext(),"已将该日程还原为未完成状态",Toast.LENGTH_SHORT).show();
            }

        }else {


            if (has_alarm) {
                if (has_changed_alarm) {
                    Intent i = new Intent("alarm");
                    i.putExtra("id", id);
                    PendingIntent pi = PendingIntent.getBroadcast(Edit_Remind.this, Integer.valueOf(id), i, 0);
                    AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);//设置闹铃设置成功
                    Toast.makeText(getApplicationContext(), "闹钟已设置成功", Toast.LENGTH_SHORT).show();
                }
            }
        }



        dbwrite.close();
        dbread.close();
        db.close();

    }


    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                super.run();

                Cursor c = dbread.query("notes", null, null, null, null, null, null);
                for (int i = 0; i < c.getCount(); i++) {
                    c.moveToPosition(i);
                    String s = c.getString(c.getColumnIndex("name"));
                    if (list_name.contains(s)) {
                    } else {
                        list_name.add(s);
                    }

                }


            }
        }.start();
        refresh_lv_relate();

    }

    Calendar c = Calendar.getInstance();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_date:

                /**
                 * 设置日期选择器
                 */
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date_show = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
                        tv_show_date.setText(date_show);
                        /**
                         * 还要存入数据库
                         */
                        String sq = "update reminds set date=? where _id=?";
                        dbwrite.execSQL(sq, new String[]{date_show, "" + id});
                        MainActivity.sp.edit().putBoolean("reminds" + id, true).commit();
                        MainActivity.sp.edit().putBoolean("reminds_changed" + id, true).commit();

                    }
                }, year, month, day);

                dialog.show();
                break;


            case R.id.set_time:
                Calendar calendar1 = Calendar.getInstance();
                int hour = calendar1.get(Calendar.HOUR_OF_DAY);
                int min = calendar1.get(Calendar.MINUTE);


                TimePickerDialog dialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        /**
                         * 数据库+tv
                         */
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);
                        String time_show = "";
                        if (minute < 10) {
                            time_show = hourOfDay + ":0" + minute;
                        } else {
                            time_show = hourOfDay + ":" + minute;
                        }
                        tv_show_time.setText(time_show);

                        String sq = "update reminds set time=? where _id=?";
                        dbwrite.execSQL(sq, new String[]{time_show, "" + id});
                        MainActivity.sp.edit().putBoolean("reminds" + id, true).commit();
                        MainActivity.sp.edit().putBoolean("reminds_changed" + id, true).commit();
                    }
                }, hour, min, true);
                dialog1.show();

                break;


            case R.id.set_sub_task:
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View add_task = inflater.inflate(R.layout.edittext_reminds_add_sub_menu, null);
                final EditText ed = (EditText) add_task.findViewById(R.id.ed_add_sub_menu);
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
                dialog2.setTitle("添加子任务");
                dialog2.setView(add_task);
                dialog2.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task_get = ed.getText().toString();
                        Log.i(TAG, task_get);
                        if (task_get.equals("")) {
                            Toast.makeText(getApplicationContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            /**
                             * 在不为空的情况下
                             */
                            ContentValues cv = new ContentValues();
                            cv.put("title", task_get);
                            cv.put("father_id", id);
                            cv.put("is_check", "false");
                            dbwrite.insert("sub_tasks", null, cv);

                            /**
                             * 还要给lv绑定adapter
                             */
                            refreshlist1(Edit_Remind.this);
                        }


                    }
                }).create().show();

                break;


            case R.id.edit_reminds_summary:
                LayoutInflater inflater1 = LayoutInflater.from(getApplicationContext());
                View add_summary_edit_reminds = inflater1.inflate(R.layout.edittext_dialog_summary, null);
                final EditText summary_get = (EditText) add_summary_edit_reminds.findViewById(R.id.ed_add_summary);
                AlertDialog.Builder dialog3 = new AlertDialog.Builder(this);
                dialog3.setTitle("添加描述");
                dialog3.setView(add_summary_edit_reminds);
                String sq = "select * from reminds where _id=?";
                Cursor cursor = dbread.rawQuery(sq, new String[]{"" + id});
                cursor.moveToFirst();
                String get_summary = cursor.getString(cursor.getColumnIndex("note"));
                if (!get_summary.equals("")) {
                    summary_get.setText(get_summary);
                }
                dialog3.setNegativeButton("取消", null).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * tv+数据库
                                 * 如果已经存在的话，点击可以重新编辑
                                 */


                                String summary = summary_get.getText().toString();
                                Log.i(TAG, "summary是" + summary);
                                tv_show_summary.setText(summary);

                                String sq = "update reminds set note= ? where _id=?";
                                dbwrite.execSQL(sq, new String[]{summary, "" + id});


                            }
                        }).create().show();

                break;

            case R.id.btn_reminds_edit:
                String sq3 = "select * from reminds where _id=?";
                Cursor cursor1 = dbread.rawQuery(sq3, new String[]{"" + id});
                cursor1.moveToFirst();
                isCollection_or_not = cursor1.getString(cursor1.getColumnIndex("is_collection"));

                if (isCollection_or_not.equals("false")) {
                    isCollection_or_not = "true";
                    btn_reminds_edit.setBackgroundResource(R.drawable.collection);

                } else {
                    isCollection_or_not = "false";
                    btn_reminds_edit.setBackgroundResource(R.drawable.remind_collection);

                }


                break;

            case R.id.edit_reminds_relate:
                AlertDialog.Builder dialog4 = new AlertDialog.Builder(this);
                final int size = list_name.size();
                dialog4.setTitle("选择要关联的Note").
                        setMultiChoiceItems((CharSequence[]) list_name.toArray(new CharSequence[size]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    int id_check = MainActivity.sp.getInt("" + which, -1);
                                    String sq = "select * from relates where relates_id=? and father_id=?";
                                    Cursor cursor2 = dbread.rawQuery(sq, new String[]{"" + id_check, "" + id});
                                    if (cursor2.getCount() != 0) {
                                    } else {

                                        String name = list_name.get(which);
                                        System.out.println("此时选中的id是" + id_check + "    " + name);
                                        ContentValues cv = new ContentValues();
                                        cv.put("father_id", id);
                                        cv.put("relates_id", id_check);
                                        cv.put("title", name);
                                        dbwrite.insert("relates", null, cv);
                                    }
//
                                } else {
                                    int id_not_check = MainActivity.sp.getInt("" + which, -1);
                                    System.out.println("此时取消选中的id是" + id_not_check);
                                    String sq = "delete from relates where relates_id=?and father_id=?";
                                    dbwrite.execSQL(sq, new String[]{"" + id_not_check, "" + id});

                                }

                            }
                        });
                dialog4.setNegativeButton("取消", null);

                dialog4.setPositiveButton("选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 给listview设置adapter
                         */

                        refresh_lv_relate();
//                        for(int i=0;i<list_id.size();i++){
//
//                        }
                    }
                });
                dialog4.create().show();

                break;

            case R.id.cancel_time:
                if(is_check.equals("true")) {
                    boolean has_alarm = MainActivity.sp.getBoolean("reminds" + id, false);
                    if (has_alarm) {
                        MainActivity.sp.edit().putBoolean("reminds" + id, false).commit();
                        AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                        Intent i = new Intent("alarm");
                        PendingIntent pi = PendingIntent.getBroadcast(Edit_Remind.this, Integer.valueOf(id), i, 0);
                        am.cancel(pi);//取消闹钟
                        Toast.makeText(getApplicationContext(), "闹钟已取消", Toast.LENGTH_SHORT).show();
                        tv_show_date.setText("      年   月   日");
                        tv_show_time.setText("   时    分 ");
                    } else {
                        Toast.makeText(getApplicationContext(), "您未设置闹钟", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"无效的操作",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /***
     * 动态设置listview的高度
     *
     * @param listView
     */
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


    private void refresh() {
        String sq = "select * from reminds where _id=?";
        Cursor cursor = dbread.rawQuery(sq, new String[]{"" + id});
        cursor.moveToFirst();
        String get_date = cursor.getString(cursor.getColumnIndex("date"));
        if (get_date.equals("")) {
        } else {
            tv_show_date.setText(get_date);
        }
        String get_time = cursor.getString(cursor.getColumnIndex("time"));
        if (get_time.equals("")) {
        } else {
            tv_show_time.setText(get_time);
        }

        String get_summary = cursor.getString(cursor.getColumnIndex("note"));
        tv_show_summary.setText(get_summary);

        refreshlist1(this);
    }


    public static void refreshlist1(Context context) {


        String sq = "select * from sub_tasks where father_id = ?";
        Cursor cc = dbread.rawQuery(sq, new String[]{"" + id});
//        Cursor c=dbread.query("sub_tasks",null,null,null,null,null,null);
//        adapter = new SimpleCursorAdapter(this, R.layout.listview_sub_task_cell, c, new String[]{"title"}, new int[]{R.id.tv_sub_task_title});
        adapter = new Sub_task_Adapter(context, R.layout.listview_sub_task_cell, cc);
        lv_sub_task.setAdapter(adapter);

        setListViewHeightBasedOnChildren(lv_sub_task);

    }


    public void refresh_lv_relate() {
        ;
        String sq = "select * from relates where father_id=?";
        Cursor cursor = dbread.rawQuery(sq, new String[]{"" + id});
        simpleCursorAdapter.changeCursor(cursor);
        setListViewHeightBasedOnChildren(lv_relates);

    }


}


class Sub_task_Adapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    CheckBox checkBox;

    TextView tv_sub_task_title;
    private int layout_num;
    private static final String TAG = "Adapter";

    public Sub_task_Adapter(Context context, int layout, Cursor c) {
        this.context = context;
        this.layout_num = layout;
        this.cursor = c;
        inflater = LayoutInflater.from(context);


    }

    public Cursor getCursor() {

        return cursor;
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
        cursor.moveToFirst();
        cursor.moveToPosition(position);
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listview_sub_task_cell, null);

        }

        checkBox = (CheckBox) convertView.findViewById(R.id.sub_task_checkbox);
        tv_sub_task_title = (TextView) convertView.findViewById(R.id.tv_sub_task_title);

        final String title = cursor.getString(cursor.getColumnIndex("title"));
        final int id = cursor.getInt(cursor.getColumnIndex("_id"));
        tv_sub_task_title.setText(title);
        boolean ischeck = Edit_Remind.sp.getBoolean(title, false);
        if (ischeck == true) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }


//

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    Edit_Remind.sp.edit().putBoolean(title, true).commit();

//                    String sq = "update sub_tasks set is_check=? where _id =?";
//                    Edit_Remind.dbwrite.execSQL(sq, new String[]{"true", "" + id});

//                    Edit_Remind.refreshlist1(context);
                } else {

                    Edit_Remind.sp.edit().putBoolean(title, false).commit();
//                    String sq = "update sub_tasks set is_check=? where _id =?";
//                    Edit_Remind.dbwrite.execSQL(sq, new String[]{"false", "" + id});
//                    Edit_Remind.refreshlist1(context);
                }
            }
        });


        return convertView;
    }
}