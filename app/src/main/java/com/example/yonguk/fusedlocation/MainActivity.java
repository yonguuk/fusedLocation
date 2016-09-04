package com.example.yonguk.fusedlocation;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final long INTERVAL = 1000*5;
    private static final long FASTEST_INTERVAL = 1000*3;

    Button btnLocation = null;
    TextView tv, tvLocation = null;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isGooglePlayServicesAvailable()){
            finish();
        }



        tvLocation = (TextView) findViewById(R.id.tv_location);
        tv = (TextView) findViewById(R.id.tv);
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        buildGoogleApiClient();
    }

    private boolean isGooglePlayServicesAvailable(){
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS == status){
            Log.d(TAG,"Available");
            return true;
        } else{
            //GooglePlayServicesUtil.getErrorDialog(status,this,0).show();
            GoogleApiAvailability.getInstance().getErrorDialog(this,status,0).show();
            Log.d(TAG,"Not Available");
            return false;
        }
    }

    protected synchronized void buildGoogleApiClient(){
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        createLocationRequest();

    }
    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates(){
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, "Location update started ..............: ");
        }catch (SecurityException e){
            Log.d(TAG, e.toString());
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }

    }


    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        Log.d(TAG, "Location update stopped .......................");


    }

    private void updateUI(){
        if(mCurrentLocation != null){
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lon = String.valueOf(mCurrentLocation.getLongitude());
            tv.setText("위치정보 가져오는중...");
            tvLocation.setText("At time: " + mLastUpdateTime + "\n"
                    + "위도 : " + lat + "\n"
                    + "경도 : " + lon + "\n"
                    + "정확도 : " + mCurrentLocation.getAccuracy() + "\n"
                    + "Provider: " + mCurrentLocation.getProvider());
        } else{
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        DateFormat dateFormat =new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        mLastUpdateTime = dateFormat.format(Calendar.getInstance().getTime());
        updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }



    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

/*
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        Log.d(TAG, "Location update stoped.....................");
    }
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected()){
            //startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}
