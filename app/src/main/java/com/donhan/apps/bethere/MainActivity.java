package com.donhan.apps.bethere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

// TODO: Multiple coordinates for each location (multiple entrance, big physical property)
public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private CheckinListAdapter mAdapter;

    private FloatingActionButton mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddButton = (FloatingActionButton)findViewById(R.id.button_add);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCheckInLocation();
            }
        });

        mListView = (ListView)findViewById(R.id.list);
        mAdapter = new CheckinListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startCheckIn(mAdapter.getItem(i));
            }
        });

        if(CheckInLocation.listAll(CheckInLocation.class).size() == 0) {
            new CheckInLocation("IBM Gym").save();
            new CheckInLocation("Mainstacks Library").save();
        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume(){
        super.onResume();

        mAdapter.clear();
        for(CheckInLocation loc:CheckInLocation.listAll(CheckInLocation.class)){
            mAdapter.add(loc);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void createNewCheckInLocation(){

        final EditText editName = new EditText(this);
        // TODO: Ask if a user is near the check-in point; if not, just add the name. If yes, launch PP and store location
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Check In Location")
                .setView(editName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CheckInLocation newLoc = addNewCheckInLocation(editName.getText().toString());
                        startCheckIn(newLoc);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                })
                .show();
    }

    private CheckInLocation addNewCheckInLocation(String name){
        CheckInLocation newLoc = new CheckInLocation(name);
        newLoc.save();
        mAdapter.add(newLoc);
        mAdapter.notifyDataSetChanged();
        return newLoc;
    }

    private void startCheckIn(CheckInLocation cil){
        Intent intent = new Intent(this, CheckInActivity.class);
        intent.putExtra("id", cil.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cleardb) {
            CheckInLocation.deleteAll(CheckInLocation.class);
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
}
