package com.donhan.apps.bethere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisualizationActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private VisualizationAdapter mAdapter;
    private long mCheckInID;
    private String mSelectedLocation_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        mListView = (ListView)findViewById(R.id.visualization_list);
        mAdapter = new VisualizationAdapter(this);
        mListView.setAdapter(mAdapter);

        mCheckInID = getIntent().getLongExtra("id", -1);
        mSelectedLocation_name = CheckInLocation.findById(CheckInLocation.class, mCheckInID).name;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");

        mAdapter.clear();

        List<CheckInCoordinate> rCoord = CheckInCoordinate.find(CheckInCoordinate.class, "name=?", mSelectedLocation_name);
        //List<CheckInCoordinate> rCoord = new ArrayList(CheckInCoordinate.listAll(CheckInCoordinate.class));
        Collections.reverse(rCoord);
        for(CheckInCoordinate coord : rCoord ){
            mAdapter.add(coord);
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visualization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
