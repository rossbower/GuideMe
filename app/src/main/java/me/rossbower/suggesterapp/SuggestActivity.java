package me.rossbower.suggesterapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import me.rossbower.suggesterapp.data.Place;
import me.rossbower.suggesterapp.fragments.ListFragment;
import me.rossbower.suggesterapp.fragments.MapFragment;

public class SuggestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String distance = null;
    private String closestFoodPlaceName;
    private String closestActivityPlaceName;
    private double bestFoodLat;
    private double bestFoodLong;
    private double bestActivityLat;
    private double bestActivityLong;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        TextView tvFoodSuggestion = (TextView) findViewById(R.id.tvFoodSuggestion);
        TextView tvActivitySuggestion = (TextView) findViewById(R.id.tvActivitySuggestion);

        ImageView ivFoodDirect = (ImageView) findViewById(R.id.ivFoodDirection);
        ImageView ivActivityDirect = (ImageView) findViewById(R.id.ivActivityDirection);


        if (getIntent() != null
                && getIntent().hasExtra(MainActivity.FOOD_NAME)
                && getIntent().hasExtra(MainActivity.ACTIVITY_NAME)) {

            closestFoodPlaceName = getIntent().getStringExtra(MainActivity.FOOD_NAME);
            closestActivityPlaceName = getIntent().getStringExtra(MainActivity.ACTIVITY_NAME);

            bestFoodLat = getIntent().getDoubleExtra(MainActivity.FOOD_LAT, 0);
            bestFoodLong = getIntent().getDoubleExtra(MainActivity.FOOD_LONG, 0);
            bestActivityLat = getIntent().getDoubleExtra(MainActivity.ACTIVITY_LAT, 0);
            bestActivityLong = getIntent().getDoubleExtra(MainActivity.ACTIVITY_LONG, 0);
        }

        tvFoodSuggestion.setText(closestFoodPlaceName);
        if (closestFoodPlaceName == null) tvFoodSuggestion.setText(R.string.no_food_places);

        tvActivitySuggestion.setText(closestActivityPlaceName);
        if (closestActivityPlaceName == null) tvActivitySuggestion.setText(R.string.no_activities);

        ivFoodDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((TextView) findViewById(R.id.tvFoodSuggestion))
                        .getText().equals(getString(R.string.no_food_places))) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + bestFoodLat + "," + bestFoodLong));
                    startActivity(intent);
                }
            }
        });

        ivActivityDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((TextView) findViewById(R.id.tvActivitySuggestion))
                        .getText().equals(getString(R.string.no_activities))) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+bestActivityLat+","+bestActivityLong));
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
