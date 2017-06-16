package com.example.zscfirefly.linefollowerrobot_controller.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zscfirefly.linefollowerrobot_controller.R;
import com.example.zscfirefly.linefollowerrobot_controller.ble.BleManager;
import com.example.zscfirefly.linefollowerrobot_controller.rocker.GameRocker;
import com.example.zscfirefly.linefollowerrobot_controller.rocker.GameRockerListener;

import java.util.Timer;
import java.util.TimerTask;

public class RockerActivity extends Activity implements GameRockerListener{

    private SeekBar sb_progress;
    private TextView tv_progress;

    private final static String TAG = "MainActivity";
    private final static String TIMER_TAG = "Timer:";
    private final static int SENSITIVITY_VALUE = 50;
    private float Computed_Scale = 0.5f;
    private int Init_Value = 50;



    private GameRocker gameRocker;

    private Timer timer = new Timer();  //记住，创建对象时记得new,否则容易报NullPointerException异常
    private int cur_X;
    private int cur_Y;
    private String test_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocker);
        initView();

        timer.schedule(timerTask, 0, 200);   //启动定时器
    }

    @Override
    public void onBackPressed(){
        GameRocker.isTouched = false;
        GameRocker.isStopped = false;
        timerTask.cancel();
        finish();
    }


    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //Log.e(TIMER_TAG, "cur_X = " + cur_X + ", cur_Y = " + cur_Y);
            //Log.e(TIMER_TAG,"Computed_Scale:" + Computed_Scale);
           // BleManager.getInstance().write("X:" + cur_X + "      Y:" + cur_Y + "\n");
            //BleManager.getInstance().write("A" + cur_X + "      Y:" + cur_Y + "\n");

            String data_str = "";

            /*100即为上限，超过100后当做100来处理*/
            if(cur_Y >= 100) {
                cur_Y = 100;
            }else if((-cur_Y) >= 100){
                cur_Y = -100;
            }

            if(cur_X >= 100) {
                cur_X = 100;
            }else if((-cur_X) >= 100){
                cur_X = -100;
            }

//            cur_Y *= Computed_Scale;
//            cur_X *= Computed_Scale;

            cur_Y = ((int) cur_Y) * 1;
            cur_X = ((int) cur_X) * 1;

            Log.d(TIMER_TAG, "X = " + cur_X + ", Y = " + cur_Y);

           // BleManager.getInstance().write("X = " + cur_X + ", Y = " + cur_Y + "\n");

            if((cur_Y >= 0) && (cur_Y <= 100)){
                if((cur_Y >= 0) && (cur_Y < 10)){ data_str = "A+00" + cur_Y; }
                else if((cur_Y >= 10) && (cur_Y < 100)){ data_str = "A+0" + cur_Y; }
                else{ data_str = "A+100";}
            }else{
                if(((-cur_Y) > 0) && ((-cur_Y) < 10)){ data_str = "A-00" + (-cur_Y); }
                else if(((-cur_Y) >= 10) && ((-cur_Y) < 100)){ data_str = "A-0" + (-cur_Y); }
                else{ data_str = "A-100"; }
            }

            if((cur_X >= 0) && (cur_X <= 100)){
                if((cur_X >= 0) && (cur_X < 10)){ data_str += "+00" + cur_X; }
                else if((cur_X >= 10) && (cur_X < 100)){ data_str += "+0" + cur_X; }
                else{ data_str += "+100";}
            }else{
                if(((-cur_X) > 0) && ((-cur_X) < 10)){ data_str += "-00" + (-cur_X); }
                else if(((-cur_X) >= 10) && ((-cur_X) < 100)){ data_str += "-0" + (-cur_X); }
                else{ data_str += "-100"; }
            }


//            if(cur_Y >= 0 && cur_Y <= 100){
//               // data_str = "A+0" + cur_Y;
//                if(cur_Y >= 0&& cur_Y < 10){     data_str = "A+00" + (cur_Y/Computed_Scale);    }
//                else{
//                    if(cur_Y == 100){
//                        //data_str = "A+" + (cur_Y/Computed_Scale);
//                        if(Computed_Scale == 1){
//                            data_str = "A+" + (cur_Y/Computed_Scale);
//                        }else{
//                            data_str = "A+0" + (cur_Y/Computed_Scale);
//                        }
//                    }
//                    else{
//                        //要根据Computed_Scale来进行判断
//                        if((cur_Y/Computed_Scale) < 10){
//                            data_str = "A+00" + (cur_Y/Computed_Scale);
//                        }else{
//                            data_str = "A+0" + (cur_Y/Computed_Scale);
//                        }
//                    }
//                       // data_str = "A+0"+ (cur_Y/Computed_Scale);
//                }
//            }else{
//                if((-cur_Y) >= 0 && (-cur_Y) < 10){     data_str = "A-00" + ((-cur_Y)/Computed_Scale);    }
//                else{
//                    if((-cur_Y) == 100) {
//                        //data_str = "A" + ((cur_Y)/Computed_Scale);
//                        if(Computed_Scale == 1){
//                            data_str = "A" + ((cur_Y)/Computed_Scale);
//                        }else{
//                            data_str = "A-0" + ((-cur_Y)/Computed_Scale);
//                        }
//                    }
//                    else{
//                        //data_str = "A-0"+ ((-cur_Y)/Computed_Scale);
//
//                        if(((-cur_Y)/Computed_Scale) < 10){
//                            data_str = "A-00"+ ((-cur_Y)/Computed_Scale);
//                        }else{
//                            data_str = "A-0"+ ((-cur_Y)/Computed_Scale);
//                        }
//                    }
//                }
//            }

           // data_str = "A+000";

