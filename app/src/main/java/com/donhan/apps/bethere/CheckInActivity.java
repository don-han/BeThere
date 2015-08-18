package com.donhan.apps.bethere;

import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.orm.StringUtil;

import java.util.List;

public class CheckInActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    long mCheckInID;

    private final static String TAG = CheckInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        // buildGoogleApiClient
        Log.d(TAG,"Before Google API");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d(TAG,"Google API built");
        mCheckInID = getIntent().getLongExtra("id", -1);
        mGoogleApiClient.connect();

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
        Log.d(TAG, "Connected");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, String.format("last recorded location: %.4f, .4f", mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }else{
            Log.d(TAG, "No location");
            // TODO: wait for a period and retry, or ask them to step outside and retry
            mLastLocation = new Location("");
            mLastLocation.setLatitude(42.6394);
            mLastLocation.setLongitude(-71.3147);
        }
        Log.d(TAG, "mCheckInID: " + mCheckInID);
        Log.d(TAG, "findById: " + CheckInLocation.findById(CheckInLocation.class, mCheckInID));
        String lastLocation_name = CheckInLocation.findById(CheckInLocation.class, mCheckInID).name;
        Log.d(TAG, "lastLocation's name: "+lastLocation_name);

        double mLastLocation_lat = mLastLocation.getLatitude();
        double mLastLocation_lon = mLastLocation.getLongitude();

        List<CheckInCoordinate> CICoordinates = CheckInCoordinate.find(CheckInCoordinate.class, "name=?", lastLocation_name);

        if(CICoordinates.size() == 0){
            CheckInCoordinate very_first_entry = new CheckInCoordinate(lastLocation_name, mLastLocation_lat, mLastLocation_lon);
            very_first_entry.save();
            Log.d(TAG, "There are no entries in the table");
        }else{
            CheckInCoordinate firstLocation = CheckInCoordinate.find(CheckInCoordinate.class, "name=?", lastLocation_name).get(0);
            double firstLocation_lat = firstLocation.lat;
            double firstLocation_lon = firstLocation.lon;
            //double firstLocation_lat = 42.5375;
            //double firstLocation_lon = -71.5125;
            Log.d(TAG, "first location's name: " + firstLocation.name);
            float dist[] = new float[1];
            Location.distanceBetween(mLastLocation_lat, mLastLocation_lon, firstLocation_lat, firstLocation_lon, dist);
            Log.d(TAG, "DISTANCE IN METERS: " + dist[0]);
            if(dist[0] < 25) {
                Log.d(TAG, "We are near the original location");
                CheckInCoordinate new_location = new CheckInCoordinate(lastLocation_name, mLastLocation_lat, mLastLocation_lon);
                new_location.save();
                // TODO: display a checkmark
            }else{
                Log.d(TAG, "We are far away from the original location");
                // TODO: retry, camera option, or display fail
            }
        }



        /*

        Log.d(TAG, "No exception");
        /*
            Log.d(TAG, "Exception occured: " + se);
            // Construct a table
            CheckInCoordinate first_entry = new CheckInCoordinate(lastLocation, mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d(TAG, "mlastLocation: "+mLastLocation);
            first_entry.save();
            //CheckInCoordinate first_coord = CheckInCoordinate.findById(CheckInCoordinate.class,new Long(1));
            Log.d(TAG, "location name: "+first_coord.name);
            Log.d(TAG, "location latitude: "+first_coord.lat);
            Log.d(TAG, "location longitude: "+first_coord.lon);
            Log.d(TAG, "location timestamp: "+first_coord.timestamp); */

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "suspended connection");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"failed connection");
    }
}