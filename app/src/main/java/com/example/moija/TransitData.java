package com.example.moija;

public class TransitData {
    private String firstStartStation;
    private String lastEndStation;
    private String busNo;

    public TransitData(String firstStartStation, String lastEndStation, String busNo) {
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.busNo = busNo;
    }

    public String getFirstStartStation() {
        return firstStartStation;
    }

    public void setFirstStartStation(String firstStartStation) {
        this.firstStartStation = firstStartStation;
    }

    public String getLastEndStation() {
        return lastEndStation;
    }

    public void setLastEndStation(String lastEndStation) {
        this.lastEndStation = lastEndStation;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    // 필요한 Getter 메서드 추가
}