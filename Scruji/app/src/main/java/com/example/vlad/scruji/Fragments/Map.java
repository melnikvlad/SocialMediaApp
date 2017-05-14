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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlad.scruji.Adapters.UsersAdapter;
import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.Interfaces.Service;
import com.example.vlad.scruji.MainActivity;
import com.example.vlad.scruji.Models.Markers;
import com.example.vlad.scruji.Models.PicassoMarker;
import com.example.vlad.scruji.Models.UserResponse;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class Map extends Fragment  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    MapView mMapView;
    TextView back,viewRv,gone,type;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mMarker,userMarker;
    PicassoMarker picassoMarker;
    Circle mCircle;
    SharedPreferences sharedPreferences;
    RecyclerView rv;
    RecyclerView.LayoutManager manager;
    UsersAdapter adapter;
    private ArrayList<Markers> users = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        back     = (TextView)view.findViewById(R.id.btn_back);
        type     = (TextView)view.findViewById(R.id.type);
        viewRv   = (TextView)view.findViewById(R.id.btn_view);
        gone     = (TextView)view.findViewById(R.id.btn_gone);
        rv       = (RecyclerView)view.findViewById(R.id.rv);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(getActivity().getApplicationContext());
        sharedPreferences = getPreferences();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Objects.equals(sharedPreferences.getString(Constants.MAP_TYPE, ""), "6"))
                {
                    goToFriends();
                }
                else{
                    goToSearch();
                }
            }
        });
        viewRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.setVisibility(View.VISIBLE);
                gone.setVisibility(View.VISIBLE);
            }
        });
        gone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.setVisibility(View.GONE);
                gone.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(getActivityContex(),"Cant get current location",Toast.LENGTH_LONG).show();
        }
        else{
            LatLng mPos = new LatLng(location.getLatitude(), location.getLongitude());
            updateMyLocation(mPos.latitude,mPos.longitude);
            if(mMarker != null||userMarker!=null){
                mMarker.remove();
                userMarker.remove();
            }

            mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mPos)
                    .flat(false));

            setCameraType(mPos);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(15000);
        checkPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();
        checkPermissions();
    }

    @Override
    public void onStop() {
        super.onStop();
        checkPermissions();
    }

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

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    private void setCameraType(LatLng mPos){
        CameraUpdate cameraUpdate;
        switch (sharedPreferences.getString(Constants.MAP_TYPE,"")){
            case "1":
                type.setText("Показать пользователей в радиусе "+ sharedPreferences.getString(Constants.RANGE,""));
                if(mCircle!=null){
                    mMarker.remove();
                    mCircle.remove();
                }
                mCircle = mGoogleMap.addCircle(new CircleOptions()
                        .center(mPos)
                        .radius(Double.parseDouble(sharedPreferences.getString(Constants.RANGE,"")))
                        .strokeColor(Color.rgb(0, 136, 255))
                        .fillColor(Color.argb(20, 0, 136, 255)));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,15);
                mGoogleMap.moveCamera(cameraUpdate);
                getUsers();
                drawUsers(mPos);
                break;
            case "2":
                type.setText("Показать пользователей в городе");
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,10);
                mGoogleMap.moveCamera(cameraUpdate);
                getUsers();
                drawUsers(mPos);
                break;
            case "3":
                type.setText("Показать пользователей в стране");
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,5);
                mGoogleMap.moveCamera(cameraUpdate);
                getUsers();
                drawUsers(mPos);
                break;
            case "4":
                type.setText("Показать пользователей с моим тэгом "+ sharedPreferences.getString(Constants.TAG_ONCLICK,""));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,13);
                mGoogleMap.moveCamera(cameraUpdate);
                getUsers();
                drawUsersCoordsWithTag(sharedPreferences.getString(Constants.TAG_ONCLICK,""));
                break;
            case "5":
                type.setText("Показать пользователей с тэгом "+ sharedPreferences.getString(Constants.TAG_ONCLICK,""));
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,13);
                mGoogleMap.moveCamera(cameraUpdate);
                getUsers();
                drawUsersCoordsWithTag(sharedPreferences.getString(Constants.TAG_ONCLICK,""));
                break;
            case "6":
                type.setText("Показать моих друзей");
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPos,13);
                mGoogleMap.moveCamera(cameraUpdate);
                getFriends();
                drawFriends();
                break;
        }
    }

    private void drawFriends(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<Markers>> call = service.get_friends_coords(sharedPreferences.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<Markers>>() {
            @Override
            public void onResponse(Call<ArrayList<Markers>> call, Response<ArrayList<Markers>> response) {

                users = response.body();
                drawMarkers(users);
            }
            @Override
            public void onFailure(Call<ArrayList<Markers>> call, Throwable t) {
            }
        });
    }

    private void drawUsers(final LatLng mPos){
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

                users = response.body();

                if(Objects.equals(sharedPreferences.getString(Constants.MAP_TYPE, ""), "1"))
                {
                    drawMarkersinCircle(mPos,users, Double.parseDouble(sharedPreferences.getString(Constants.RANGE,"")));
                }
                else{
                    drawMarkers(users);
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Markers>> call, Throwable t) {
            }
        });
    }

    private void getFriends(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.get_user_friends(sharedPreferences.getString(Constants.UNIQUE_ID,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponse>> call, Response<ArrayList<UserResponse>> response) {

                ArrayList<UserResponse> mResponse = response.body();

                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new UsersAdapter(getActivity(),mResponse);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<ArrayList<UserResponse>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE " +t.getMessage());
            }
        });
    }

    private void getUsers(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<UserResponse>> call = service.users_with_equal_tag(sharedPreferences.getString(Constants.TAG_ONCLICK,""));
        call.enqueue(new retrofit2.Callback<ArrayList<UserResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserResponse>> call, Response<ArrayList<UserResponse>> response) {

                ArrayList<UserResponse> mResponse = response.body();
                manager = new LinearLayoutManager(getActivity());
                rv.setLayoutManager(manager);
                adapter = new UsersAdapter(getActivity(),mResponse);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<ArrayList<UserResponse>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE " +t.getMessage());
            }
        });
    }

    private void drawUsersCoordsWithTag(String tag){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<ArrayList<Markers>> call = service.users_coords_by_tag(tag);
        call.enqueue(new retrofit2.Callback<ArrayList<Markers>>() {
            @Override
            public void onResponse(Call<ArrayList<Markers>> call, Response<ArrayList<Markers>> response) {

                users = response.body();
                drawMarkers(users);
            }
            @Override
            public void onFailure(Call<ArrayList<Markers>> call, Throwable t) {
                Log.d(Constants.TAG,"FAILURE in users coords by tag " +t.getMessage());
            }
        });
    }

    private void drawMarkersinCircle(LatLng mPos, List<Markers> response, double range) {
        for (Markers m : response) {

            userMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(m.getLatitude()),Double.valueOf(m.getLongtitude()))));
            userMarker.setVisible(false);

            if(!m.getUserId().equals(sharedPreferences.getString(Constants.UNIQUE_ID,""))) {
                picassoMarker = new PicassoMarker(userMarker);
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

                markers.add(userMarker);
            }
        }
        for (Marker marker : markers) {
            if (SphericalUtil.computeDistanceBetween(mPos, marker.getPosition()) < range) {
                marker.setVisible(true);
            }
            else{
                marker.setVisible(false);
            }
        }
    }

    private void drawMarkers(List<Markers> response) {
        for (Markers m : response) {

            userMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(m.getLatitude()),Double.valueOf(m.getLongtitude()))));

            if(!m.getUserId().equals(sharedPreferences.getString(Constants.UNIQUE_ID,""))) {
                picassoMarker = new PicassoMarker(userMarker);
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
            }
        }
    }


    private void updateMyLocation(double latitude,double longtitude){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Service service = retrofit.create(Service.class);
        Call<String> call = service.save_user_location(sharedPreferences.getString(Constants.UNIQUE_ID,""),String.valueOf(latitude),String.valueOf(longtitude));
        call.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    private void getPermissionsToViewMyLocation(GoogleMap googleMap) {
        if (checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                            Constants.REQUEST_FINE_LOCATION);
        }
    }

    private void checkPermissions(){
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
                            Constants.REQUEST_FINE_LOCATION);
        }
    }

    public Context getActivityContex(){
        return MainActivity.getContextOfApplication();
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(getActivityContex());
    }

    private void goToSearch() {
        SearchTags fragment = new SearchTags();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map_frame,fragment);
        ft.commit();
    }

    private void goToFriends() {
        Friends fragment = new Friends();
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map_frame,fragment);
        ft.commit();
    }
}
