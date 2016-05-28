package com.example.gongzhiyao.twonote03.Recent_log;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.gongzhiyao.twonote03.Edit_log.Edit_Text;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.database.MyDB;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 宫智耀 on 2016/4/10.
 */
public class ListView_show_recent_log extends Activity implements View.OnClickListener {
    private Button mbAdd;
    private MyDB db;
    private SQLiteDatabase dbwrite, dbRead;
    private SimpleCursorAdapter adapter;
    private ListView lv;
    public static final int Request_edit_code = 1;
    public static final int Request_add_code = 2;
    private static final String TAG = "SHOWLOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        findViews();
        db = new MyDB(this);
        dbRead = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();


        adapter = new SimpleCursorAdapter(this, R.layout.drawer_layout, null, new String[]{"name", "date"}, new int[]{R.id.tv_title, R.id.tv_date});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                Intent i = new Intent(ListView_show_recent_log.this, Edit_Text.class);
                i.putExtra(Edit_Text.Title, c.getString(c.getColumnIndex("name")));
                i.putExtra(Edit_Text.Content, c.getString(c.getColumnIndex("content")));
                i.putExtra(Edit_Text.Date, c.getString(c.getColumnIndex("date")));
                i.putExtra(Edit_Text.Id, c.getInt(c.getColumnIndex("_id")));
                startActivityForResult(i, Request_edit_code);
            }
        });


        mbAdd.setOnClickListener(this);
        refreshList();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case Request_edit_code:
            case Request_add_code: //无论是新建还是修改都要刷新一下列表

                if (resultCode == Activity.RESULT_OK) {
                    refreshList();//如果是add操作，就刷.................................新列表
//                    Log.i(TAG, "刷新列表成功");
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbRead.close();
        dbwrite.close();
    }


    private void refreshList() {
        Cursor c = dbRead.query("notes", null, null, null, null, null, null);
        adapter.changeCursor(c);
//        Log.i(TAG, "刷新了一次列表");
    }

    private void findViews() {
        lv = (ListView) findViewById(R.id.lv);
        mbAdd = (Button) findViewById(R.id.btn_add);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日    hh:mm:ss");
                String time = f.format(new Date());
                Intent i = new Intent(ListView_show_recent_log.this, Edit_Text.class);
                i.putExtra(Edit_Text.Date, time);
                startActivityForResult(i, Request_add_code);
//                Log.i(TAG, "获取到的时间是" + time);
                break;

        }
    }
}
