package jay.com.loginphp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;
import android.view.WindowManager;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin,btnLinkToRegister,forgetpassword;
    private EditText inputEmail,inputPassword,retypenewpassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        forgetpassword = (Button) findViewById(R.id.forget_password);
        retypenewpassword = (EditText)findViewById(R.id.retypenewpassword);
        retypenewpassword.setVisibility(View.INVISIBLE);

        String email=inputEmail.getText().toString();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", email);
                editor.commit();
                Log.e("Value",">>>>"+email);

                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    if(!inputEmail.getText().toString().trim().matches(emailPattern)){
                        inputEmail.setError("Enter Valid Email ID");
                    }
                    if(inputPassword.getText().toString().length()>16 || inputPassword.getText().toString().length()<8){
                        inputPassword.setError("Enter Password Between 8 to 16 charecter");
                        //Toast.makeText(RegisterActivity.this, "Enter Password Between 8 to 16 charecter", Toast.LENGTH_SHORT).show();
                    }else {
                        checkLogin(email, password);
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPassword.setText("");
                inputEmail.setText("");
                inputPassword.setHint("Enter New Password");
                btnLogin.setText("Change Password?");
                forgetpassword.setText("Click here to Login");
                retypenewpassword.setVisibility(View.VISIBLE);
                forgetpassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                        finish();
                    }
                });
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String email = inputEmail.getText().toString().trim();
                        final String password = inputPassword.getText().toString().trim();
                        if (!email.isEmpty() && !password.isEmpty()) {
                            if (!inputEmail.getText().toString().trim().matches(emailPattern)) {
                                inputEmail.setError("Enter Valid Email ID");
                            }
                            if (inputPassword.getText().toString().length() > 16 || inputPassword.getText().toString().length() < 8) {
                                inputPassword.setError("Enter new Password Between 8 to 16 charecter");
                                //Toast.makeText(RegisterActivity.this, "Enter Password Between 8 to 16 charecter", Toast.LENGTH_SHORT).show();
                            }
                            if (!retypenewpassword.getText().toString().trim().equals(inputPassword.getText().toString().trim())){
                                retypenewpassword.setError("Password Does not Match");
                            }
                            else {
                                pDialog.setMessage("Wait While We sending Mail...");
                                showDialog();
                                StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_FORGET_PASSWORD, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        hideDialog();
                                        try {
                                            JSONObject jObj = new JSONObject(response);
                                            boolean error = jObj.getBoolean("error");

                                            // Check for error node in json
                                            if (!error) {
                                                String errorMsg = jObj.getString("error_msg");
                                                Toast.makeText(getApplicationContext(),
                                                        errorMsg, Toast.LENGTH_LONG).show();
                                            } else {
                                                String errorMsg = jObj.getString("error_msg");
                                                Toast.makeText(getApplicationContext(),
                                                        errorMsg, Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            // JSON error
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e(TAG, "Login Error: " + error.getMessage());
                                        Toast.makeText(getApplicationContext(),
                                                error.getMessage(), Toast.LENGTH_LONG).show();
                                        hideDialog();
                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        // Posting parameters to login url
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("email", email);
                                        params.put("password", password);

                                        return params;
                                    }
                                };

                                // Adding request to request queue
                                AppController.getInstance().addToRequestQueue(strReq);
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "Please enter the credentials!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session


                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        String status = user.getString("user_status");
                        if (status.equals(String.valueOf(1))){
                            session.setLogin(true);
                            // Inserting row in users table
                            db.addUser(name, email, uid, created_at);

                            // Launch main activity
                            Intent intent = new Intent(LoginActivity.this,
                                    Main2Activity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Toast.makeText(LoginActivity.this, "active your account by email id...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}