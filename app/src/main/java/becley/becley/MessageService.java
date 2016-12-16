package becley.becley;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;

public class MessageService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    MessageListener mMessageListener;
    Message mActiveMessage;
    Handler handler;
    BroadcastReceiver broadcastReceiver;
    LocalBroadcastManager broadCaster;

    public static final String TAG = "near";

    public static final String BASIC_ATTACK = "basic attack";
    public static final String STRIKE_ATTACK = "strike attack";

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
//                super.onFound(message);
                String messageText = new String(message.getContent());
                Log.i(TAG, messageText);
                Intent intent = new Intent(messageText);
                broadCaster.sendBroadcast(intent);
            }
        };
        handler = new Handler();
        broadCaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        registerReceiver();
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");
    }

    @Override
    public void onDestroy() {
        if(mGoogleApiClient.isConnected()){
            unPublish();
            unSubscribe();
            mGoogleApiClient.disconnect();
        }
        unRegisterReceiver();
        super.onDestroy();
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes(), "Type");
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

    private void registerReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "asdsds");
                switch (intent.getAction()){

                    case BASIC_ATTACK : {
                        publish(BASIC_ATTACK);
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unPublish();
                            }
                        }, 2000);
                        break;
                    }
                    case STRIKE_ATTACK : {
                        publish(STRIKE_ATTACK);
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unPublish();
                            }
                        }, 2000);
                        break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BASIC_ATTACK);
        intentFilter.addAction(STRIKE_ATTACK);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unRegisterReceiver(){
        if(broadcastReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
    }
}
