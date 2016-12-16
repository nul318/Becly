package becley.becley;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;

import at.grabner.circleprogress.CircleProgressView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    CircleProgressView circleView;
    CircleProgressView defendView;
    CircleProgressView strikeView;


    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 200;
    private static final int STRIKE_THRESHOLD = 10000;

    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    int cnt=0;

    Boolean attack_time = true;
    Boolean defense_time = true;

    Vibrator vibe;
    Handler handler = new Handler();

    TextView attack_check;
    MediaPlayer mp;
    MediaPlayer mp2;
    MediaPlayer mp3;
    MediaPlayer mp4;

    int defense_check=-1;


    private GoogleApiClient mGoogleApiClient;
    private Message mActiveMessage;
    private MessageListener mMessageListener;




    private TextView User_HP;
    LocalBroadcastManager aa;

    private static final String TAG = "near";

    private IconRoundCornerProgressBar progressOne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        circleView = (CircleProgressView) findViewById(R.id.circleView);
        defendView = (CircleProgressView) findViewById(R.id.defendView);
        strikeView = (CircleProgressView) findViewById(R.id.strikeView);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                if(defense_check == 2){
                    Log.i("near", "방어막");
                    return;
                }
                if(message.getType().equals("Basic")){
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("basic attack"));
                }
                else if(message.getType().equals("Strike")){
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent("strike attack"));
                }
                String messegaTest = new String(message.getContent());
                Log.i("near", messegaTest);

//                if(messegaTest.equals("basic attack")){

//                }
//                else if(messegaTest.equals("strike attack")){
//
//                }
            }
        };

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        attack_time = true;

        Button bt = (Button) findViewById(R.id.defense);

        mp = MediaPlayer.create(this, R.raw.cool_time);
        mp2 = MediaPlayer.create(this, R.raw.sword);
        mp3 = MediaPlayer.create(this, R.raw.defense);
        mp4 = MediaPlayer.create(this, R.raw.strike);

        attack_check = (TextView) findViewById(R.id.attack_check);



//        RoundCornerProgressBar progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
//        progress1.setProgressColor(Color.parseColor("#ed3b27"));
//        progress1.setProgressBackgroundColor(Color.parseColor("#808080"));
//        progress1.setMax(70);
//        progress1.setProgress(15);

        progressOne = (IconRoundCornerProgressBar) findViewById(R.id.progress_one);
        progressOne.setMax(100f);




        new Thread () {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cnt=0;
                }

            }
        }.start();






        User_HP = (TextView) findViewById(R.id.User_HP);
        aa = LocalBroadcastManager.getInstance(MainActivity.this);


//        Button buttonStartService = (Button)findViewById(R.id.btnPlay);
//        buttonStartService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
////                Register MessageService in Manifest to work
//                Intent hp_gen_intent = new Intent("strike attack");
//                // You can also include some extra data.
//                hp_gen_intent.setAction("strike attack");
//                aa.sendBroadcast(hp_gen_intent);
////Log.i("tas","dasda");
//
//            }
//        });
        HpRecoveryService.isStop = true;
        startService(new Intent(MainActivity.this, HpRecoveryService.class));
        circleView.bringToFront();
        defendView.bringToFront();

    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }
    protected  void onDestroy(){
        unPublish();
        unSubscribe();
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
            progressOne.setProgress(message);
            User_HP.setText("HP : "+message);

        }
    };





    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        mGoogleApiClient.connect();
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

                speed = Math.abs(x - lastX) / gabOfTime * 10000;
//                Log.i("cnt ", cnt+"");
//                if(check) {
//                Log.i("x : ", Math.abs(x - lastX) / gabOfTime * 10000 + ", y : " + Math.abs(y - lastY) / gabOfTime * 10000 + ", z : " + Math.abs(z - lastZ) / gabOfTime * 10000);
//                }
//                Log.i("speed : ", speed+"");
                if(speed > STRIKE_THRESHOLD){
                    publish(Long.toString(System.currentTimeMillis()), "Strike");
                    vibe.vibrate(2000);
                    if (!mp4.isPlaying()) {
                        mp4.setLooping(false);
                        mp4.start();
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unPublish();
                        }
                    }, 2000);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
//                    vibe.vibrate(80);
                    cnt++;
                    if(cnt>5) {
                        cnt = 0;
//                        Log.i("defense" , defense_check+"");
                        if (defense_check == 2 && defense_time) {
                            if (!mp3.isPlaying()) {
                                mp3.setLooping(false);
                                mp3.start();
                            }


                            new Thread() {
                                @Override
                                public void run() {
                                    defense_time = false;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            defendView.setVisibility(View.VISIBLE);

                                            defendView.setValueAnimated(100, 3000);
//                                        attack_check.setText("기본공격 쿨타임 입니다");
                                        }
                                    });

                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    defense_time = true;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            defendView.setVisibility(View.INVISIBLE);
                                            defendView.setValueAnimated(0, 0);
//                                        attack_check.setText("기본공격 가능");
                                        }
                                    });
                                }
                            }.start();

                        }else{
                            if (attack_time) {
                                vibe.vibrate(800);
                                publish(Long.toString(System.currentTimeMillis()), "Basic");
                                Log.i("near", "qweqwr");
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        unPublish();
                                    }
                                }, 2000);
                                if (!mp2.isPlaying()) {
                                    mp2.setLooping(false);
                                    mp2.start();
                                }


                                new Thread() {
                                    @Override
                                    public void run() {
                                        attack_time = false;


                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                circleView.setVisibility(View.VISIBLE);

                                                circleView.setValueAnimated(100, 2000);

//                                        attack_check.setText("기본공격 쿨타임 입니다");
                                            }
                                        });


                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }


                                        attack_time = true;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                circleView.setVisibility(View.INVISIBLE);
                                                circleView.setValueAnimated(0, 0);
//                                        attack_check.setText("기본공격 가능");
                                            }
                                        });
                                    }
                                }.start();

                            } else {
                                vibe.vibrate(100);
//                            if(!mp.isPlaying()) {
//                                mp.setLooping(false);
//                                mp.start();
//                            }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        defense_check=event.getAction();
        return super.onTouchEvent(event);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void publish(String message, String type) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes(), type);
        PublishOptions.Builder builder = new PublishOptions.Builder();
        builder.setCallback(new PublishCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.i(TAG, "Expired");
            }
        }).setStrategy(Strategy.DEFAULT);
        Nearby.Messages.publish(mGoogleApiClient, mActiveMessage, builder.build());
    }

    private void unPublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, mActiveMessage);
            mActiveMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener);
    }

    private void unSubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }


}



