package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Location extends AppCompatActivity {
    //layout id값을 받는 변수
    private Button button1;
    private TextView txtResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //layout에서 id값을 찾음
        button1 = findViewById(R.id.button1);
        txtResult = findViewById(R.id.txtResult);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        //위치 관리자 객체 생성 locationMainager
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //id값이 button1을 클릭 시 발생하는 이벤트
        //버튼 클릭 시 위도 경도 고도를 가져오고 위치를 계속 받아서 갱신 해준다
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if문은 앱이 위치 정보(ACCESS_FINE_LOCATION)에 대한 권한을 요청하며, 요청이 필요한 경우에만 실행됩니다.
                // 권한이 부여되지 않았다면 사용자에게 권한을 요청하게 됩니다
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( Location.this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                }
                else{
                    android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null) {
                        //위치데이터를 받는 객체 생성
                        LocationData locationData = new LocationData();

                        String provider = location.getProvider();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        double altitude = location.getAltitude();

                        locationData.setLongitude(longitude); //위도
                        locationData.setLatitude(latitude); //경도
                        locationData.setAltitude(altitude); //고도

                        txtResult.setText(
                                "위치정보 : " + provider + "\n" +
                                "위도 : " + longitude + "\n" +
                                "경도 : " + latitude + "\n" +
                                "고도  : " + altitude);
                    }
                    // 위치정보를 원하는 시간, 거리마다 갱신해준다.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                }
            }
        });
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(android.location.Location location) {
            //위도, 경도, 고도를 받는 객체 생성
            LocationData locationData = new LocationData();
            
            // 위치 리스너는 위치정보를 전달할 때 호출되므로 onLocationChanged()메소드 안에 위지청보를 처리를 작업을 구현 해야합니다.
            String provider = location.getProvider();  // 위치정보
            double longitude = location.getLongitude(); // 위도
            double latitude = location.getLatitude(); // 경도
            double altitude = location.getAltitude(); // 고도
            
            //setter 사용
            locationData.setLongitude(longitude); //위도
            locationData.setLatitude(latitude); //경도
            locationData.setAltitude(altitude); //고도

            txtResult.setText(
                    "위치정보 : " + provider + "\n" +
                    "위도 : " + longitude + "\n" +
                    "경도 : " + latitude + "\n" +
                    "고도 : " + altitude);

        } public void onStatusChanged(String provider, int status, Bundle extras) {

        } public void onProviderEnabled(String provider) {

        } public void onProviderDisabled(String provider) {

        }
    };

}