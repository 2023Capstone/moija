package com.example.moija;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private android.widget.Button location_btn;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationData locationData = new LocationData(); // Initialize the LocationData object

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);

        mapView = view.findViewById(R.id.map_view);
        location_btn = view.findViewById(R.id.location_btn);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapView.start(new KakaoMapReadyCallback() {

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

        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    locationData.setLatitude(location.getLatitude());
                                    locationData.setLongitude(location.getLongitude());
                                }
                            }
                        });
            }
        });

        return view;
    }
}