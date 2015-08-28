package com.donhan.apps.bethere;

// TDOO: Support mutli-locations
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class CheckInActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG = CheckInActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;

    private Button mCheckInButton;
    private Button mVisButton;

    private Intent mToVis;

    private long mCheckInID;
    private String mSelectedLocation_name;
    private double mCurrentLocation_lon;
    private double mCurrentLocation_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_check_in);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)     // for set-up
                .addApi(LocationServices.API)           // for check-in
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mCheckInID = getIntent().getLongExtra("id", -1);

        mCheckInButton = (Button)findViewById(R.id.check_in_button);
        mVisButton = (Button)findViewById(R.id.vis_button);

        mToVis = new Intent(this, VisualizationActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });

        mVisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToVis.putExtra("id", mCheckInID);
                startActivity(mToVis);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar tem clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        mSelectedLocation_name = CheckInLocation.findById(CheckInLocation.class, mCheckInID).name;

        Log.d(TAG, "mSelectedLocation_name: " + mSelectedLocation_name);
        List<CheckInCoordinate> CICoordinates = CheckInCoordinate.find(CheckInCoordinate.class, "name=?", mSelectedLocation_name);
        if (CICoordinates.size() == 0) { // no coordinate for mSelectedLocation_name
            new AlertDialog.Builder(this)
                    .setTitle("Are you at " + mSelectedLocation_name + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "User is near selected location");
                            launchPlacePicker();
                            // onActivityResult stores the entry
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d(TAG, "User is far away from selected location");
                            Toast.makeText(getApplicationContext(), "Please check-in near your selected location", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    })
                    .show();
                    Log.d(TAG, "dialog is shown");

        }else{
            check_in();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    public void check_in() {
        // TODO: Mayhaps requestLocationUpdate is better? (battery vs. accuracy) -> use getLastLocation and if not works, use update (http://developer.android.com/training/location/receive-location-updates.html)

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mCurrentLocation_lon = mCurrentLocation.getLongitude();
        mCurrentLocation_lat = mCurrentLocation.getLatitude();

        if (mCurrentLocation == null) {
            Log.d(TAG, "No location");
            // options: 1. ask to try again later; 2. wait and requestLocationUpdate (finding your location ... ); 3. turn on GPS or wifi
            Toast.makeText(getApplicationContext(), "Sorry. We can't seem to find you. Try again later", Toast.LENGTH_LONG);
            return;
        }

        Log.d(TAG, String.format("last recorded location: %.4f, %.4f", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        Log.d(TAG, "mCheckInID: " + mCheckInID);
        Log.d(TAG, "findById: " + CheckInLocation.findById(CheckInLocation.class, mCheckInID));

        CheckInCoordinate firstLocation = CheckInCoordinate.find(CheckInCoordinate.class, "name=?", mSelectedLocation_name).get(0);
        double firstLocation_lat = firstLocation.lat;
        double firstLocation_lon = firstLocation.lon;

        Log.d(TAG, "first location's name: " + firstLocation.name);
        float dist[] = new float[1];
        Location.distanceBetween(mCurrentLocation_lat, mCurrentLocation_lon, firstLocation_lat, firstLocation_lon, dist);
        Log.d(TAG, "DISTANCE IN METERS: " + dist[0]);
        if (dist[0] < 100) {
            Log.d(TAG, "We are near the original location");
            CheckInCoordinate new_location = new CheckInCoordinate(mSelectedLocation_name, mCurrentLocation_lat, mCurrentLocation_lon);
            new_location.save();

            // TODO: Animated checkmark
            Toast.makeText(getApplicationContext(), "Check-in Successful!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Log.d(TAG, "We are far away from the original location");

            // improvement options: 1. ask to try again later; 2. wait and retry automatically (finding your location ... ); 3. turn on GPS; 4. add a new location
            Toast.makeText(getApplicationContext(), "You seem to be far away from the location. Please try again", Toast.LENGTH_LONG).show();
            return;
        }


    }

    protected void launchPlacePicker(){
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();

        try {
            startActivityForResult(intentBuilder.build(context), PLACE_PICKER_REQUEST);
            Log.d(TAG, "startActivityForResult successfully ran");
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR");
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(context, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
            Log.d(TAG, "ERROR");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult");
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                Log.d(TAG, "place: "+ place);
                LatLng ll = place.getLatLng();
                // TODO: if Place name is different than the given one, ask to replace the names
                mCurrentLocation_lat = ll.latitude;
                mCurrentLocation_lon = ll.longitude;
                new CheckInCoordinate(mSelectedLocation_name, mCurrentLocation_lat, mCurrentLocation_lon).save();
                Toast.makeText(getApplicationContext(), "Your first check-in is successful!", Toast.LENGTH_LONG).show();
                finish();

            }//else if(resultCode == RESULT_CANCELED){            }
        }
    }
}

// LOOCK INTO GEOFENCING (http://developer.android.com/training/location/geofencing.html)