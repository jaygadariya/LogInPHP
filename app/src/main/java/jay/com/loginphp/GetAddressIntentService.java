package jay.com.loginphp;

/**
 * Created by JAY GADARIYA on 29-05-2018.
 */
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class GetAddressIntentService extends IntentService {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;

    private static final String IDENTIFIER = "GetAddressIntentService";
    private ResultReceiver addressResultReceiver;

    public GetAddressIntentService() {
        super(IDENTIFIER);
    }

    //handle the address request
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg = "";
        //get result receiver from intent
        addressResultReceiver = intent.getParcelableExtra("add_receiver");

        if (addressResultReceiver == null) {
            Log.e("GetAddressIntentService",
                    "No receiver, not processing the request further");
            return;
        }

        Location location = intent.getParcelableExtra("add_location");

        //send no location error to results receiver
        if (location == null) {
            msg = "No location, can't go further without location";
            sendResultsToReceiver(0, msg);
            return;
        }
        //call GeoCoder getFromLocation to get address
        //returns list of addresses, take first one and send info to result receiver
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }

        if (addresses == null || addresses.size()  == 0) {
            msg = "No address found for the location";
            sendResultsToReceiver(1, msg);
        } else {
            Address address = addresses.get(0);
            StringBuffer addressDetails = new StringBuffer();

                addressDetails.append(address.getFeatureName());
                addressDetails.append("\n");

                addressDetails.append(address.getThoroughfare());
                addressDetails.append("\n");

                addressDetails.append("Locality: ");
                addressDetails.append(address.getLocality());
                addressDetails.append("\n");

                addressDetails.append("County: ");
                addressDetails.append(address.getSubAdminArea());
                addressDetails.append("\n");

                addressDetails.append("State: ");
                addressDetails.append(address.getAdminArea());
                addressDetails.append("\n");

                addressDetails.append("Country: ");
                addressDetails.append(address.getCountryName());
                addressDetails.append("\n");

                addressDetails.append("Postal Code: ");
                addressDetails.append(address.getPostalCode());
                addressDetails.append("\n");


                sendResultsToReceiver(2, addressDetails.toString());

            Log.e("addreess >>>",addressDetails.toString());
        }
    }
    //to send results to receiver in the source activity
    private void sendResultsToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();

        if (message.contains("null")){
            String error="no Location";
            bundle.putString("address_result", error);
        }else {
            bundle.putString("address_result", message);
        }
        //bundle.putString("address_result", message);
        addressResultReceiver.send(resultCode, bundle);
    }
}