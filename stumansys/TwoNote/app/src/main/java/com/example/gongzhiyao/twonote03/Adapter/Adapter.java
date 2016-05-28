package com.example.gongzhiyao.twonote03.Adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gongzhiyao.twonote03.MainActivity;
import com.example.gongzhiyao.twonote03.R;

import net.tsz.afinal.FinalBitmap;

/**
 * Created by 宫智耀 on 2016/5/21.
 */
public class Adapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    private int layout_num;
    public TextView tv_title;
    public TextView tv_content;
    public TextView tv_date;
    private FinalBitmap fb;
    private static final String TAG = "Adapter";

    ImageView iv;
    ImageView iv_collection;

    public Adapter(Context context, int layout, Cursor c) {
//        super(context, layout, c, from, to);
        this.cursor = c;
        this.context = context;
        this.layout_num = layout;
        inflater = LayoutInflater.from(context);
        fb = FinalBitmap.create(context);//初始化FinalBitmap模块

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
    public View getView(int position, View listlayout, ViewGroup parent) {
//        Log.i(TAG, "getview被执行，position是" + position);
        cursor.moveToPosition(position);


        if (listlayout == null) {

            listlayout = inflater.inflate(layout_num, null);
//             tv_title= (ScrollForeverTextView) listlayout.findViewById(R.id.tv_title);

            listlayout.setTag(iv);


        } else {
            iv = (ImageView) listlayout.getTag();

        }
        tv_title = (TextView) listlayout.findViewById(R.id.tv_title);
        tv_content = (TextView) listlayout.findViewById(R.id.tv_content);
        tv_date = (TextView) listlayout.findViewById(R.id.tv_date);
        iv = (ImageView) listlayout.findViewById(R.id.iv_show);
        iv_collection = (ImageView) listlayout.findViewById(R.id.collection);

        String title = cursor.getString(cursor.getColumnIndex("name"));
        String summary = cursor.getString(cursor.getColumnIndex("summary"));
        String date = cursor.getString(cursor.getColumnIndex("date"));
        String uri = cursor.getString(cursor.getColumnIndex("image"));
        String passwd = cursor.getString(cursor.getColumnIndex("passwd"));
        String collection = cursor.getString(cursor.getColumnIndex("is_collect"));

        int id=cursor.getInt(cursor.getColumnIndex("_id"));
        MainActivity.sp.edit().putInt(""+position,id).commit();


        if (collection.equals("true")) {
            iv_collection.setImageResource(R.drawable.collection);
        }

        boolean only_title=MainActivity.sp.getBoolean("only_title",false);
        if(only_title){
            if (!passwd.equals("")) {
                summary = "内容已被加密";
                tv_title.setText(title);

                tv_content.setText(summary);
                tv_content.setTextColor(Color.RED);
                tv_date.setText(date);
                // fb.display(iv, uri);
                iv.setImageResource(R.drawable.lock);

            } else {
                tv_title.setText(title);

                tv_content.setText("");
                tv_date.setText(date);
                fb.display(iv, uri);
            }

        }else {if (!passwd.equals("")) {
            summary = "内容已被加密";
            tv_title.setText(title);

            tv_content.setText(summary);
            tv_content.setTextColor(Color.RED);
            tv_date.setText(date);
            // fb.display(iv, uri);
            iv.setImageResource(R.drawable.lock);

        } else {
            tv_title.setText(title);

            tv_content.setText(summary);
            tv_date.setText(date);
            fb.display(iv, uri);
        }

        }

        return listlayout;
    }





//    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        String column = MediaStore.Images.Media.DATA;
//        String[] projection = {column};
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                int index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


//    private Bitmap resizeImage_main(Bitmap originalBitmap, int newWidth, int newHeight) {
//        int width = originalBitmap.getWidth();
//        int height = originalBitmap.getHeight();
//        //定义欲转换成的宽、高
////            int newWidth = 200;
////            int newHeight = 200;
//        //计算宽、高缩放率
//        float scanleWidth = (float) newWidth / width;
//        float scanleHeight = (float) newHeight / height;
//        //创建操作图片用的matrix对象 Matrix
//        Matrix matrix = new Matrix();
//        // 缩放图片动作
//        matrix.postScale(scanleWidth, scanleHeight);
//        //旋转图片 动作
//        //matrix.postRotate(45);
//        // 创建新的图片Bitmap
//        Bitmap resizedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true);
//        return resizedBitmap;
//    }
//
//
}

//class backTask extends AsyncTask<Cursor,Void,Void>{
//
//    Cursor cursor;
//
//    private LayoutInflater inflater;
//    private int layout_num;
//
//    @Override
//    protected Void doInBackground(Cursor... params) {
//        cursor=params[0];
//        String title = cursor.getString(cursor.getColumnIndex("name"));
//        String summary = cursor.getString(cursor.getColumnIndex("summary"));
//        String date = cursor.getString(cursor.getColumnIndex("date"));
//        String uri = cursor.getString(cursor.getColumnIndex("image"));
//        if(uri!=null){
//            Uri path=Uri.parse(uri);
//            String location=Adapter.getImageAbsolutePath(MainActivity.this, path);
//            System.out.println("此时的path是"+location);
//
////            Bitmap originalBitmap=BitmapFactory.decodeFile(String.valueOf(new File(location)));
////            Bitmap bmp=resizeImage_main(originalBitmap,90,90);
//            Bitmap bmp=decodeBitmap(location,400,400);
////            *
////             * 仍然还是会卡顿只是轻了一点
////
////
//
//        }
//
//
//
//        return null;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//
//
//
//    }
//}



