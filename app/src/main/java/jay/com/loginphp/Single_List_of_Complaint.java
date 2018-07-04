package jay.com.loginphp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Single_List_of_Complaint extends AppCompatActivity {

    TextView tv_single_id,tv_single_email,tv_single_problem,tv_single_location,tv_single_created_at,tv_single_status;
    ImageView img_single_img;
    private Bitmap bmp;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__list_of__complaint);

        final Intent intent=getIntent();

        tv_single_id=(TextView)findViewById(R.id.tv_single_id);
        tv_single_email=(TextView)findViewById(R.id.tv_single_email);
        tv_single_problem=(TextView)findViewById(R.id.tv_single_problem);
        tv_single_location=(TextView)findViewById(R.id.tv_single_location);
        tv_single_created_at=(TextView)findViewById(R.id.tv_single_created_at);
        tv_single_status=(TextView) findViewById(R.id.tv_single_status);
        img_single_img=(ImageView)findViewById(R.id.img_single_img);

        tv_single_id.setText("Complaint Number:- "+intent.getStringExtra("id")+"\n");
        tv_single_email.setText("Complaint Given By:- \n"+intent.getStringExtra("email")+"\n");
        tv_single_problem.setText("Problem:- \n"+intent.getStringExtra("problem")+"\n");
        tv_single_location.setText("Problem Located At:- \n"+intent.getStringExtra("location")+"\n");
        tv_single_created_at.setText("Date of Complaint:- "+intent.getStringExtra("created_at")+"\n");

        if (intent.getStringExtra("status").equals("0"))
        {
            tv_single_status.setText("Status:- Pending");
        }
        if (intent.getStringExtra("status").equals("1"))
        {
            tv_single_status.setText("Status:- Accepted");
        }
        if (intent.getStringExtra("status").equals("2"))
        {
            tv_single_status.setText("Status:- Rejected");
        }
        //tv_single_status.setText("Status:- "+intent.getStringExtra("status"));

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InputStream in=new URL("https://jaywebsite.000webhostapp.com/android_login_api/uploads/"+intent.getStringExtra("img") ).openStream();
                    bmp= BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(bmp!=null){
                    img_single_img.setImageBitmap(bmp);
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }
}
