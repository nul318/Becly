package becley.becley;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;


public class MainActivity extends AppCompatActivity implements SensorEventListener {





    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    int cnt=0;

    Boolean attack_time = true;
    Boolean defense_check;
    Boolean defense_time;
    Vibrator vibe;
    Handler handler = new Handler();

    TextView attack_check;
    MediaPlayer mp;










    private TextView User_HP;
    LocalBroadcastManager aa;

    private IconRoundCornerProgressBar progressOne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        attack_time = true;

        Button bt = (Button) findViewById(R.id.defense);
        defense_check = true;
        mp = MediaPlayer.create(this, R.raw.cool_time);

        attack_check = (TextView) findViewById(R.id.attack_check);



//        RoundCornerProgressBar progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
//        progress1.setProgressColor(Color.parseColor("#ed3b27"));
//        progress1.setProgressBackgroundColor(Color.parseColor("#808080"));
//        progress1.setMax(70);
//        progress1.setProgress(15);

        progressOne = (IconRoundCornerProgressBar) findViewById(R.id.progress_one);
        progressOne.setMax(100);
















        User_HP = (TextView) findViewById(R.id.User_HP);
        aa = LocalBroadcastManager.getInstance(MainActivity.this);

        if(!isServiceRunningCheck()) {
            Intent intent = new Intent("becley.becley.shakeservice");
            intent.setPackage("becley.becley");
            startService(intent);
        }
        Button buttonStartService = (Button)findViewById(R.id.btnPlay);
        buttonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Register MessageService in Manifest to work
                Intent hp_gen_intent = new Intent("strike sttack");
                // You can also include some extra data.
                hp_gen_intent.setAction("strike sttack");
                aa.sendBroadcast(hp_gen_intent);
                progressOne.setProgress(70);
                Toast.makeText(getApplicationContext(),"ss"+progressOne.getMax(),Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"ss"+progressOne.getProgress(),Toast.LENGTH_LONG).show();
            }
        });
        HpRecoveryService.isStop = true;
        startService(new Intent(MainActivity.this, HpRecoveryService.class));
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("becley.becley.shakeservice".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }
    protected  void onDestroy(){
        super.onPause();

    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            int message = intent.getIntExtra("message",100);
            User_HP.setText(""+message);
        }
    };





    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.i("adasd", attack_time + "");

            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {

                lastTime = currentTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

//                if(check) {
//                Log.i("x : ", Math.abs(x - lastX) / gabOfTime * 10000 + ", y : " + Math.abs(y - lastY) / gabOfTime * 10000 + ", z : " + Math.abs(z - lastZ) / gabOfTime * 10000);
//                }
                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
                    cnt++;
                    if(cnt>5){
                        cnt=0;

                        if (attack_time) {

                            vibe.vibrate(500);

                            new Thread() {
                                @Override
                                public void run() {
                                    attack_time = false;


                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
//                                        attack_check.setText("기본공격 쿨타임 입니다");
                                        }
                                    });


                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }


                                    attack_time = true;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
//                                        attack_check.setText("기본공격 가능");
                                        }
                                    });
                                }
                            }.start();

                        } else {

                            if(!mp.isPlaying()) {

                                mp.setLooping(false);
                                mp.start();
                            }
                        }
                    }
                }


                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];


            }
        }

    }








}


