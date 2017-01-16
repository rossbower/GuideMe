package me.rossbower.suggesterapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.io.Serializable;

public class Place extends SugarRecord implements Serializable{

    private String placeName;
    private double latitude;
    private double longitude;
    private String type = null;

    public Place() {
    }

    public Place(com.google.android.gms.location.places.Place placeObj){
        LatLng latlng = placeObj.getLatLng();
        latitude = latlng.latitude;
        longitude = latlng.longitude;
        placeName = placeObj.getName().toString();
        type = "Tap to set type";
    }
    public String getPlaceName() {
        return placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType(){
        return type;
    }

    public void setType(String newType){
        type = newType;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}