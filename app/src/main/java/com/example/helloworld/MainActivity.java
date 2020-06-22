package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        FragmentManager fm = getSupportFragmentManager();

        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);

        if (mapFragment == null)
        {
            mapFragment =  MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        Button b1 = (Button) findViewById(R.id.button1) ;
        Button b2 = (Button) findViewById(R.id.button2) ;
        Button b3 = (Button) findViewById(R.id.button3) ;
        Button b4 = (Button) findViewById(R.id.button4) ;
        /*Spinner spinner = (Spinner)findViewById(R.id.spinner);
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
        });*/


        b1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Basic", Toast.LENGTH_SHORT).show();
                naverMap.setMapType(NaverMap.MapType.Basic);
            }
        });
        b2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverMap.setMapType(NaverMap.MapType.Navi);
                Toast.makeText(getApplicationContext(),"Navi", Toast.LENGTH_SHORT).show();
            }
        });
        b3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverMap.setMapType(NaverMap.MapType.Satellite);
                Toast.makeText(getApplicationContext(),"Satellite", Toast.LENGTH_SHORT).show();
            }
        });
        b4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                naverMap.setMapType(NaverMap.MapType.Hybrid);
                Toast.makeText(getApplicationContext(),"Hybride", Toast.LENGTH_SHORT).show();
            }
        });

        LatLng coord = new LatLng(37.5670135,126.9783740);

        Toast.makeText(this, "위도:" + coord.latitude + ", 경도: " + coord.longitude, Toast.LENGTH_SHORT).show();
    }


}