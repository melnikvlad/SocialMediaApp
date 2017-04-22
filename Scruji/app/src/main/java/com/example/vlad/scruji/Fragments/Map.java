package com.example.vlad.scruji.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.UpdateLocationInterface;
import com.example.vlad.scruji.Interfaces.getMarkersInterface;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Markers;
import com.example.vlad.scruji.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
public class Map extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final int REQUEST_FINE_LOCATION = 1001;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Marker marker;
    private Marker otherMarker;
    private Circle circle;
    private SharedPreferences pref;
    private List<Marker> markers = new ArrayList<>();
    private List<LatLng> markersList = new ArrayList<>();
    private ArrayList<Markers> markerResponse = new ArrayList<>();
    final Handler h = new Handler();
    final int delay = 30000;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_filter, container, false);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        pref = getPreferences();

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
                if(marker != null){
                    marker.remove();
                    circle.remove();

                }

                circle = mGoogleMap.addCircle(new CircleOptions()
                    .center(mLocation)
                    .radius(Constants.RADIUS)
                    .strokeColor(Color.rgb(0, 136, 255))
                    .fillColor(Color.argb(20, 0, 136, 255)));

                marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(mLocation)
                        .flat(false)
                        .title("Это я:)"));

//
            getMarkersList(mLocation);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,14);
                mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    private void drawMarkers(LatLng latLng, List<LatLng> positions,double range) {
        int i = 0;

                if (otherMarker != null) {
                    otherMarker.remove();
                }
        for (LatLng position : positions) {
            otherMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .visible(false)
                            .title(String.valueOf(i)));
            markers.add(otherMarker);
            i++;
        }

        for (Marker marker : markers) {
            if (SphericalUtil.computeDistanceBetween(latLng, marker.getPosition()) < range) {
                marker.setVisible(true);
            }
        }
    }

    private void getMarkersList(final LatLng mPos){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        getMarkersInterface service = retrofit.create(getMarkersInterface.class);
        Call<ArrayList<Markers>> call = service.operation();
        call.enqueue(new retrofit2.Callback<ArrayList<Markers>>() {
            @Override
            public void onResponse(Call<ArrayList<Markers>> call, Response<ArrayList<Markers>> response) {
                markerResponse = response.body();
                for(Markers m : markerResponse){
                    markersList.add(new LatLng(Double.valueOf(m.getLatitude()),Double.valueOf(m.getLongtitude())));
                }
                drawMarkers(mPos,markersList,Constants.RADIUS);
            }
            @Override
            public void onFailure(Call<ArrayList<Markers>> call, Throwable t) {
                Log.d(Constants.TAG,"Markers error:"+ t.getMessage());
            }
        });
    }

    private void updateUserLocation(String user_id,String latitude,String longtitude){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UpdateLocationInterface service = retrofit.create(UpdateLocationInterface.class);
        Call<String> call = service.operation(user_id,latitude,longtitude);
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
        Context applicationContext = MainActivity.getContextOfApplication();
        return applicationContext;
    }

    public SharedPreferences getPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivityContex());
        return prefs;
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
        mLocationRequest.setInterval(30000);

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

