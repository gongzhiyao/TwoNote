package com.example.gongzhiyao.twonote03.Recordlist_fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.Text_Forever_scroll.ScrollForeverTextView;
import com.example.gongzhiyao.twonote03.database.MyDB;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Record_sound_list.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Record_sound_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Record_sound_list extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "sound_list";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner spinner;
    private SearchView searchView;
    private ListView Record_list;
    private ArrayAdapter<String> arrayAdapter;
    private MyDB DB;
    private SQLiteDatabase dbWrite;
    private static SQLiteDatabase dbRead;
    private static SimpleCursorAdapter adapter;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Button ibstart_stop, ib_reset;
    private int year_int, month_int;
    private String month, year;
    private String search_word;
    com.example.gongzhiyao.twonote03.Text_Forever_scroll.ScrollForeverTextView tv_player_title;
    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Record_sound_list.
     */
    // TODO: Rename and change types and number of parameters
    public static Record_sound_list newInstance(String param1, String param2) {
        Record_sound_list fragment = new Record_sound_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Record_sound_list() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(TAG, "录音列表被创建");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v2 = inflater.inflate(R.layout.fragment_record_sound_list, container, false);
        ib_reset = (Button) v2.findViewById(R.id.ib_reset_player);
        ib_reset.requestFocus();
        LinearLayout ll = (LinearLayout) v2.findViewById(R.id.request_layout);
        ll.requestFocus();
        spinner = (Spinner) v2.findViewById(R.id.Record_spinner);


        searchView = (SearchView) v2.findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("搜索");

//        et_Record_search = (EditText) v2.findViewById(R.id.et_Record_search);
        ibstart_stop = (Button) v2.findViewById(R.id.ib_start_stop_player);
        tv_player_title = (ScrollForeverTextView) v2.findViewById(R.id.tv_Player_Title);

        Record_list = (ListView) v2.findViewById(R.id.Record_Sound_list);
        seekBar = (SeekBar) v2.findViewById(R.id.seekbar_Record_player);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String sq = "select * from media where date like? or title like?";
                Cursor c = dbRead.rawQuery(sq, new String[]{"%" + search_word + "%", "%" + search_word + "%"});
                adapter.changeCursor(c);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_word = newText;
                return false;
            }
        });


        //给软键盘设定监听器

//        et_Record_search.setOnKeyListener(onKeyListener);
        /**
         * 在这里获取到真实的日期，给spinner时间的参考
         */
        SimpleDateFormat f = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
        String time = f.format(new Date());
//        Log.i(TAG, "现在的时间是" + time);
        month = time.substring(5, 6);
//        Log.i(TAG, "月份是" + month);
        month_int = Integer.parseInt(month);
//        Log.i(TAG, "月份的整形表达式是" + month_int);
        year = time.substring(0, 4);
//        Log.i(TAG, "年份是" + year);
        year_int = Integer.parseInt(year);
//        Log.i(TAG, "年份的整形表达式是" + year_int);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                /**
                 * hide注意的是只有在mediaplayer不为空的情况下，才可以滑动且将音频设置到指定位置
                 */


                // fromUser判断是用户改变的滑块的值
                if (fromUser == true) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


        if (mediaPlayer == null) {
            seekBar.setClickable(false);
            seekBar.setFocusable(false);
//            Log.i(TAG, "设置为不可点击");
        } else if (mediaPlayer != null) {
            seekBar.setClickable(true);
        }


        ibstart_stop.setOnClickListener(clicklistener);
        ib_reset.setOnClickListener(clicklistener);


        DB = new MyDB(getActivity());
        dbRead = DB.getReadableDatabase();
        dbWrite = DB.getWritableDatabase();


        String str[] = new String[]{
                "所有", "本月", "最近三个月", "最近五个月", "过去一年"
        };
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, str);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinner_click);


        Cursor cursor = dbRead.query("media", null, null, null, null, null, null);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.record_list_cell, cursor, new String[]{"title", "date"}, new int[]{R.id.tv_Record_title, R.id.tv_Record_date});
        Record_list.setAdapter(adapter);
        /**
         * 然后要写的有，给list添加上监听器，还有需要刷新cursor，
         */
        Record_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i(TAG, "条目被点击");

                Cursor c = dbRead.query("media", null, null, null, null, null, null);
                c.moveToPosition(position);
                title = c.getString(c.getColumnIndex("title"));
                date = c.getString(c.getColumnIndex("date"));
                path = c.getString(c.getColumnIndex("path"));
