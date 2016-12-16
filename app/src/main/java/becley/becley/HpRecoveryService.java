package becley.becley;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;


/**
 * Created by DongHoon on 2016-12-16.
 */

public class HpRecoveryService extends Service {

    LocalBroadcastManager aa = LocalBroadcastManager.getInstance(this);
    Runnable hpRecovery ;
    Thread hpRecoveryThread ;
    public static final int BASIC_ATTACK = 10;
    public static final int STRIKE_ATTACK = 30;
    public static int hp=100;
    public static boolean isStop = true;
    public static int thread_val = 0;



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        sendMessage();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("basic attack");
        intentFilter.addAction("strike attack");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                enemy_attack, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                enemy_attack);
    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    private void sendMessage() {
        hpRecovery = new Runnable() {
            @Override
            public void run() {
                while(isStop) {
                    if(hp<100) {
                        Intent hp_gen_intent = new Intent("custom-event-name");
                        // You can also include some extra data.
                        hp_gen_intent.putExtra("message", hp++);
                        aa.sendBroadcast(hp_gen_intent);

                    }
                    else
                    {
                        hp= 100;
                        Intent hp_gen_intent = new Intent("custom-event-name");
                        // You can also include some extra data.
                        hp_gen_intent.putExtra("message", hp);
                        aa.sendBroadcast(hp_gen_intent);
                    }
                    thread_val++;
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };



        if(thread_val==0) {
            hpRecoveryThread = new Thread(hpRecovery);
            if (Thread.State.NEW == hpRecoveryThread.getState()) {
                hpRecoveryThread.start();
            }
        }

    }


    private BroadcastReceiver enemy_attack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if(intent.getAction().equals("basic attack")==true)
            {

                hp = hp-BASIC_ATTACK;
                if(hp<0)
                {
                    hp=0;
                }
                Intent hp_sub_intent = new Intent("custom-event-name");
                // You can also include some extra data.
                hp_sub_intent.putExtra("message", hp);
                aa.sendBroadcast(hp_sub_intent);

            }
            else if(intent.getAction().equals("strike attack")==true)
            {
                hp = hp-STRIKE_ATTACK;
                if(hp<0)
                {
                    hp=0;
                }
                Intent hp_sub_intent = new Intent("custom-event-name");
                // You can also include some extra data.
                hp_sub_intent.putExtra("message", hp);
                aa.sendBroadcast(hp_sub_intent);

            }
        }
    };
}
