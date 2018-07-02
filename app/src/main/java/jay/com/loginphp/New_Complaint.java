package jay.com.loginphp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class New_Complaint extends Fragment implements LocationListener{
    TextView useremail;

    private SQLiteHandler db;
    private SessionManager session;

    private TextView currentAddTv;

    Button complaint;
    private ProgressDialog pDialog;
    TextView locationtext;

    Button btnSubmit, btnCamera;
    private ImageView ivImage;
    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    Spinner spinner;

    String imagepath = "";
    String fname;
    String image;
    File file;

    LocationManager locationManager;
    String provider;

    public static final String MyPREFERENCES = "MyPrefs" ;


    public New_Complaint() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_new__complaint, container, false);

            complaint = (Button) view.findViewById(R.id.btn_complaint);

            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            final String email = sharedPreferences.getString("email", null);
            locationtext = (TextView) view.findViewById(R.id.current_address);
            spinner = (Spinner) view.findViewById(R.id.spinner);

            complaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String location = locationtext.getText().toString().trim();
                    if (locationtext.getText().toString() == "") {
                        statuscheck();
                        Toast.makeText(getActivity(), "Wait While Getting Location", Toast.LENGTH_SHORT).show();
                    } else {
                        String problem = spinner.getSelectedItem().toString();
                        if (spinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(getActivity(), "Choose Right Value In spinner", Toast.LENGTH_SHORT).show();

                        } else {
                            complain(email, location, problem);
                            //Toast.makeText(getActivity(), ""+spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                            //image upload
                            if (cd.isConnectingToInternet()) {
                                if (!upflag) {
                                    Toast.makeText(getActivity(), "Image Not Captured..!", Toast.LENGTH_LONG).show();
                                } else {
                                    saveFile(bitmapRotate, file);
                                }
                            } else {
                                Toast.makeText(getActivity(), "No Internet Connection !", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                }
            });

            cd = new ConnectionDetector(getActivity());


            btnCamera = (Button) view.findViewById(R.id.btnCamera);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);

            cd = new ConnectionDetector(getContext());

            //open camera for image upload
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cameraintent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, 101);
                }
            });

            statuscheck();

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            provider = locationManager.getBestProvider(criteria, false);

            if (provider != null && !provider.equals("")) {
                if (!provider.contains("gps")) { // if gps is disabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    getActivity().sendBroadcast(poke);
                }
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 500, 0, this);
                if (location != null)
                    onLocationChanged(location);
                else
                    location = locationManager.getLastKnownLocation(provider);
                if (location != null)
                    onLocationChanged(location);
                else

                    Toast.makeText(getActivity().getBaseContext(), "Location can't be retrieved",
                            Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity().getBaseContext(), "No Provider Found",
                        Toast.LENGTH_SHORT).show();
            }


        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken
                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }
                            if (Float.valueOf(getImageOrientation()) >= 0) {
                                bitmapRotate = rotateImage(bitmap, Float.valueOf(getImageOrientation()));
                            } else {
                                bitmapRotate = bitmap;
                                bitmap.recycle();
                            }

                            ivImage.setVisibility(View.VISIBLE);
                            ivImage.setImageBitmap(bitmapRotate);

//                            Saving image to mobile internal memory for sometime
                            String root = getContext().getFilesDir().toString();
                            File myDir = new File(root + "/androidlift");
                            Log.e("root",">>"+root);
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);


                            java.util.Date date=new java.util.Date();
                            String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());

//                            Give the file name that u want
                            fname = timeStamp + n + ".jpg";
                            image=fname;

                            imagepath = root + "/androidlift/" + fname;

                            file = new File(myDir, fname);
                            upflag = true;
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    //    In some mobiles image will get rotate so to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            System.out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();
        try {
            FileOutputStream out = new FileOutputStream(destination);
            sourceUri.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(getActivity(), "No Internet Connection..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void statuscheck(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<android.location.Address> addresses;
        String strAdd = "";
        geocoder=new Geocoder(getContext(), Locale.getDefault());


        try {
            addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                locationtext.setText(strAdd);
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("wait While Complenting to AMC..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(imagepath);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload("https://jaywebsite.000webhostapp.com/android_login_api/file_upload.php", "ftitle", "fdescription", fname);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (upflag) {
                Toast.makeText(getContext(), "Complaint Posted Successfully..", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Unfortunately file is not Uploaded..", Toast.LENGTH_LONG).show();
            }
        }
    }
    //complaint function
    public void complain(final String email, final String location,final String problem) {
        String tag_string_req = "req_complaint";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_COMPLAINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getActivity(), "Complaint Posted Successfully", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        //Toast.makeText(getContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Check Your Internet Connection", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("location", location);
                params.put("email", email);
                params.put("fname",image);
                params.put("problem",problem);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        ivImage.setImageBitmap(null);
    }

}
