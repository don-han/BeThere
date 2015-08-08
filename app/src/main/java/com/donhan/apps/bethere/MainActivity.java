package com.donhan.apps.bethere;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
                CheckInLocation.listAll(CheckInLocation.class).size();
                CheckInLocation yeah = new CheckInLocation("Yeah "+CheckInLocation.listAll(CheckInLocation.class).size());
                yeah.save();
                mAdapter.add(yeah);
                Log.d(TAG, "button clicked");

                mAdapter.notifyDataSetChanged();
            }
        });

        mListView = (ListView)findViewById(R.id.list);
        mAdapter = new CheckinListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), CheckInActivity.class);
                intent.putExtra("id", mAdapter.getItem(i).getId());
                startActivity(new Intent(getBaseContext(), CheckInActivity.class));
            }
        });

        if(CheckInLocation.listAll(CheckInLocation.class).size() == 0) {
            new CheckInLocation("Gainz at the Gym").save();
            new CheckInLocation("Apache Spark with Don").save();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
