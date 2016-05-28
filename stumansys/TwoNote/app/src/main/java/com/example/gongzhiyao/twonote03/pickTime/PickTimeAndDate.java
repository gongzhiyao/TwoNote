package com.example.gongzhiyao.twonote03.pickTime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.gongzhiyao.twonote03.R;

import java.util.ArrayList;
import java.util.List;


/**
 *在本程序中没有使用
 */
public class PickTimeAndDate extends AppCompatActivity {



    PickerView pv_year, pv_month, pv_day, pv_pm_am, pv_hour, pv_min;
    int time_month;
    int time_year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_pick_time_and_date);
        pv_day = (PickerView) findViewById(R.id.pv_day);
        pv_hour = (PickerView) findViewById(R.id.pv_hour);
        pv_min = (PickerView) findViewById(R.id.pv_min);
        pv_month = (PickerView) findViewById(R.id.pv_month);
        pv_pm_am = (PickerView) findViewById(R.id.pv_pm_am);
        pv_year = (PickerView) findViewById(R.id.pv_year);

        List<String> year = new ArrayList<String>();
        List<String> month = new ArrayList<String>();
        List<String> day = new ArrayList<String>();
        List<String> pm_am = new ArrayList<String>();
        List<String> hour = new ArrayList<String>();
        List<String> min = new ArrayList<String>();


        for (int i = 2010; i <= 2050; i++) {
            year.add("" + i);
        }
        pv_year.setData(year);
        pv_year.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                time_year = Integer.parseInt(text);
                /**
                 * 在这里判断他是否是闰年
                 */
            }
        });

        for (int i = 1; i <= 12; i++) {
            month.add("" + i);
        }
        pv_month.setData(month);
        pv_month.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                time_month = Integer.parseInt(text);
                /**
                 * 判断是否为闰月或是13579月
                 */
            }
        });


        /***
         * 后，面还要持续添加别的
         */

        if (time_month == 1 || time_month == 3 || time_month == 5 || time_month == 7 || time_month == 8 || time_month == 10 || time_month == 12) {
            for (int i = 1; i <= 31; i++) {
                day.add("" + i);
            }
        } else if (time_month == 4 || time_month == 6 || time_month == 9 || time_month == 11) {
            for (int i = 1; i <= 30; i++) {
                day.add("" + i);
            }
        } else if ((time_year % 4 == 0 && time_year % 100 != 0) || time_year % 400 == 0) {
            for (int i = 1; i <= 29; i++) {
                day.add("" + i);
            }
        } else {
            for (int i = 1; i <= 28; i++) {
                day.add("" + i);
            }
        }


        pv_day.setData(day);

        pv_day.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                /**
                 * 监听器
                 */
            }
        });

        pm_am.add("上午");
        pm_am.add("下午");
        pv_pm_am.setData(pm_am);
        pv_pm_am.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                /**
                 * 监听器
                 */
            }
        });


        for(int i=1;i<=12;i++){
            hour.add(""+i);

        }
        pv_hour.setData(hour);
        pv_hour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                /**
                 *
                 */
            }
        });


        for(int i=0;i<=59;i++){
            min.add(""+i);

        }
        pv_min.setData(min);
        pv_min.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                /**
                 *
                 */
            }
        });









    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pick_time_and_date, menu);
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
}
