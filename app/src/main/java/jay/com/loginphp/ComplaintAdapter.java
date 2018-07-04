package jay.com.loginphp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static android.support.v4.content.ContextCompat.createDeviceProtectedStorageContext;
import static android.support.v4.content.ContextCompat.startActivities;
import static android.support.v4.content.ContextCompat.startActivity;
import static java.security.AccessController.getContext;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>{

    private Context mCtx;
    private List<Complaint> complaintList=new ArrayList<Complaint>();
    private Bitmap bmp;



    public ComplaintAdapter(Context mCtx, List<Complaint> complaintList) {
        this.mCtx = mCtx;
        this.complaintList = complaintList;
    }

    @Override
    public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mCtx);
        View view=inflater.inflate(R.layout.complaint_list,null);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ComplaintViewHolder holder, final int position) {
        final Complaint complaint=complaintList.get(position);


        holder.email.setText("Complaint Given By:- "+complaint.getEmail());
        holder.created_at.setText("Date of Complaint:- "+complaint.getCreated_at());
        holder.id.setText("Complaint Number:- "+complaint.getId());
        holder.problem.setText("Problem:- "+complaint.getProblem());
        if (complaint.getStatus() == String.valueOf(0))
        {
            holder.status.setText("Status:- Pending");
        }
        if (complaint.getStatus() == String.valueOf(1))
        {
            holder.status.setText("Status:- Accepted");
        }
        if (complaint.getStatus()==String.valueOf(2))
        {
            holder.status.setText("Status:- Rejected");
        }
        if (complaint.getStatus()==String.valueOf(3))
        {
            holder.status.setText("Status:- Solved");
        }
        //holder.status.setText("Status:- "+complaint.getStatus());
        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mCtx,Single_List_of_Complaint.class);
                intent.putExtra("id",complaint.getId());
                intent.putExtra("img",complaint.getImage());
                intent.putExtra("email",complaint.getEmail());
                intent.putExtra("location",complaint.getLocation());
                intent.putExtra("created_at",complaint.getCreated_at());
                intent.putExtra("problem",complaint.getProblem());
                intent.putExtra("status",complaint.getStatus());
                mCtx.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(mCtx);
                builder.setMessage("Are you Sure to want to delete this?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_DELETECOMPLAINT, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(mCtx, "DELETED Complaint No:- "+complaint.getId(), Toast.LENGTH_SHORT).show();
                                android.support.v4.app.FragmentTransaction ft=((AppCompatActivity) mCtx).getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.container_main,new Past_Complaint());
                                ft.commit();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(mCtx,
                                        "Check your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();
                                //params.put("id", complaint.getId().toString());
                                params.put("id", complaint.getId());
                                return params;
                            }
                        };
                        AppController.getInstance().addToRequestQueue(strReq);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return complaintList.size();
    }



    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView email,image,created_at,id,problem,status;
        ImageView imageView;
        Button delete;
        RelativeLayout relative;

        public ComplaintViewHolder(View itemView) {
            super(itemView);
            email=itemView.findViewById(R.id.tvemail);
            created_at=itemView.findViewById(R.id.tvcreated_at);
            id=itemView.findViewById(R.id.tvid);
            delete=itemView.findViewById(R.id.button1);
            problem=itemView.findViewById(R.id.tvproblem);
            relative=itemView.findViewById(R.id.relative);
            status=itemView.findViewById(R.id.tvstatus);
        }
    }
}
