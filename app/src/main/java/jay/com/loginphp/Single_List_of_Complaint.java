package jay.com.loginphp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Single_List_of_Complaint extends AppCompatActivity {

    TextView tv_single_id,tv_single_email,tv_single_problem,tv_single_location,tv_single_created_at,tv_single_status,tv_single_discription;
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
        tv_single_discription=(TextView) findViewById(R.id.tv_single_discription);
        img_single_img=(ImageView)findViewById(R.id.img_single_img);

        tv_single_id.setText("Complaint Number:- "+intent.getStringExtra("id")+"\n");
        tv_single_email.setText("Complaint Given By:- \n"+intent.getStringExtra("email")+"\n");
        tv_single_problem.setText("Problem:- \n"+intent.getStringExtra("problem")+"\n");
        tv_single_location.setText("Problem Located At:- \n"+intent.getStringExtra("location")+"\n");
        tv_single_created_at.setText("Date of Complaint:- "+intent.getStringExtra("created_at")+"\n");
        tv_single_discription.setText("Discription About Complaint:- "+intent.getStringExtra("discription")+"\n");

//        if (intent.getStringExtra("status").equals("0"))
//        {
//            tv_single_status.setText("Status:- Pending");
//            btn_single_accept.setVisibility(View.VISIBLE);
//            btn_single_reject.setVisibility(View.VISIBLE);
//        }
//        if (intent.getStringExtra("status").equals("1"))
//        {
//            tv_single_status.setText("Status:- Accepted");
//            btn_single_accept.setVisibility(View.INVISIBLE);
//            btn_single_reject.setVisibility(View.VISIBLE);
//        }
//        if (intent.getStringExtra("status").equals("2"))
//        {
//            btn_single_accept.setVisibility(View.VISIBLE);
//            btn_single_reject.setVisibility(View.INVISIBLE);
//            tv_single_status.setText("Status:- Rejected");
//        }
//        if (intent.getStringExtra("status").equals("3"))
//        {
//            btn_single_accept.setVisibility(View.INVISIBLE);
//            btn_single_reject.setVisibility(View.INVISIBLE);
//            tv_single_status.setText("Status:- Solved");
//        }
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

//        btn_single_accept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id=intent.getStringExtra("id");
//                String status="1";
//                String progress_status="Accept";
//                status_of_complaint(id,status,progress_status);
//            }
//        });
//        btn_single_reject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id=intent.getStringExtra("id");
//                String status="2";
//                String progress_status="Reject";
//                status_of_complaint(id,status,progress_status);
//            }
//        });
//        btn_single_solve.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String id=intent.getStringExtra("id");
//                String status="3";
//                String progress_status="Solve";
//                status_of_complaint(id,status,progress_status);
//            }
//        });
//
//    }
//    private void status_of_complaint(final String id, final String status, final String progress_status)
//    {
//        final ProgressDialog progressDialog=new ProgressDialog(Single_List_of_Complaint.this);
//        progressDialog.setMessage("Wait While "+progress_status+"ing ...");
//        if (!progressDialog.isShowing()) {
//            progressDialog.show();
//        }
//        StringRequest stringRequest=new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_COMPLAINT_STATUS, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);
//                        boolean error = jsonObject.getBoolean("error");
//                        if (!error) {
//                            Toast.makeText(Single_List_of_Complaint.this, "Your Application is "+progress_status+"!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            String errorMsg = jsonObject.getString("error_msg");
//                            Toast.makeText(Single_List_of_Complaint.this, "" + errorMsg, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                startActivity(new Intent(Single_List_of_Complaint.this,Past_Complaint.class));
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Single_List_of_Complaint.this, "Problem while "+progress_status+"your application!", Toast.LENGTH_SHORT).show();
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("id",id);
//                params.put("status",status);
//                return params;
//            }
//        };
//        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
