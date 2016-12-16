package becley.becley;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;


public class MainActivity extends AppCompatActivity {

    private TextView User_HP;
    LocalBroadcastManager aa;

    private IconRoundCornerProgressBar progressOne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

}


