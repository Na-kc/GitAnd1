package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        FragmentManager fm = getSupportFragmentManager();

        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (mapFragment == null)
        {
            mapFragment =  MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {

        naverMap.setLocationSource(locationSource);


        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        Button button = (Button)findViewById(R.id.button);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 0) {
                    Toast.makeText(getApplicationContext(),"Basic", Toast.LENGTH_SHORT).show();
                    naverMap.setMapType(NaverMap.MapType.Basic);
                }
                else if(position == 1) {
                    naverMap.setMapType(NaverMap.MapType.Navi);
                    Toast.makeText(getApplicationContext(),"Navi", Toast.LENGTH_SHORT).show();
                }
                else if(position == 2) {
                    naverMap.setMapType(NaverMap.MapType.Satellite);
                    Toast.makeText(getApplicationContext(),"Satellite", Toast.LENGTH_SHORT).show();
                }
                else if(position == 3) {
                    naverMap.setMapType(NaverMap.MapType.Hybrid);
                    Toast.makeText(getApplicationContext(),"Hybride", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        toggleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButton.isChecked()) {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
                    toggleButton.setBackgroundColor(Color.GREEN);
                }
                else {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
                    toggleButton.setBackgroundColor(Color.RED);
                }
            }
        });
        button.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        });

        naverMap.addOnLocationChangeListener(location ->
                Toast.makeText(this,"위도:" + location.getLatitude() + ", 경도: " + location.getLongitude(), Toast.LENGTH_SHORT).show());

    }


}