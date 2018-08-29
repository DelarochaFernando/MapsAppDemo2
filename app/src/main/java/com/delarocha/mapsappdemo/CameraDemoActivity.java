package com.delarocha.mapsappdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class CameraDemoActivity extends AppCompatActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private static final String TAG = CameraDemoActivity.class.getName();

    private static final int SCROLL_BY_PIX = 100;

    public static final CameraPosition BONDI =
            new CameraPosition.Builder().target(new LatLng(-33.891614, 151.276417))
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(50)
                    .build();

    public static final CameraPosition SYDNEY =
            new CameraPosition.Builder().target(new LatLng(-33.87365, 151.20689))
                    .zoom(15.5f)
                    .bearing(0)
                    .tilt(25)
                    .build();

    public static final CameraPosition MTY =
            new CameraPosition.Builder().target(new LatLng(25.6866142, -100.3161126))
            .zoom(15.5f)
            .bearing(300)
            .tilt(50)
            .build();

    public static final CameraPosition CDMX =
            new CameraPosition.Builder().target(new LatLng(19.4978, -99.12691929))
             .zoom(15.5f)
             .bearing(300)
             .tilt(50)
             .build();

    public static final CameraPosition BsAs =
            new CameraPosition.Builder().target(new LatLng(-34.6083, -58.37123436 ))
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(50)
                    .build();

    private GoogleMap mMap;

    private CompoundButton mAnimateToggle;
    private CompoundButton mCustomDurationToggle;
    private SeekBar mCustomDurationBar;
    private PolylineOptions currPolylineOptions;
    private boolean isCanceled = false;
    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_demo);

        mAnimateToggle = (CompoundButton)findViewById(R.id.animate);
        mCustomDurationToggle = (CompoundButton)findViewById(R.id.duration_toggle);
        mCustomDurationBar = (SeekBar)findViewById(R.id.duration_bar);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }
}
