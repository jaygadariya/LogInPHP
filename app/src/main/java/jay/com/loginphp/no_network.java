package jay.com.loginphp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class no_network extends AppCompatActivity {
    private NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);
    }
}
