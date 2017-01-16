package me.rossbower.suggesterapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import me.rossbower.suggesterapp.adapter.MainPagerAdapter;
import me.rossbower.suggesterapp.fragments.ListFragment;
import me.rossbower.suggesterapp.fragments.MapFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MyLocationManager.OnLocChanged, GoogleApiClient.OnConnectionFailedListener {

    private MyLocationManager myLocationManager = null;
    private Location prevLoc = null;

    private GoogleApiClient googleApiClient;

    private ViewPager pager;

    public static final String KEY_TODO_TO_EDIT = "KEY_TODO_TO_EDIT";
    public static final int REQUEST_CODE_EDIT = 101;
    private int positionToEdit = -1;
    private Long idToEdit = null;

    public static final String FOOD_NAME = "FOOD_NAME";
    public static final String ACTIVITY_NAME = "ACTIVITY_NAME";
    public static final String FOOD_LAT = "FOOD_LAT";
    public static final String FOOD_LONG = "FOOD_LONG";
    public static final String ACTIVITY_LAT = "ACTIVITY_LAT";
    public static final String ACTIVITY_LONG = "ACTIVITY_LONG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myLocationManager = new MyLocationManager(this, getApplicationContext());

        requestNeededPermission();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.btnAddMain);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlacePicker();
            }
        });

        FloatingActionButton btnSuggest = (FloatingActionButton) findViewById(R.id.btnSuggest);
        btnSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateClosestPlace();


            }
        });

    }

    public void calculateClosestPlace() {
        String closestFoodPlaceName = null;
        float bestFoodDistance = 1000000000;
        double bestFoodLat = 0;
        double bestFoodLong = 0;
        double bestActivityLat = 0;
        double bestActivityLong = 0;

        String closestActivityPlaceName = null;
        float bestActivityDistance = 1000000000;

        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0"

        );
        List<me.rossbower.suggesterapp.data.Place> placeList = listFragment.getPlaceList();

        for (int i=0; i<placeList.size(); i++) {
            float distance = 1000000000;

            if (prevLoc != null) {
                Location placeLocation = new Location("");
                placeLocation.setLatitude(placeList.get(i).getLatitude());
                placeLocation.setLongitude(placeList.get(i).getLongitude());
                distance = prevLoc.distanceTo(placeLocation)/1000;
            } else {
                Toast.makeText(this,
                        "We need current location", Toast.LENGTH_LONG).show();
            }


            if (placeList.get(i).getType().equals("Food")) {
                if (distance < bestFoodDistance) {
                    closestFoodPlaceName = placeList.get(i).getPlaceName();
                    bestFoodDistance = distance;
                    bestFoodLat = placeList.get(i).getLatitude();
                    bestFoodLong = placeList.get(i).getLongitude();

                }
            }
            if (placeList.get(i).getType().equals("Activity")) {
                if (distance < bestActivityDistance) {
                    closestActivityPlaceName = placeList.get(i).getPlaceName();
                    bestActivityDistance = distance;
                    bestActivityLat = placeList.get(i).getLatitude();
                    bestActivityLong = placeList.get(i).getLongitude();
                }
            }

        }

        Intent intentShowSuggest = new Intent();
        intentShowSuggest.setClass(MainActivity.this, SuggestActivity.class);
        intentShowSuggest.putExtra(FOOD_NAME,closestFoodPlaceName);
        intentShowSuggest.putExtra(FOOD_LAT, bestFoodLat);
        intentShowSuggest.putExtra(FOOD_LONG, bestFoodLong);
        intentShowSuggest.putExtra(ACTIVITY_NAME,closestActivityPlaceName);
        intentShowSuggest.putExtra(ACTIVITY_LAT, bestActivityLat);
        intentShowSuggest.putExtra(ACTIVITY_LONG, bestActivityLong);
        startActivity(intentShowSuggest);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.pager+":0"

            );
            listFragment.placeRecyclerAdapter.deleteAllItems();

            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.pager+":1"

            );
            mapFragment.mMap.clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            // Handle the add action
            showPlacePicker();
        }
        if (id == R.id.nav_suggest) {
            // Handle the suggest action
            calculateClosestPlace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this,
                        "I need it for gps", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        } else {
            myLocationManager.startLocationMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "FINELOC perm granted", Toast.LENGTH_SHORT).show();

                    myLocationManager.startLocationMonitoring();

                } else {
                    Toast.makeText(MainActivity.this,
                            "FINE perm NOT granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myLocationManager.stopLocationMonitoring();
    }

    @Override
    public void locationChanged(Location location) {
        prevLoc = location;

//        Toast.makeText(this, "Got location",Toast.LENGTH_SHORT).show();
    }

    private void geoCode() {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String streetAddress = "Gázgyár utca 1, Budapest";
        List<Address> locations = null;
        try {
            locations = geocoder.getFromLocationName(streetAddress, 3);

            Toast.makeText(this, locations.get(0).getLongitude()+", "+locations.get(0).getLatitude(),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Place newPlace = PlacePicker.getPlace(this, data);

                ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(
                        "android:switcher:"+R.id.pager+":0"

                );

                listFragment.addPlaceToRecycler(newPlace);

            }
        }
    }

    private void revGeoCode() {
        if (prevLoc != null) {
            double latitude = prevLoc.getLatitude();
            double longitude = prevLoc.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            List<Address> addrs = null;
            try {
                addrs = gc.getFromLocation(latitude, longitude, 10);
                Toast.makeText(this, addrs.get(0).getAddressLine(0)+"\n"+
                                addrs.get(0).getAddressLine(1)+"\n"+
                                addrs.get(0).getAddressLine(2),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showPlacePicker() {
        PlacePicker.IntentBuilder builder =
                new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(MainActivity.this), 101);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google API connect failed: "+connectionResult.getErrorMessage(),
                Toast.LENGTH_SHORT).show();
    }

}
