package com.example.moija;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.kakao.sdk.template.model.Button;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.sdk.template.model.Button;
import com.kakao.sdk.template.model.Button;
import com.kakao.vectormap.MapType;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.MapViewInfo;
import com.kakao.vectormap.mapwidget.MapWidget;
import com.kakao.vectormap.mapwidget.MapWidgetManager;

public class MapFragment extends Fragment {
    private MapView mapView;
    private Button kakaoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);

        mapView = view.findViewById(R.id.map_view);

//        kakaoButton = view.findViewById(R.id.floating_button);

        mapView.start(new KakaoMapReadyCallback() {
            LocationData locationData = new LocationData();


            @Override
            public void onMapReady(KakaoMap kakaoMap) {

            }

            @Override
            public LatLng getPosition() {
                // 지도 시작 시 위치 좌표를 설정
                return LatLng.from(locationData.getLatitude(), locationData.getLongitude());
            }

            @Override
            public int getZoomLevel() {
                return 15;
            }

            @Override
            public String getViewName() {
                return "MyFirstMap";
            }

            @Override
            public boolean isVisible() {
                return true;
            }

            @Override
            public String getTag() {
                return "FirstMapTag";
            }
        });

        return view;
    }
}