package me.rossbower.suggesterapp.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.rossbower.suggesterapp.R;
import me.rossbower.suggesterapp.data.Place;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    public GoogleMap mMap;
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
//        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        ListFragment listFragment = (ListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0"

        );

        List<Place> placeList = listFragment.placeRecyclerAdapter.getPlaceList();

        for (int i = 0; i < placeList.size(); i++) {
            Marker marker = mMap.addMarker(
                    new MarkerOptions().
                            position(new LatLng(placeList.get(i).getLatitude(),
                                    placeList.get(i).getLongitude())).
                            title(placeList.get(i).getPlaceName()));
            marker.setDraggable(true);
        }

        if (placeList.size()>0) {
            LatLng move = new LatLng(placeList.get(placeList.size() - 1).getLatitude(),
                    placeList.get(placeList.size() - 1).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(move,12));
        }
    }

    public void updateMap() {
        mMap.clear();

        ListFragment listFragment = (ListFragment) getActivity().getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.pager+":0"

        );

        List<Place> placeList = listFragment.placeRecyclerAdapter.getPlaceList();

        for (int i = 0; i < placeList.size(); i++) {
            Marker marker = mMap.addMarker(
                    new MarkerOptions().
                            position(new LatLng(placeList.get(i).getLatitude(),
                                    placeList.get(i).getLongitude())).
                            title(placeList.get(i).getPlaceName()));
            marker.setDraggable(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            updateMap();
        }

    }

    public void addPlace(com.google.android.gms.location.places.Place place) {
        Marker marker = mMap.addMarker(
                    new MarkerOptions().
                            position(place.getLatLng()).
                            title(place.getName().toString()));
            marker.setDraggable(true);

    }

}
