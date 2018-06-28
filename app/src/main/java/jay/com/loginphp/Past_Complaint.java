package jay.com.loginphp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
//https://www.simplifiedcoding.net/retrieve-data-mysql-database-android/
public class Past_Complaint extends Fragment {

    private ProgressDialog pDialog;
    List<Complaint> complaintList;
    RecyclerView recyclerView;


    public static final String MyPREFERENCES = "MyPrefs" ;

    public Past_Complaint() {
        // Required empty public constructor
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past__complaint, container, false);

                recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView1);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                complaintList=new ArrayList<>();
                loadComplaints();
                return view;
            }


            private void loadComplaints() {
                StringRequest stringRequest=new StringRequest(Request.Method.POST, AppConfig.URL_GETCOMPLAINT, new Response.Listener<String>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length()==0){
                                Toast.makeText(getActivity(), "No Complaint", Toast.LENGTH_SHORT).show();
                                recyclerView.setBackground(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                            }
                            else {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject complaint = array.getJSONObject(i);
                                    complaintList.add(new Complaint(
                                            complaint.getString("email"),
                                            complaint.getString("location"),
                                            complaint.getString("image"),
                                            complaint.getString("created_at"),
                                            complaint.getString("id"),
                                            complaint.getString("problem")
                                    ));
                                    recyclerView.setBackground(getResources().getDrawable(R.color.white));
                                }
                            }
                            ComplaintAdapter adapter = new ComplaintAdapter(getActivity(), complaintList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_LONG).show();
                        pDialog.dismiss();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                        final String email=sharedPreferences.getString("email",null);
                        Log.e("EMAIL PAST",">>>>"+email);
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", email);
                        return params;
                    }
                };

                Volley.newRequestQueue(getActivity()).add(stringRequest);
                pDialog=new ProgressDialog(getActivity());
                pDialog.setMessage("Fetching Your all Complaints...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

};