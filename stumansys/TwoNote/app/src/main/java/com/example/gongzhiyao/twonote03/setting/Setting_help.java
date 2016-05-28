package com.example.gongzhiyao.twonote03.setting;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.gongzhiyao.twonote03.R;

public class Setting_help extends AppCompatActivity {

    Button btn_notes_help,btn_reminds_help,btn_record_help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_help);
        btn_notes_help= (Button) findViewById(R.id.btn_notes_help);
        btn_record_help= (Button) findViewById(R.id.btn_record_help);
        btn_reminds_help= (Button) findViewById(R.id.btn_reminds_help);
        btn_notes_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("http://m.blog.csdn.net/article/details?id=51519372");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        btn_reminds_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                http://m.blog.csdn.net/article/details?id=51519746
                Uri uri1=Uri.parse("http://m.blog.csdn.net/article/details?id=51519967");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri1);
                startActivity(intent);
            }
        });


        btn_record_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1=Uri.parse("http://m.blog.csdn.net/article/details?id=51519746");
                Intent intent=new Intent(Intent.ACTION_VIEW,uri1);
                startActivity(intent);
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting_help, menu);
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
