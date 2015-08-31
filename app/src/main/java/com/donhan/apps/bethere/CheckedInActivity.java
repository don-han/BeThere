package com.donhan.apps.bethere;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;

public class CheckedInActivity extends AppCompatActivity {
    private static final String TAG = CheckedInActivity.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;

    private String mName;
    private double mLat;
    private double mLon;

    private Button mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_in);

        mShareButton = (Button) findViewById(R.id.share_button);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share_intent = new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.putExtra(Intent.EXTRA_TEXT, "I just checked-in to " + mName + "!");
                share_intent.setType("text/plain");
//                startActivity(Intent.createChooser(share_intent, "Share to"));
                startActivity(share_intent);
            }
        });

        mName = getIntent().getStringExtra("name");
        mLat = getIntent().getDoubleExtra("lat", -1);
        mLon = getIntent().getDoubleExtra("lon", -1);

        Log.d(TAG, "mName: " + mName);
        Log.d(TAG, "mLat: " + mLat);
        Log.d(TAG, "mLon: " + mLon);

    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checked_in, menu);

//        Intent i = new Intent();
//        i.setAction(Intent.ACTION_SEND);
//        i.putExtra(Intent.EXTRA_TEXT, "I have just checked-in to " + mName + "!");
//        i.setType("text/plain");
//        setShareIntent(i);

//        MenuItem item = menu.findItem(R.id.menu_item_share);
//        MenuItemCompat.setActionProvider(item, mShareActionProvider) ;

        return true;
    }

//    private void setShareIntent(Intent shareIntent){
//        if (mShareActionProvider != null) {
//            mShareActionProvider.;
//        }
//    }

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
