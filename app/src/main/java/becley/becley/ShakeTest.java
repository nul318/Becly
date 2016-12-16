package becley.becley;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShakeTest extends Activity implements SensorEventListener {

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


    TextView cnt;

    Boolean attack_time = true;
    Boolean defense_check;
    Boolean defense_time;
    Vibrator vibe;
    Handler handler = new Handler();

    TextView attack_check;
    MediaPlayer mp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        attack_time = true;

        Button bt = (Button) findViewById(R.id.defense);
        defense_check = true;
        mp = MediaPlayer.create(this, R.raw.cool_time);

        attack_check = (TextView) findViewById(R.id.attack_check);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if(defense_check) defense_check = false;
//                else defense_check = true;
            }
        });
    }

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
                    if (attack_time) {

                        vibe.vibrate(500);

                        new Thread() {
                            @Override
                            public void run() {
                                attack_time = false;


                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        attack_check.setText("기본공격 쿨타임 입니다");
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
                                        attack_check.setText("기본공격 가능");
                                    }
                                });
                            }
                        }.start();

                    } else {
                        vibe.vibrate(50);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        vibe.vibrate(50);
                        if(!mp.isPlaying()) {

                            mp.setLooping(false);
                            mp.start();
                        }
                    }
                }


                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];


            }
        }

    }

    float Radian2Degree(float radian) {
        return radian * 180 / (float) Math.PI;
    }

}
