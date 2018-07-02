package jay.com.loginphp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    TextView useremail;

    private SQLiteHandler db;
    private SessionManager session;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) ==PackageManager.PERMISSION_GRANTED) {
                // SqLite database handler
                db = new SQLiteHandler(getApplicationContext());

                // session manager
                session = new SessionManager(getApplicationContext());

                if (!session.isLoggedIn()) {
                    logoutUser();
                }

                // Fetching user details from sqlite

                HashMap<String, String> user = db.getUserDetails();
                String email = user.get("email");
                Log.e("email main2", ">>>>" + user.get("email"));

                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", email);
                editor.commit();

                NavigationView navigationView1 = (NavigationView) findViewById(R.id.nav_view);
                View hview = navigationView.getHeaderView(0);
                useremail = (TextView) hview.findViewById(R.id.textforemail);

                useremail.setText(user.get("email"));
            } else {
              requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION},666);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 666:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(Main2Activity.this,Main2Activity.class));
                }else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Change Permissions in Settings");
                    alertDialogBuilder
                            .setMessage("" +
                                    "\nClick SETTINGS to Manually Set\n"+"Permissions to use Database Storage")
                            .setCancelable(false)
                            .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1000);     // Comment 3.
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setTitle("Second Chance");
                        alertDialogBuilder
                                .setMessage("Click RETRY to Set Permissions to Allow\n\n"+"Click EXIT to the Close App")
                                .setCancelable(false)
                                .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Integer.parseInt(WRITE_EXTERNAL_STORAGE));
                                        Intent i = new Intent(Main2Activity.this,Main2Activity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                break;
        }
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {

         DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            session.setLogin(false);

            db.deleteUsers();

            // Launching the login activity
            Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.newcomplaint) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                New_Complaint myFragment = new New_Complaint();
                transaction.replace(R.id.container_main, myFragment).addToBackStack(null);
                transaction.commit();
        } else if (id == R.id.pastcomplaint) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container_main, new Past_Complaint()).addToBackStack(null);
                ft.commit();
        } else if(id == R.id.allcomplaint){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_main, new complaint_filter()).addToBackStack(null);
            ft.commit();
        }else if (id == R.id.nav_share) {
           ArrayList<Uri> uris=new ArrayList<Uri>();
           Intent sendintent=new Intent(Intent.ACTION_SEND_MULTIPLE);
           sendintent.setType("application/*");
           uris.add(Uri.fromFile(new File(getApplicationInfo().publicSourceDir)));
           sendintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
           startActivity(Intent.createChooser(sendintent,null));
        } else if (id == R.id.nav_send) {

        } else if(id == R.id.logout) {
            logoutUser();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
