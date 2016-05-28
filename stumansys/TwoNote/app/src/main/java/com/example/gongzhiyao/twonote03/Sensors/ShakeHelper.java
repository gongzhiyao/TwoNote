package com.example.gongzhiyao.twonote03.Sensors;

/**
 * Created by 宫智耀 on 2016/5/25.
 */
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ShakeHelper implements SensorEventListener
{
    private Context mContext;
    //传感器管理器
    private SensorManager mSensorManager;
    //传感器
    private Sensor mSensor;
    //速度阀值
    private int mSpeed=3000;
    //时间间隔
    private int mInterval=50;
    //上一次摇晃的时间
    private long LastTime;
    //上一次的x、y、z坐标
    private float LastX,LastY,LastZ;
    public ShakeHelper(Context mContext)
    {
        this.mContext=mContext;
        Start();
    }

    public void Start()
    {
        mSensorManager=(SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!=null)
        {
            mSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if(mSensor!=null)
        {
            mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void Stop()
    {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent Event)
    {
        long NowTime=System.currentTimeMillis();
        if((NowTime-LastTime)<mInterval)
            return;
        //将NowTime赋给LastTime
        LastTime=NowTime;
        //获取x,y,z
        float NowX=Event.values[0];
        float NowY=Event.values[1];
        float NowZ=Event.values[2];
        //计算x,y,z变化量
        float DeltaX=NowX-LastX;
        float DeltaY=NowY-LastY;
        float DeltaZ=NowZ-LastZ;
        //赋值
        LastX=NowX;
        LastY=NowY;
        LastZ=NowZ;
        //计算
        double NowSpeed = Math.sqrt(DeltaX * DeltaX + DeltaY * DeltaY + DeltaZ * DeltaZ)/mInterval * 10000;
        //判断
        if(NowSpeed>=mSpeed)
        {
//            Toast.makeText(mContext, "你摇晃了手机！", Toast.LENGTH_SHORT).show();
            i=i+1;
            System.out.println((int)(i/2.545));
        }
    }

    int i=0;


    public int getCount(){
        return i;
    }
}