//                Log.i(TAG, "获得的path是" + path);
                /**
                 * 在里面应该初始化播放器
                 */

                if (mediaPlayer == null) {
                    init();
                    tv_player_title.setText(title);
                    mediaPlayer.start();
                    //启动
                    handler.post(updateThread);

                } else if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    seekBar.setProgress(0);
                    init();
                    mediaPlayer.start();
                    //启动
                    handler.post(updateThread);

                }


            }
        });

        Record_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position1, long id) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("提醒")
                        .setMessage("您确定要删除这条录音吗？")
                        .setNegativeButton("不是", null)
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor c = dbRead.query("media", null, null, null, null, null, null);
                                c.moveToPosition(position1);
                                int id = c.getInt(c.getColumnIndex("_id"));
                                String path = c.getString(c.getColumnIndex("path"));
//                                Log.i(TAG, "path是" + path);
                                String sq = "delete from media where _id=?";
                                dbWrite.execSQL(sq, new String[]{"" + id});
                                File f = new File(Environment.getExternalStorageDirectory() + "/TwoNote/recordSound", path);
                                if (f.exists())
                                    f.delete();
                                else {
                                    Toast.makeText(getActivity(), "文件已不存在", Toast.LENGTH_SHORT).show();
                                }
                                refreshList();
                                Toast.makeText(getActivity(), "录音文件已删除", Toast.LENGTH_SHORT).show();

                            }
                        }).create().show();


                return true;
            }
        });


        refreshList();
        return v2;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    private View.OnClickListener clicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_start_stop_player:

                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        //启动
                        handler.post(updateThread);
                    } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        handler.removeCallbacks(updateThread);
                    }


                    break;

                case R.id.ib_reset_player:
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                        mediaPlayer.seekTo(0);
                        seekBar.setProgress(0);
                        init();
                    }

                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i(TAG, "在这里结束执行了释放内存的操作");
        handler.removeCallbacks(updateThread);
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
        }

    }

    String title;
    String date;
    String path;


    Handler handler = new Handler();
    Runnable updateThread = new Runnable() {
        public void run() {
            //获得歌曲现在播放位置并设置成播放进度条的值
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            //每次延迟100毫秒再启动线程
            handler.postDelayed(updateThread, 100);
        }
    };

    private void init() {
        File f = new File(Environment.getExternalStorageDirectory() + "/TwoNote/recordSound", path);
        mediaPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(f));
        seekBar.setMax(mediaPlayer.getDuration());
    }


    public static void refreshList() {
        try {
            Cursor c = dbRead.query("media", null, null, null, null, null, null);
            adapter.changeCursor(c);


        } catch (Exception e) {
//            Log.i(TAG, "发生了异常");
        }
    }

    private AdapterView.OnItemSelectedListener spinner_click = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            /**
             * 这里是spinner的监听器
             */

            //这里同样也要关联listView

            switch (position) {
                case 0:
//                    Log.i(TAG, "这是全部");
                    refreshList();
                    break;
                case 1:

//                    Cursor c=dbRead.rawQuery("")

                    String sq = "select * from media where date like?";
                    Cursor c = dbRead.rawQuery(sq, new String[]{"%" + year + "_" + month + "%"});

                    while (c.moveToNext()) {
                        String title = c.getString(c.getColumnIndex("title"));
//                        Log.i(TAG, "查询到的数据库的内容有" + title);
                    }
                    adapter.changeCursor(c);
//                    Log.i(TAG, "本月");
                    break;
                case 2:
//                    Log.i(TAG, "最近三个月");
                    if (month_int >= 3) {
                        String sq_1 = "select * from media where date like ?or date like ? or date like ?";
                        String month_1 = "" + (month_int - 1);
                        String month_2 = "" + (month_int - 2);
                        Cursor c_1 = dbRead.rawQuery(sq_1, new String[]{"%" + year + "_" + month + "%", "%" + year + "_" + month_1 + "%", "%" + year + "_" + month_2 + "%"});
                        adapter.changeCursor(c_1);

                    } else if (month_int == 2) {
                        String sq_1 = "select * from media where date like ?or date like ? or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_1 = dbRead.rawQuery(sq_1, new String[]{"%" + year + "_" + 2 + "%", "%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%"});
                        adapter.changeCursor(c_1);
                    } else if (month_int == 1) {
                        String sq_1 = "select * from media where date like ?or date like ? or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_1 = dbRead.rawQuery(sq_1, new String[]{"%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%", "%" + year_1 + "_" + 11 + "%"});
                        adapter.changeCursor(c_1);
                    }
                    break;
                case 3:
//                    Log.i(TAG, "最近五个月");
                    if (month_int >= 5) {
                        String sq_2 = "select * from media where date like ?or date like ? or date like ? or date like ?or date like ?";
                        String month_1 = "" + (month_int - 1);
                        String month_2 = "" + (month_int - 2);
                        String month_3 = "" + (month_int - 3);
                        String month_4 = "" + (month_int - 4);
                        Cursor c_2 = dbRead.rawQuery(sq_2, new String[]{"%" + year + "_" + month + "%", "%" + year + "_" + month_1 + "%", "%" + year + "_" + month_2 + "%", "%" + year + "_" + month_3 + "%", "%" + year + "_" + month_4 + "%"});
                        adapter.changeCursor(c_2);
                    } else if (month_int == 4) {
                        String sq_2 = "select * from media where date like ?or date like ? or date like ? or date like ?or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_2 = dbRead.rawQuery(sq_2, new String[]{"%" + year + "_" + 4 + "%", "%" + year + "_" + 3 + "%", "%" + year + "_" + 2 + "%", "%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%"});
                        adapter.changeCursor(c_2);
                    } else if (month_int == 3) {
                        String sq_2 = "select * from media where date like ?or date like ? or date like ? or date like ?or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_2 = dbRead.rawQuery(sq_2, new String[]{"%" + year + "_" + 3 + "%", "%" + year + "_" + 2 + "%", "%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%", "%" + year_1 + "_" + 11 + "%"});
                        adapter.changeCursor(c_2);
                    } else if (month_int == 2) {
                        String sq_2 = "select * from media where date like ?or date like ? or date like ? or date like ?or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_2 = dbRead.rawQuery(sq_2, new String[]{"%" + year + "_" + 2 + "%", "%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%", "%" + year_1 + "_" + 11 + "%", "%" + year_1 + "_" + 10 + "%"});
                        adapter.changeCursor(c_2);
                    } else if (month_int == 1) {
                        String sq_2 = "select * from media where date like ?or date like ? or date like ? or date like ?or date like ?";
                        String year_1 = "" + (year_int - 1);
                        Cursor c_2 = dbRead.rawQuery(sq_2, new String[]{"%" + year + "_" + 1 + "%", "%" + year_1 + "_" + 12 + "%", "%" + year_1 + "_" + 11 + "%", "%" + year_1 + "_" + 10 + "%", "%" + year_1 + "_" + 9 + "%"});
                        adapter.changeCursor(c_2);
                    }
                    break;
                case 4:

                    String sq_3 = "select * from media where date like ?";
                    String year_1 = "" + (year_int - 1);
                    Cursor c_3 = dbRead.rawQuery(sq_3, new String[]{"%" + year_1 + "_" + "%"});
                    adapter.changeCursor(c_3);


                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            refreshList();
        }
    };


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG, "录音列表 start");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i(TAG, "录音列表 resume");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.i(TAG, "录音列表  stop");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.i(TAG, "录音列表  pause");
    }
}
