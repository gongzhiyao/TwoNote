package com.example.gongzhiyao.twonote03.Edit_log;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.database.MyDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.text.Html.toHtml;


public class Edit_Text extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Edit";
    public static final String Title = "mtitle";
    public static final String Content = "mcontent";
    public static final String Date = "mdate";
    public static final String Id = "mid";
    public static final String passwd = "mpasswd";
    private static final int PHOTO_SUCCESS = 1;
    private static final int CAMERA_SUCCESS = 2;
    private EditText mtitle, mcontent;
    private TextView mdate;
    private Intent i;
    private MyDB db;
    private SQLiteDatabase dbwrite, dbRead;
    private Button mb_bold, mb_italic, mb_underLine, mb_lighter, mb_takePhoto, mb_pick_from_photos, mbMore;
    private String CurrentPath = "";
    private static int screenWidth;
    private static int screenHeigh;
    private List<Integer> local_point = new ArrayList<Integer>();
    private List<String> uriList = new ArrayList<String>();
    boolean is_image = false;
    private Editable s_toHtml;
    private String name, html, text = "";
    private int Start1, Count1;
    private String delete_content, set_remove_passwd;
    private String set_passwd_new = "";
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__text);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        boolean night_on = MainActivity.sp.getBoolean("night_light", false);
        if (night_on) {
            change_to_night();
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //在这里把所有的notification都关闭了
        manager.cancel(0);
        manager.cancel(1);

        is_bold = false;
        is_lighter = false;
        is_underLine = false;
        is_italic = false;
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeigh = dm.heightPixels;

        Bundle b = new Bundle();
        ArrayList<String> uri = (ArrayList<String>) b.getSerializable("list");
        //进行了list的替换
        if (uri != null) {
            uriList = uri;
        }


        findView();
        attachListener();
        db = new MyDB(this);
        dbRead = db.getReadableDatabase();
        dbwrite = db.getWritableDatabase();


        i = getIntent();
        id = i.getIntExtra(Id, -1);
        if (id > -1) {//这时是修改操作
            try {
                delete_content = i.getStringExtra(Content).toString();
                mtitle.setText(i.getStringExtra(Title).toString());
                mdate.setText(i.getStringExtra(Date).toString());
                name = delete_content;
                set_remove_passwd = i.getStringExtra(passwd).toString();
            } catch (Exception e) {
                Log.i(TAG, "从数据库中获取数据时出错");
            }

            try {
                FileInputStream fis = openFileInput(name);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                char[] input = new char[fis.available()];
                isr.read(input);
                isr.close();
                fis.close();
                html = new String(input);
//                Log.i(TAG, "获取到的html格式文件是" + html);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mcontent.setText(Html.fromHtml(html, imageGetter, null));
        } else {
            mdate.setText(i.getStringExtra(Date));
        }


        mcontent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString().substring(start, start + count);

                if (s1.startsWith("<img src")) {
//                    Log.i(TAG, "此时输入的是图片");
                    is_bold = false;//在插入图片时把所有的属性更改为false，重置
                    is_lighter = false;
                    is_underLine = false;
                    is_italic = false;
                    is_image = true;
                    local_point.add(start);
                    String test_s = s1.substring(10, s1.length() - 4);
//                    Log.i(TAG, "得到的uri是" + test_s);
                    uriList.add(test_s);

                } else {
                    if (count > 1) {
                        for (int i1 = start; i1 < start + count; i1++) {
                            onTextChanged(s, i1, before, 1);

                        }
                    }
//                    Log.i(TAG, "此时输入的是文字");
                }


                Start1 = start;
                Count1 = count;
                if (before > 0) {
                    //在删除时故意把所有的字体设置都归零,这个功能可开启，可关闭
//                    is_bold = false;
//                    is_lighter = false;
//                    is_underLine = false;
//                    is_italic = false;
//                    Log.i(TAG,"所有的设置归零");

                    for (int i = local_point.size() - 1; i >= 0; i--) {//在这里要确保，照片删除后，节点也被删除，不然在保存时会空指针错误
                        if (start <= local_point.get(i) + 1) { //这里试了下，如果是不加1，那么就得多删除一位才能删除节点
                            local_point.remove(i);
                            uriList.remove(i);
                        }
                    }

                }

//                Log.i(TAG, "此时s的值是" + s);
//                Log.i(TAG, "此时start是" + start);
//                Log.i(TAG, "此时before是" + before);
//                Log.i(TAG, "此时的count是" + count);


            }


            @Override
            public void afterTextChanged(Editable s) {

                text = s.toString();
//                Log.i(TAG, "此时s的值是" + text);
                ////原来是用s.length来约束字体属性，后来发现使用Start和count实时设置更好
                if (is_bold == true) {

                    for (int i = Start1; i < Start1 + Count1; i++) {
                        s.setSpan(new StyleSpan(Typeface.BOLD), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }


//                    Log.i(TAG, "粗体依然存在");
                }
                if (is_italic == true) {

                    for (int i = Start1; i < Start1 + Count1; i++) {
                        s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

//                    Log.i(TAG, "斜体依然存在");
                }
                if (is_underLine == true) {


                    for (int i = Start1; i < Start1 + Count1; i++) {
                        s.setSpan(new UnderlineSpan(), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

//                    Log.i(TAG, "下划线依然存在");
                }
                if (is_lighter == true) {

                    for (int i = Start1; i < Start1 + Count1; i++) {
                        s.setSpan(new ForegroundColorSpan(Color.GREEN), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }


//                    Log.i(TAG, "高亮依然存在");
                }

                s_toHtml = s;


            }


        });


    }

    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
//            Log.i(TAG, "此时得到的source是" + source);

            Drawable d = null;
            try {

                d = Drawable.createFromStream(getContentResolver().openInputStream(Uri.parse(source)), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int width1 = d.getIntrinsicWidth() * 3;
            int height1 = d.getIntrinsicHeight() * 3;
            float scanleWidth = 0, scanleHeight = 0;
            if (width1 > height1) {
                //横屏的图片
                if (width1 > screenWidth / 2) {
                    scanleWidth = (float) (((float) screenWidth / (float) width1) - 0.01);
                    scanleHeight = scanleWidth;
                } else {
                    scanleWidth = (float) screenWidth / (float) 2 / (float) width1;
                    scanleHeight = scanleWidth;
                }

            }
            if (width1 <= height1) {//刚开始的时候是使用的int类型的来除，后来发现不精确，所以在这里全都转化成了float
                //竖屏的图片
                if (width1 >= screenWidth / 2) {
                    scanleWidth = (float) (((float) screenWidth / (float) width1) - 0.01);
//                    Log.i(TAG, "缩小比例是多少" + scanleWidth);
                    scanleHeight = scanleWidth;
                } else {
                    scanleWidth = (float) screenWidth / (float) 2 / (float) width1;
                    scanleHeight = scanleWidth;
                }
            }


            ///这一行设置了显示时，图片的大小
            d.setBounds(0, 0, (int) (width1 * scanleWidth), (int) (height1 * scanleHeight));
//            Log.i(TAG, "传过来的照片的大小是" + d.getIntrinsicWidth() + "    " + d.getIntrinsicHeight());
            return d;
        }
    };

    private void attachListener() {
        mbMore.setOnClickListener(this);
        mb_italic.setOnClickListener(this);
        mb_pick_from_photos.setOnClickListener(this);
        mb_underLine.setOnClickListener(this);
        mb_bold.setOnClickListener(this);
        mb_lighter.setOnClickListener(this);
        mb_takePhoto.setOnClickListener(this);


    }

    private void findView() {


        mbMore = (Button) findViewById(R.id.btn_more);
        mtitle = (EditText) findViewById(R.id.et_title);
        mcontent = (EditText) findViewById(R.id.et_content);
        mdate = (TextView) findViewById(R.id.et_date);
        mb_bold = (Button) findViewById(R.id.btn_bold);
        mb_italic = (Button) findViewById(R.id.btn_italic);
        mb_lighter = (Button) findViewById(R.id.btn_lighter);
        mb_underLine = (Button) findViewById(R.id.btn_underLine);
        mb_takePhoto = (Button) findViewById(R.id.btn_takePhoto);
        mb_pick_from_photos = (Button) findViewById(R.id.btn_Pick_from_Photos);

    }

    /**
     * 这里是其中自定义菜单
     *
     * @param view
     */

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.menu_edit_more, popupMenu.getMenu());
        // menu的item点击事件

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    /**
                     * 这里是自定义菜单的操作
                     */
                    case R.id.action_clear:
//                        Log.i(TAG, "进行清空操作");

                        if (id > -1) {
                            //修改时清空

                            mtitle.setText("");
                            mcontent.setText("");
                            Toast.makeText(getApplicationContext(), "内容已清空", Toast.LENGTH_SHORT).show();
                        } else {
                            //新建时清空
                            mtitle.setText("");
                            mcontent.setText("");
                            Toast.makeText(getApplicationContext(), "内容已清空", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.action_delete:
                        Log.i(TAG, "进行删除操作");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(Edit_Text.this);
                        dialog.setTitle("提示：").setMessage("您确定要删除这条Note吗？");
                        dialog.setNegativeButton("放弃", null);
                        dialog.setPositiveButton("我意已决！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (id > -1) {
                                    String sq = "delete from notes where _id=?";
                                    dbwrite.execSQL(sq, new String[]{"" + id});
                                    deleteFile(delete_content);
                                    finish();
                                } else {
                                    finish();
                                }
                            }
                        });

                        dialog.create().show();

                        break;
                    case R.id.action_passwd:
                        if (id > -1) {
                            //修改时，更改密码状态后直接修改数据库
                            if (set_remove_passwd.equals("")) {
                                //没有密码，设置密码
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Edit_Text.this);
                                dialog2.setTitle("您正在设置阅读密码！");
                                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                                View edit = li.inflate(R.layout.edit_passwd, null);
                                final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                                dialog2.setView(edit);//
                                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String passwd = et_passwd.getText().toString();
                                        if (passwd.equals("")) {
                                            Toast.makeText(getApplicationContext(), "密码设置失败,密码不能为空!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.i(TAG, "获得的密码是" + passwd);
                                            String sq = "update notes set passwd=? where _id=?";
                                            dbwrite.execSQL(sq, new String[]{passwd, "" + id});
                                            Toast.makeText(getApplicationContext(), "密码设置成功", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                dialog2.create().show();


                            } else {
                                //有密码,清除密码
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Edit_Text.this);
                                dialog2.setTitle("您正在取消阅读密码！");
                                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                                View edit = li.inflate(R.layout.edit_passwd, null);
                                final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);
                                dialog2.setView(edit);//
                                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String passwd = et_passwd.getText().toString();
                                        if (set_remove_passwd.equals(passwd)) {
                                            String sq = "update notes set passwd=? where _id=?";
                                            dbwrite.execSQL(sq, new String[]{"", "" + id});
                                            Toast.makeText(getApplicationContext(), "密码已成功取消", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getApplicationContext(), "密码输入错误,阅读密码仍在保护您的隐私", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                dialog2.create().show();


                            }
                        } else {

                            //新建操作，只有设置密码
                            //然后在保存时一并将密码写入

                            if (set_passwd_new.equals("")) {
                                //在新建中设置密码
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Edit_Text.this);
                                dialog2.setTitle("您正在设置阅读密码！");
                                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                                View edit = li.inflate(R.layout.edit_passwd, null);
                                final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);

                                dialog2.setView(edit);//
                                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        finish();
                                    }
                                });
                                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String passwd = et_passwd.getText().toString();
                                        if (passwd.equals("")) {
                                            Toast.makeText(getApplicationContext(), "密码设置失败,密码不能为空!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.i(TAG, "获得的密码是" + passwd);
                                            set_passwd_new = passwd;
                                            Toast.makeText(getApplicationContext(), "密码设置成功", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                dialog2.create().show();
                            } else {
                                //在未保存前清除密码

                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Edit_Text.this);
                                dialog2.setTitle("您正在取消阅读密码！");
                                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                                View edit = li.inflate(R.layout.edit_passwd, null);
                                final EditText et_passwd = (EditText) edit.findViewById(R.id.ed_passwd);

                                dialog2.setView(edit);//
                                dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String passwd = et_passwd.getText().toString();
                                        if (passwd.equals(set_passwd_new)) {
                                            set_passwd_new = "";
                                            Toast.makeText(getApplicationContext(), "阅读密码已成功取消", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "密码错误！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog2.create().show();

                            }
                        }
                        break;
                }

                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_more:
                showPopupMenu(mbMore);
                break;

            case R.id.btn_Pick_from_Photos:
                Intent getImage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, PHOTO_SUCCESS);
                break;


            case R.id.btn_takePhoto:
                Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");//文件不存在，就创建
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CurrentPath = file.getAbsolutePath();//获得绝对路径
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//指定输出路径
                startActivityForResult(getImageByCamera, CAMERA_SUCCESS);
                break;
            case R.id.btn_bold:
                if (is_bold == false) {
                    is_bold = true;
                    Toast.makeText(getApplicationContext(), "加粗", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已加粗");
//                    position_bold = text.length();
//                    Log.i(TAG, "粗体的开始位置是" + position_bold);
                } else if (is_bold == true) {
                    is_bold = false;
                    Toast.makeText(getApplicationContext(), "已取消加粗", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已取消加粗");
                }


                break;
            case R.id.btn_italic:
                if (is_italic == false) {
                    is_italic = true;
                    Toast.makeText(getApplicationContext(), "斜体", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已设置为斜体");
//                    position_italic = text.length();
//                    Log.i(TAG, "斜体的开始位置是" + position_italic);
                } else {
                    is_italic = false;
                    Toast.makeText(getApplicationContext(), "已取消斜体", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已取消斜体");
                }
                break;
            case R.id.btn_lighter:
                if (is_lighter == false) {
                    is_lighter = true;
                    Toast.makeText(getApplicationContext(), "高亮", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已设置高亮");
//                    position_lighter = text.length();
//                    Log.i(TAG, "高亮的开始位置是" + position_lighter);
                } else {
                    is_lighter = false;
                    Toast.makeText(getApplicationContext(), "已取消高亮", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已取消高亮");
                }
                break;
            case R.id.btn_underLine:
                if (is_underLine == false) {
                    is_underLine = true;
                    Toast.makeText(getApplicationContext(), "下划线", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已添加下划线");
//                    position_underLine = text.length();
//                    Log.i(TAG, "下划线的开始位置是" + position_underLine);
                } else {
                    is_underLine = false;
                    Toast.makeText(getApplicationContext(), "已取消下划线", Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "已取消下划线");
                }
                break;


        }

    }

    public File getMediaDir() {//在sd卡上创建一个目录
        File dir = new File(Environment.getExternalStorageDirectory() + "/TwoNote", "TwoNotes_Image");//
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    String File_name;
    String ss;
    StringBuffer sb;
    String show_title;
    String date;
    String s_cut;


    private void saveNote() {//这里是保存笔记的地方
        ContentValues cv = new ContentValues();
        ContentValues cv_relate = new ContentValues();
        show_title = mtitle.getText().toString();
        if (show_title.equals("")) {
            show_title = "无标题";
        }
        cv.put("name", show_title);
        cv_relate.put("name", show_title);

        //在发生异常之前获得日期
        date = mdate.getText().toString();
        Log.i(TAG, "此时获得的标题是" + show_title);

        ss = toHtml(s_toHtml);
        sb = new StringBuffer(ss);

        /**
         * 在这里把密码输入到数据库中
         *
         */


//        Log.i(TAG, "得到的未转换前的是" + s_toHtml);
//        Log.i(TAG, "此时list中记录了几个" + local_point.size());
//        Log.i(TAG, "第一种方案此时获得的html文本是" + ss);


        //这里需要注意如果有图片的话
        int Start = 0;

        /**
         * 在这里获取到第一个图片的uri，如果有的话
         */
        if (uriList.size() >= 1) {
            String Image_first_uri = uriList.get(0);
            cv.put("image", Image_first_uri);
//            Log.i(TAG, "第一张图片的uri已经写入数据库");
//            Log.i(TAG, "此时上传到数据库的uri是" + Image_first_uri.toString());

        }
        /**
         * 在这里会截取正文的部分信息，显示到item中
         */
        String s = mcontent.getText().toString();
//        Log.i(TAG, "此时获得的文本信息是" + s);
        int index = s.indexOf("<img");
        if (index != -1) {
//            Log.i(TAG, "截取到的第一个图片的位置是" + index);
            s_cut = s.substring(0, index);

        } else {
            s_cut = s;
        }

        cv.put("summary", s_cut);

//        Log.i(TAG, "截取后的信息是" + s_cut);

        for (int i = 0; i < local_point.size(); i++) {//插入的图片数

            if (i == 0) {
                Start = local_point.get(0);
            } else {
//
                Start = local_point.get(i);

//                Log.i(TAG, "start:" + Start);
//                        Log.i(TAG,"length:"+length);
            }

            int a = sb.indexOf("null", Start);
//            Log.i(TAG, "这里得到了null的位置" + a);
            sb.replace(a, a + 4, uriList.get(i));
//            Log.i(TAG, "此时的sb是" + sb.toString());

        }
        String html = sb.toString();
        if (id > -1) {
            File_name = name;

        } else {
            File_name = System.currentTimeMillis() + ".txt";

        }

        try {
            FileOutputStream fos = openFileOutput(File_name, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(html);
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Log.i(TAG, "已经成功写入");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        cv.put("content", File_name);

        if (id > -1) {
            /**
             * 修改
             */
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = f.format(new Date());
            cv.put("date", time);
            dbwrite.update("relates", cv_relate, "relates_id=?", new String[]{id + ""});
            dbwrite.update("notes", cv, "_id=?", new String[]{id + ""});

        } else {
            /**
             * 新建
             */
            cv.put("passwd", set_passwd_new);
            cv.put("date", mdate.getText().toString());
            dbwrite.insert("notes", null, cv);

            /**
             * 保存连接页面的数据库
             */

        }


    }


    /*******************************************************************************************************/
    @Override
    protected void onPause() {//在暂停时保存
        super.onPause();
        Log.i(TAG, "编辑界面暂停");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                saveNote();
            } catch (Exception e) {
//                Log.i(TAG, "日志保存时出错");


                /**
                 * 因为在日志内容不更改的情况下保存，监测输入的ss为空，会报错，所以在它报错时，finish它
                 * 同时还要满足标题的更改和日期的更改，所以在catch块里面添加如下语句
                 */


                /**
                 * 问题在这里，不能单独保存标题的问题
                 */

                if (id > -1) {//这种情况适用于修改时，没有对正文进行修改造成的异常的补救方法
                    String title = mtitle.getText().toString();
                    dbwrite.execSQL("update relates set title=? where relates_id=?", new String[]{title, id + ""});
                    dbwrite.execSQL("update notes set name=? " + " where _id=?", new String[]{title, id + ""});
                    /**
                     * 在没有更改真正的正文的情况下，如果只更改标题或标题也不更改，不会影响到之前的时间
                     */
//
//                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//
//                    String time = f.format(new Date());
//                    dbwrite.execSQL("update notes set date=? " + " where _id=?", new String[]{time, id + ""});
                } else {//这种则是新建时，只是添加了标题，而没有对正文进行书写时造成的异常的补救措施

                    ContentValues cv = new ContentValues();
                    cv.put("name", show_title);
                    cv.put("date", date);
                    cv.put("passwd", set_passwd_new);


                    File_name = System.currentTimeMillis() + ".txt";
                    try {
                        FileOutputStream fos = openFileOutput(File_name, MODE_PRIVATE);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                        osw.write("");
                        osw.flush();
                        fos.flush();
                        osw.close();
                        fos.close();
//                        Log.i(TAG, "已经成功写入");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    cv.put("content", File_name);
                    dbwrite.insert("notes", null, cv);


                }


                finish();
            }

        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbwrite.close();
        dbRead.close();
//        Log.i(TAG, "编辑界面销毁");
    }

    /**
     * 具体的onSaveInstanceState的用法还不知道，但是暂时不能添加缩略图，因为会卡顿
     *
     * @param outState
     * @param outPersistentState
     */


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        outState.putSerializable("uri_list", (Serializable) uriList);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    File f;

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_SUCCESS:
                    //获得图片的uri
                    Uri originalUri = intent.getData();
                    Bitmap bitmap = null;
                    try {
                        Bitmap originalBitmap = BitmapFactory.decodeStream(resolver.openInputStream(originalUri));


                        f = new File(getMediaDir(), System.currentTimeMillis() + ".jpg");
                        if (!f.exists()) {
                            try {
                                f.createNewFile();
                                FileOutputStream fos = new FileOutputStream(f);
                                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        bitmap = resizeImage(originalBitmap, 1080, 1800);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(Edit_Text.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String tempUrl = "<img src=\"" + Uri.fromFile(f) + "\" />";
                        SpannableString spannableString = new SpannableString(tempUrl);
                        //  用ImageSpan对象替换face

                        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置
                        int index = mcontent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = mcontent.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");//添加换行符
                        } else {
                            edit_text.insert(index, spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");//这是插入后是否添加换行符，可以去掉，是个可选项
                        }
                    } else {
                        Toast.makeText(Edit_Text.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CAMERA_SUCCESS:


                    File sd = Environment.getExternalStorageDirectory();
                    boolean can_write = sd.canWrite();

//                    Log.i(TAG, "sd卡是否可读" + can_write);
//                    Bundle extras = intent.getExtras();
//                    Bitmap originalBitmap1 = (Bitmap) extras.get("data");
//                    Uri uri=intent.getData();
//                    Log.i(TAG, "currentPath的值是" + CurrentPath);
                    Bitmap originalBitmap1 = BitmapFactory.decodeFile(CurrentPath);
                    if (originalBitmap1 != null) {
                        bitmap = resizeImage(originalBitmap1, 1080, 1800);

                        //根据Bitmap对象创建ImageSpan对象
                        ImageSpan imageSpan = new ImageSpan(Edit_Text.this, bitmap);
                        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
                        String tempUrl = "<img src=\"" + Uri.fromFile(new File(CurrentPath)) + "\" />";
                        SpannableString spannableString = new SpannableString(tempUrl);
                        //  用ImageSpan对象替换face
//                        spannableString.setSpan(imageSpan, 0, "[local]1[local]".length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //将选择的图片追加到EditText中光标所在位置

                        int index = mcontent.getSelectionStart(); //获取光标所在位置
                        Editable edit_text = mcontent.getEditableText();
                        if (index < 0 || index >= edit_text.length()) {
                            edit_text.append(spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");
                        } else {
                            edit_text.insert(index, spannableString);
                            edit_text.insert(index + spannableString.length(), "\n");
                        }
                    } else {
                        Toast.makeText(Edit_Text.this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    }

                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 夜间模式
     */


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


    /**
     * 图片缩放
     *
     * @param originalBitmap 原始的Bitmap
     * @param newWidth       自定义宽度
     * @return 缩放后的Bitmap
     */
    public static Bitmap resizeImage(Bitmap originalBitmap, int newWidth, int newHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Log.i(TAG, "照片源的宽和高分别是" + width + "     " + height);
        Log.i(TAG, "系统的尺寸是" + screenWidth + "   " + screenHeigh);
        float scanleWidth = 0;
        float scanleHeight = 0;
        if (width > height) {
            //横屏的图片
            if (width > screenWidth / 2) {
                scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                scanleHeight = scanleWidth;
            } else {
                scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                scanleHeight = scanleWidth;
            }

        }
        if (width <= height) {//刚开始的时候是使用的int类型的来除，后来发现不精确，所以在这里全都转化成了float
            //竖屏的图片
            if (width >= screenWidth / 2) {
                scanleWidth = (float) (((float) screenWidth / (float) width) - 0.01);
                Log.i(TAG, "缩小比例是多少" + scanleWidth);
                scanleHeight = scanleWidth;
            } else {
                scanleWidth = (float) screenWidth / (float) 2 / (float) width;
                scanleHeight = scanleWidth;
            }
        }
        /****************************************在这里可以把宽高调节******************************************************/

        //创建操作图片用的matrix对象 Matrix
        Matrix matrix = new Matrix();
        // 缩放图片动作
        //matrix.postScale(scanleWidth, scanleHeight);
        matrix.postScale(scanleWidth, scanleHeight);


        //旋转图片 动作
//        matrix.postRotate(45);
        // 创建新的图片Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
        // 用完了记得回收
//        resizedBitmap.recycle();
        return resizedBitmap;
    }


    private boolean is_bold = false, is_underLine = false, is_italic = false, is_lighter = false;
//    private int position_bold = 1;
//    private int position_italic = 1;
//    private int position_lighter = 1;
//    private int position_underLine = 1;


}














