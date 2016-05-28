package com.example.gongzhiyao.twonote03.Recordlist_fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gongzhiyao.twonote03.R;
import com.example.gongzhiyao.twonote03.database.MyDB;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Record_sound_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Record_sound_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Record_sound_fragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CircleBar circleBar;
    Chronometer chronometer;
    private static final String TAG = "Main";
    private int startOrpause = 1;
    private Button mb_Record_help, mb_Record_drop;
    public static MediaRecorder mediaRecorder;
    public static List<String> soundList = new ArrayList<String>();
    public static int length_list;
    private SQLiteDatabase dbRead, dbWrite;

    private OnFragmentInteractionListener mListener;
    private EditText title;
    private String Record_title, Record_path;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Record_sound_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Record_sound_fragment newInstance(String param1, String param2) {
        Record_sound_fragment fragment = new Record_sound_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Record_sound_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 = inflater.inflate(R.layout.fragment_record_sound_fragment, container, false);

//        findview();
        mb_Record_help = (Button) v1.findViewById(R.id.btn_Record_help);
        mb_Record_drop = (Button) v1.findViewById(R.id.btn_Record_drop);
        title = (EditText) v1.findViewById(R.id.et_Record_title);
//        title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mb_Record_help.requestFocus();

//        bindListener();
        final MyDB database = new MyDB(getActivity());
        dbRead = database.getReadableDatabase();
        dbWrite = database.getWritableDatabase();

        mb_Record_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                /**
//                 * 在这里写帮助文档
//                 * 由于还没有写帮助文档，所以现在这里测试一下数据库是否写入了
//                 */
//                Cursor c = dbRead.query("media", null, null, null, null, null, null);
//                while (c.moveToNext()) {
//                    String s1 = c.getString(c.getColumnIndex("title"));
//                    String s2 = c.getString(c.getColumnIndex("path"));
//                    Log.i(TAG, "标题是" + s1 + "         路径是" + s2);
//                }


            }
        });

        mb_Record_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//这诗取消录音的操作
                if (mediaRecorder != null) {
                    title.setText("");
                    startOrpause=1;
                    stopRecord();//停止录音
                    String needToRemove = soundList.get(length_list - 1);
//                    Log.i(TAG, "此时应该把这个删除" + needToRemove);
                    soundList.remove(length_list - 1);
                    chronometer.stop();
                    chronometer.setText("00:00");
                    File need_remove = new File(Environment.getExternalStorageDirectory() + "/TwoNote/recordSound", needToRemove);
                    need_remove.delete();
//                    Log.i(TAG, "录音已取消，文件已删除");
                    Toast.makeText(getActivity(), "录音已取消", Toast.LENGTH_SHORT).show();

                }
            }
        });
        circleBar = (CircleBar) v1.findViewById(R.id.circle);
        circleBar.setSweepAngle(360);//设定点击后一圈360度


        chronometer = (Chronometer) v1.findViewById(R.id.chronometer);
        circleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vieaw) {

                switch (startOrpause) {
                    case 1:
                        /////////////这是开始录音
                        Toast.makeText(getActivity(), "录音系统已启动", Toast.LENGTH_SHORT).show();
                        chronometer.setBase(SystemClock.elapsedRealtime());

                        chronometer.start();
                        circleBar.startCustomAnimation();
                        startOrpause = 2;
                        try {
                            startRecord();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:              //这是保存录音
                        Toast.makeText(getActivity(), "录音已保存，可到录音列表查看", Toast.LENGTH_SHORT).show();
                        chronometer.stop();
                        startOrpause = 1;
                        stopRecord();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        chronometer.setText("00:00");
                        //这个语句的成立，必须有以下条件：1.在退出录音界面再返回时，要确保soundList没有再次初始化
                        //
//                        Log.i(TAG, "需要保存的是" + soundList.get(length_list - 1));
                        /**
                         * 可以在这里把录音文件的路径添加到数据库中，还有标题
                         */
                        Record_title = title.getText().toString();
                        if (Record_title.equals("")) {
                            Record_title = "无标题";
                        }
                        Record_path = soundList.get(length_list - 1);
                        ContentValues cv = new ContentValues();
                        cv.put("title", Record_title);
                        cv.put("path", Record_path);
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
                        String time = f.format(new Date());
                        cv.put("date", time);
                        dbWrite.insert("media", null, cv);
                        Log.i(TAG, "数据已经成功的写入");
                        title.setText("");//重置为0


                        break;

                }


            }
        });
        circleBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getActivity(), "录音系统已重置", Toast.LENGTH_SHORT).show();
                if (mediaRecorder != null) {//证明此时在录音状态
                    stopRecord();
                    String need_remove = soundList.get(length_list - 1);
//                    Log.i(TAG, "需要删除的是" + need_remove);
                    soundList.remove(length_list - 1);
//                        chronometer.setText("00:00");
                    chronometer.setBase(SystemClock.elapsedRealtime());

                    chronometer.start();
                    try {
//                        Log.i(TAG, "重新开始");
                        startRecord();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //因为上面又有了一个start，所以这时候删除就应该注意位置了
                    //因为前面已经删除了，所以在这里就不用担心了

                    /**
                     *  此时最好能够在后台删除刚才录制的音频
                     */
//                    Log.i(TAG, "打算在这里删除文件");

                    /**
                     * 在这里将need_removed这个名字的文件删除即可
                     */
                    File needToDelete = new File(Environment.getExternalStorageDirectory() + "/TwoNote/recordSound", need_remove);
                    needToDelete.delete();
//                    Log.i(TAG, "文件已经删除");


                }

                //在里面实现重新录音，重置
                return true;
            }
        });


        return v1;
    }


    private void findview() {

    }

    private void bindListener() {

        mb_Record_help.setOnClickListener((View.OnClickListener) this);
        mb_Record_drop.setOnClickListener((View.OnClickListener) this);
    }



    private void startRecord() throws IOException {


        if (mediaRecorder == null) {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "TwoNote/recordSound");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = System.currentTimeMillis() + ".amr";
            Log.i(TAG, "filename" + filename);


            ///测试

            File soundFile = new File(dir, filename);
            if (!soundFile.exists()) {
                soundFile.createNewFile();
            }
            soundList.add(filename);
            length_list = soundList.size();
            Log.i(TAG, "此时的length是" + length_list);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mediaRecorder.setOutputFile(soundFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();


        }
    }

    private void stopRecord() {

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG,"录音界面 start");
    }

    @Override
    public void onResume() {

//        Log.i(TAG,"录音界面 resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.i(TAG,"录音界面 pause");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.i(TAG,"录音界面 stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.i(TAG,"录音界面 destroy");
    }

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

}
