package jay.com.loginphp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternet extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null){
            NetworkInfo ni=(NetworkInfo)intent.getExtras().get(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED){
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
