package jay.com.loginphp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Complaint_filter_Adapter extends RecyclerView.Adapter<Complaint_filter_Adapter.ComplaintViewHolder>{

    private Context mCtx;
    private List<Complaint> complaintList=new ArrayList<Complaint>();
    private Bitmap bmp;
    AppCompatActivity activity;



    public Complaint_filter_Adapter(Context mCtx, List<Complaint> complaintList) {
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

        holder.email.setText(complaint.getEmail());
        holder.location.setText(complaint.getLocation());
        holder.created_at.setText(complaint.getCreated_at());
        holder.id.setText(complaint.getId());
        holder.problem.setText(complaint.getProblem());

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    InputStream in=new URL("https://jaywebsite.000webhostapp.com/android_login_api/uploads/"+complaint.getImage().toString() ).openStream();
                    bmp=BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(bmp!=null){
                    holder.imageView.setImageBitmap(bmp);
                }
                super.onPostExecute(aVoid);
            }
        }.execute();

    }


    @Override
    public int getItemCount() {
        return complaintList.size();
    }



    public class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView email,location,image,created_at,id,problem;
        ImageView imageView;
        Button delete;

        public ComplaintViewHolder(View itemView) {
            super(itemView);
            email=itemView.findViewById(R.id.tvemail);
            location=itemView.findViewById(R.id.tvlocation);
            created_at=itemView.findViewById(R.id.tvcreated_at);
            imageView=itemView.findViewById(R.id.imageView);
            id=itemView.findViewById(R.id.tvid);
            delete=itemView.findViewById(R.id.button1);
            delete.setVisibility(View.INVISIBLE);
            problem=itemView.findViewById(R.id.tvproblem);
        }
    }
}
