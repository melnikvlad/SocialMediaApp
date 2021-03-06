package com.example.vlad.scruji.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Markers;
import com.example.vlad.scruji.Models.PicassoMarker;
import com.example.vlad.scruji.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.SphericalUtil;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
public class OldMap extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_FINE_LOCATION = 1001;
//    private static final double EARTH_RADIOUS = 3958.75; // Earth radius;
//    private static final int METER_CONVERSION = 1609;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker me;
    private Marker user;
    private PicassoMarker picassoMarker;
    private Circle circle;
    private SharedPreferences pref;
    private List<Marker> markers = new ArrayList<>();
    private ArrayList<Markers> markerResponse = new ArrayList<>();
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pref = getPreferences();
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        final LatLng mLocation = new LatLng(location.getLatitude(), location.getLongitude());
        updateUserLocation(pref.getString(Constants.UNIQUE_ID,""),String.valueOf(mLocation.latitude),String.valueOf(mLocation.longitude));

        if(location == null){
            Toast.makeText(getActivityContex(),"Cant get current location",Toast.LENGTH_LONG).show();
        }
        else {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                if(me != null){
                    me.remove();
                    circle.remove();
                }

                circle = mGoogleMap.addCircle(new CircleOptions()
                    .center(mLocation)
                    .radius(Double.parseDouble(Constants.RANGE))
                    .strokeColor(Color.rgb(0, 136, 255))
                    .fillColor(Color.argb(20, 0, 136, 255)));

                me = mGoogleMap.addMarker(new MarkerOptions()
                        .position(mLocation)
                        .flat(false));

                viewMarkers(mLocation);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,14);
                mGoogleMap.moveCamera(cameraUpdate);

//                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//                    @Override
//                    public void onCameraIdle() {
//                        // Listener of zooming;
//                        CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
//                        float zoomLevel = cameraPosition.zoom;
//                        VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
//                        LatLng nearLeft = visibleRegion.nearLeft;
//                        LatLng nearRight = visibleRegion.nearRight;
//                        LatLng farLeft = visibleRegion.farLeft;
//                        LatLng farRight = visibleRegion.farRight;
//                        double dist_w = distanceFrom(nearLeft.latitude, nearLeft.longitude, nearRight.latitude, nearRight.longitude);
//                        double dist_h = distanceFrom(farLeft.latitude, farLeft.longitude, farRight.latitude, farRight.longitude);
//                    }
//                });
        }
    }
    
    private void viewMarkers(final LatLng mPos){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<Markers>> call = service.get_all_markers();
        call.enqueue(new retrofit2.Callback<ArrayList<Markers>>() {
            @Override
            public void onResponse(Call<ArrayList<Markers>> call, Response<ArrayList<Markers>> response) {

                markerResponse = response.body();
                drawMarkers(mPos,markerResponse, Double.parseDouble(Constants.RANGE));

            }
            @Override
            public void onFailure(Call<ArrayList<Markers>> call, Throwable t) {
            }
        });
    }
    
    private void drawMarkers(LatLng latLng, List<Markers> response,double range) {
        for (Markers m : response) {
            
            user = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(m.getLatitude()),Double.valueOf(m.getLongtitude()))));
            user.setVisible(false);

            if(!m.getUserId().equals(pref.getString(Constants.UNIQUE_ID,""))) {
                picassoMarker = new PicassoMarker(user);
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .borderWidthDp(1)
                        .cornerRadiusDp(100)
                        .oval(false)
                        .build();

                Picasso.with(getActivity())
                        .load(Constants.PICASSO_MAIN + m.getUserId() + ".png")
                        .transform(transformation)
                        .resize(150, 150)
                        .into(picassoMarker);

                markers.add(user);
            }
        }
        for (Marker marker : markers) {
            if (SphericalUtil.computeDistanceBetween(latLng, marker.getPosition()) < range) {
                marker.setVisible(true);
            }
            else{
                marker.setVisible(false);
            }
        }
    }

//    public double distanceFrom(double lat1, double lng1, double lat2, double lng2)
//    {
//        // Return distance between 2 points, stored as 2 pair location;
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
//                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double dist = EARTH_RADIOUS * c;
//        return Double.valueOf(dist * METER_CONVERSION).floatValue();
//    }

    private void updateUserLocation(String user_id,String latitude,String longtitude){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.save_user_location(user_id,latitude,longtitude);
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

//============================================================================================================================
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getPermissionsToViewMyLocation(mGoogleMap);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

     private void getPermissionsToViewMyLocation(GoogleMap googleMap) {
         if (checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                 checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
             googleMap.setMyLocationEnabled(true);
             googleMap.getUiSettings().setMyLocationButtonEnabled(true);
         } else {
             ActivityCompat.requestPermissions(getActivity(), new String[]{
                             Manifest.permission.ACCESS_FINE_LOCATION,
                             Manifest.permission.ACCESS_COARSE_LOCATION},
                     REQUEST_FINE_LOCATION);
         }
     }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(15000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}

