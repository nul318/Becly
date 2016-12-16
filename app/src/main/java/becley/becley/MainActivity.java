package becley.becley;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isServiceRunningCheck()) {
            Intent intent = new Intent("becley.becley.shakeservice");
//            intent.setPackage("becley.becley");
            startService(intent);
        }
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

}
