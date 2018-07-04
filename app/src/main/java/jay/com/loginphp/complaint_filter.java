package jay.com.loginphp;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
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
public class complaint_filter extends Fragment {
    private ProgressDialog pDialog;
    List<Complaint> complaintList;
    RecyclerView recyclerView;
    Spinner spinner;

    public static final String MyPREFERENCES = "MyPrefs" ;

    public complaint_filter() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complaint_filter, container, false);

        recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView2);
        spinner=(Spinner)view.findViewById(R.id.complaint_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemId()==0){
                    Toast.makeText(getActivity(), "Select Proper Problem", Toast.LENGTH_LONG).show();
                }
                else {
                    String spinner_complaint= (String) spinner.getSelectedItem();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    complaintList=new ArrayList<>();
                    loadComplaints(spinner_complaint);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "Select Something", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private void loadComplaints(final String spinner_complaint) {
        StringRequest stringRequest=new StringRequest(Request.Method.POST, AppConfig.URL_GETCOMPLAINT, new Response.Listener<String>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    if (array.length()==0){
                        Toast.makeText(getActivity(), "No Complaint", Toast.LENGTH_SHORT).show();
                        int img=R.drawable.ic_delete_black_24dp;
                        recyclerView.setBackground(getResources().getDrawable(img));
//                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.no, new no_complaint()).addToBackStack(null);
//                        ft.commit();
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
                                    complaint.getString("problem"),
                                    complaint.getString("status")
                            ));

                            recyclerView.setBackground(getResources().getDrawable(R.color.white));
                        }
                    }
                    Complaint_filter_Adapter adapter = new Complaint_filter_Adapter(getActivity(), complaintList);
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

                SharedPreferences sharedPreferences=getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                final String email=sharedPreferences.getString("email",null);
                Log.e("EMAIL PAST",">>>>"+email);
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("problem",spinner_complaint);
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
        pDialog=new ProgressDialog(getActivity());
        pDialog.setMessage("Fetching all Complaints...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


}