//            if(cur_X >= 0 && cur_X <= 100){
//                // data_str = "A+0" + cur_Y;
//                if(cur_X >= 0&& cur_X < 10){     data_str += "+00" + (cur_X/Computed_Scale);    }
//                else{
//                    if(cur_X == 100){
//                        //data_str = "A+" + (cur_Y/Computed_Scale);
//                        if(Computed_Scale == 1){
//                            data_str += "+" + (cur_X/Computed_Scale);
//                        }else{
//                            data_str += "+0" + (cur_X/Computed_Scale);
//                        }
//                    }
//                    else{
//                        //要根据Computed_Scale来进行判断
//                        if((cur_X/Computed_Scale) < 10){
//                            data_str += "+00" + (cur_X/Computed_Scale);
//                        }else{
//                            data_str += "+0" + (cur_X/Computed_Scale);
//                        }
//                    }
//                    // data_str = "A+0"+ (cur_Y/Computed_Scale);
//                }
//            }else{
//                if((-cur_X) >= 0 && (-cur_X) < 10){     data_str += "-00" + ((-cur_X)/Computed_Scale);    }
//                else{
//                    if((-cur_X) == 100) {
//                        //data_str = "A" + ((cur_Y)/Computed_Scale);
//                        if(Computed_Scale == 1){
//                            data_str += "" + ((cur_X)/Computed_Scale);
//                        }else{
//                            data_str += "-0" + ((-cur_X)/Computed_Scale);
//                        }
//                    }
//                    else{
//                        //data_str = "A-0"+ ((-cur_Y)/Computed_Scale);
//
//                        if(((-cur_X)/Computed_Scale) < 10){
//                            data_str += "-00"+ ((-cur_X)/Computed_Scale);
//                        }else{
//                            data_str += "-0"+ ((-cur_X)/Computed_Scale);
//                        }
//                    }
//                }
//            }

//            if(cur_X >= 0 && cur_X <= 100){
//                // data_str = "A+0" + cur_Y;
//                if(cur_X >= 0&& cur_X < 10){     data_str += "+00" + (cur_X/Computed_Scale);    }
//                else{
//                    if(cur_X == 100) data_str = "+" + (cur_X/Computed_Scale);
//                    else data_str += "+0"+ (cur_X/Computed_Scale);
//                }
//            }else{
//                if((-cur_X) >= 0 && (-cur_X) < 10){     data_str += "-00" + ((-cur_X)/Computed_Scale);    }
//                else{
//                    if((-cur_X) == 100) data_str +=  ((cur_X)/Computed_Scale);
//                    else data_str += "-0"+ ((-cur_X)/Computed_Scale);
//                }
//            }

           // data_str += "-000";

            data_str += "\n";

            if(GameRocker.isTouched == true){
               BleManager.getInstance().write(data_str);
            }
            else if(GameRocker.isStopped == true){
                BleManager.getInstance().write("A+000+000\n");
                GameRocker.isStopped = false;
            }

            //Log.d(TIMER_TAG, "X = " + cur_X + ", Y = " + cur_Y);
            //Log.e(TIMER_TAG,"Computed_Scale:" + Computed_Scale);
            Log.d(TIMER_TAG,"data_str:" + data_str);


            //test_str = "X = " + cur_X + ", Y = " + cur_Y + "\n";
            //test_str = " Y = " + cur_Y + "\n";

            //BleManager.getInstance().write("Computed_Scale:" + Computed_Scale + "\n");
            //test_str = " X = " + cur_X + "\n";
            //BleManager.getInstance().write(test_str);
        }
    };

    private void initView(){
        gameRocker = (GameRocker) findViewById(R.id.game_rocker);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);

        sb_progress.setProgress(Init_Value);
        tv_progress.setText("灵敏度：" + Init_Value);
        sb_progress.setOnSeekBarChangeListener(myseekbarListener);
        setOnListener();
    }

    private void setOnListener(){
        gameRocker.setOnGameRockerListener(this);
    }


    SeekBar.OnSeekBarChangeListener myseekbarListener = new MySeekBarListener();

    public class MySeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Init_Value = i;
            tv_progress.setText("灵敏度：" + Init_Value);

//            if (i > 81) {
//                Computed_Scale = 1;
//            } else {
//                Computed_Scale = (100 - i) / SENSITIVITY_RATIO;
//            }

            Computed_Scale = ((float) i) / (100.0f);

            Log.d(TIMER_TAG, "Computed_Scale:" + Computed_Scale);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    public void onDirection(float x, float y) {
        //Log.e(TAG, "x = " + x + ", y = " + y);

        //Demo的坐标系的是左为Y轴的正半轴，下为X轴的正半轴,对获取的X,Y值修改一下，改成上为Y轴正半轴，右为X轴正半轴
//        cur_X = (int) x;
//        cur_Y = (int) y;

//        cur_X = (int)(-y);
//        cur_Y = (int)(-x);

        cur_X = (int) x;
        cur_Y = (int) (-y);

        cur_Y *= Computed_Scale;
        cur_X *= Computed_Scale;

       // BleManager.getInstance().write("X:" + cur_X + "      Y:" + cur_Y + "\n");

    }
}
