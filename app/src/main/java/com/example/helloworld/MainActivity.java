package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private boolean isDeleteMode = false;
    String pnu = "";
    String ReverseGeo = "";

    Marker kunsan_Uni = new Marker();
    Marker kunsan_cityHall = new Marker();


    InfoWindow infoWindow = new InfoWindow();
    // pnu로 받아온 좌표값 저장

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

    public class ReverseGeocording extends AsyncTask<LatLng, String, String> {
        private static final String clientId = "wnz1hbngf0";
        private static final String clientSecret = "aMMYNkT1VQjoQ3ifRJkFYHDIknKp7sv4A35ZSLt8";
        String TextAddress = "";
        @Override
        protected String doInBackground(LatLng... latLng) {
            String strcoord = latLng[0].longitude + "," + latLng[0].latitude;

            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + strcoord + "&sourcecrs=EPSG:4019&orders=addr";
            try {
                Log.d("NaverReverseGeocoding", "apiURL : " + apiURL);
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

                /*
                int responseCode = conn.getResponseCode();
                Log.d("HTTP 응답 코드: ", ""+responseCode);
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                */
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                String tag;
                // inputStream으로부터 xml 값 받기
                xpp.setInput(conn.getInputStream(), null);
                xpp.next();
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {       // 문서가 끝나지 않을때까지
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:              // 문서 시작 시
                            break;                                      // 아무 일도 하지 않음

                        case XmlPullParser.START_TAG:                   // 태그 시작 시
                            tag = xpp.getName();                        // 태그 값을 tag 변수에 저장

                            String zero = "";

                            if (tag.equals("id")) {                     // 태그 값이 id 일때
                                xpp.next();
                                pnu = xpp.getText();                    // pnu 에 값 저장
                                Log.d("NaverReverseGeocoding", "pnu1 : " + pnu);
                                break;
                            }
                            if (tag.equals("land")) {                   // 태그 값이 land 일때
                                xpp.next();
                                tag = xpp.getName();                    // 다음 태그값을 받음
                                if (tag.equals("type")) {               // 태그 값이 type 일때
                                    xpp.next();
                                    pnu = pnu + xpp.getText();          // pnu에 값 추가 저장
                                    Log.d("NaverReverseGeocoding", "pnu2 : " + pnu);
                                }
                            }
                            if (tag.equals("number1")) {
                                xpp.next();
                                xpp.getText().length();
                                for (int i = 0; i < (4 - xpp.getText().length()); i++) {
                                    zero = zero + "0";
                                }
                                pnu = pnu + zero + xpp.getText();
                                Log.d("NaverReverseGeocoding", "pnu3 : " + pnu);

                                TextAddress = TextAddress + xpp.getText();
                                Log.d("NaverReverseGeocoding", "TextAddress5 : " + TextAddress);
                            }
                            if (tag.equals("number2")) {
                                xpp.next();
                                if (xpp.getText() != null) {
                                    xpp.getText().length();

                                    for (int i = 0; i < (4 - xpp.getText().length()); i++) {
                                        zero = zero + "0";
                                    }
                                    pnu = pnu + zero + xpp.getText();
                                    Log.d("NaverReverseGeocoding", "pnu4 : " + pnu);

                                    TextAddress = TextAddress + "-" + xpp.getText() + "번지";
                                    Log.d("NaverReverseGeocoding", "TextAddress6 : " + TextAddress);
                                } else {
                                    pnu = pnu + "0000";
                                    Log.d("NaverReverseGeocoding", "pnu4 : " + pnu);

                                    TextAddress = TextAddress + "번지";
                                    Log.d("NaverReverseGeocoding", "TextAddress6 : " + TextAddress);
                                }
                            }

                            if(tag.equals("area1")) {
                                xpp.next();
                                tag = xpp.getName();
                                if(tag.equals("name")) {
                                    xpp.next();
                                    TextAddress = TextAddress + xpp.getText() + " ";
                                    Log.d("NaverReverseGeocoding", "TextAddress1 : " + TextAddress);
                                }
                            }
                            if(tag.equals("area2")) {
                                xpp.next();
                                tag = xpp.getName();
                                if(tag.equals("name")) {
                                    xpp.next();
                                    TextAddress = TextAddress + xpp.getText() + " ";
                                    Log.d("NaverReverseGeocoding", "TextAddress2 : " + TextAddress);
                                }
                            }
                            if(tag.equals("area3")) {
                                xpp.next();
                                tag = xpp.getName();
                                if(tag.equals("name")) {
                                    xpp.next();
                                    TextAddress = TextAddress + xpp.getText() + " ";
                                    Log.d("NaverReverseGeocoding", "TextAddress3 : " + TextAddress);
                                }
                            }
                            if(tag.equals("area4")) {
                                xpp.next();
                                if(xpp.getName() != null) {
                                    tag = xpp.getName();
                                    if(tag.equals("name")) {
                                        xpp.next();
                                        TextAddress = TextAddress + xpp.getText() + " ";
                                        Log.d("NaverReverseGeocoding", "TextAddress4 : " + TextAddress);
                                    }
                                }
                            }
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                Log.d("error: ", ""+e);
            }
            ReverseGeo = TextAddress;

            return TextAddress;
        }
        @Override
        protected void onPostExecute(String TextAddress) {
            super.onPostExecute(TextAddress);
            Log.d("HTTP Result : ", TextAddress);
        }
    }


    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        naverMap.setLocationSource(locationSource);

        kunsan_Uni.setPosition(new LatLng(35.945347, 126.682148));
        kunsan_Uni.setCaptionText("군산대학교");

        kunsan_cityHall.setPosition(new LatLng(35.967640, 126.736849));
        kunsan_cityHall.setCaptionText("군산시청");

        kunsan_Uni.setMap(naverMap);
        kunsan_cityHall.setMap(naverMap);

        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        final ToggleButton toggleButton2 = (ToggleButton) findViewById(R.id.toggleButton2);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button button = (Button) findViewById(R.id.button);

        InfoWindow infoWindow = new InfoWindow();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Basic", Toast.LENGTH_SHORT).show();
                    naverMap.setMapType(NaverMap.MapType.Basic);
                } else if (position == 1) {
                    naverMap.setMapType(NaverMap.MapType.Navi);
                    Toast.makeText(getApplicationContext(), "Navi", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    naverMap.setMapType(NaverMap.MapType.Satellite);
                    Toast.makeText(getApplicationContext(), "Satellite", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {
                    naverMap.setMapType(NaverMap.MapType.Hybrid);
                    Toast.makeText(getApplicationContext(), "Hybride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        toggleButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, true);
                    toggleButton.setBackgroundColor(Color.GREEN);
                } else {
                    naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_CADASTRAL, false);
                    toggleButton.setBackgroundColor(Color.RED);
                }
            }
        });
        toggleButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton2.isChecked()) {
                    isDeleteMode = true;
                } else {
                    isDeleteMode = false;
                }
            }
        });

        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        });

        // 맵 터치 이벤트로 해당 좌표에 마커 생성
        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            List<LatLng> coords = new ArrayList<>();

            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                Marker touch_marker = new Marker();
                PolygonOverlay polygon = new PolygonOverlay();

                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                touch_marker.setPosition(new LatLng(latitude, longitude));
                touch_marker.setTag(String.format("%f, %f", latitude, longitude));
                touch_marker.setMap(naverMap);

                Collections.addAll(coords,
                        new LatLng(latitude, longitude)
                );

                if(coords.size()>2){
                    //coords.set(0, new LatLng(latitude, longitude));
                    // 아직 반영되지 않음
                    polygon.setCoords(coords);
                    // 반영됨
                    polygon.setOutlineWidth(5);
                    polygon.setMap(naverMap);
                }

                touch_marker.setOnClickListener(overlay -> {
                    if (!isDeleteMode) {
                        if (touch_marker.getInfoWindow() == null) {
                            infoWindow.open(touch_marker);
                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                                @NonNull
                                @Override
                                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                    // reverse geocording
                                    ReverseGeocording reverseGeocording = new ReverseGeocording();
                                    reverseGeocording.execute(latLng);
                                    Log.d("ReverseGeo : ", ReverseGeo);
                                    return (CharSequence) (infoWindow.getMarker().getTag() +"\n" + ReverseGeo);
                                }
                            });
                        } else {
                            infoWindow.close();
                        }
                    } else {
                        touch_marker.setMap(null);
                        coords.remove(new LatLng(latitude, longitude));
                        polygon.setMap(null);
                    }
                    return true;
                });


            }
        });

    }

}