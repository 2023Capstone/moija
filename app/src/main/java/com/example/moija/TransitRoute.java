package com.example.moija;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class TransitRoute {
    private List<String> busNumbers;
    private List<String> startNames;
    private List<String> endNames;
    private List<Double> startCoordinates;
    private List<Double> endCoordinates;

    public List<String> getBusNumbers() {
        return busNumbers;
    }

    public void setBusNumbers(List<String> busNumbers) {
        this.busNumbers = busNumbers;
    }

    public List<String> getStartNames() {
        return startNames;
    }

    public void setStartNames(List<String> startNames) {
        this.startNames = startNames;
    }

    public List<String> getEndNames() {
        return endNames;
    }

    public void setEndNames(List<String> endNames) {
        this.endNames = endNames;
    }

    public List<Double> getStartCoordinates() {
        return startCoordinates;
    }

    public void setStartCoordinates(List<Double> startCoordinates) {
        this.startCoordinates = startCoordinates;
    }

    public List<Double> getEndCoordinates() {
        return endCoordinates;
    }

    public void setEndCoordinates(List<Double> endCoordinates) {
        this.endCoordinates = endCoordinates;
    }

    public TransitRoute() {
        this.busNumbers = new ArrayList<>();
        this.startNames = new ArrayList<>();
        this.endNames = new ArrayList<>();
        this.startCoordinates = new ArrayList<>();
        this.endCoordinates = new ArrayList<>();
    }

    public void addBusNumber(String busNo) {
        busNumbers.add(busNo);
    }

    public void setStartEndNames(String startName, String endName) {
        startNames.add(startName);
        endNames.add(endName);
    }

    public void setStartEndCoordinates(double startX, double startY, double endX, double endY) {
        startCoordinates.add(startX);
        startCoordinates.add(startY);
        endCoordinates.add(endX);
        endCoordinates.add(endY);
    }

    public String formatData() {
        StringBuilder formattedData = new StringBuilder();
        for (int i = 0; i < busNumbers.size(); i++) {
            formattedData.append("Bus Numbers: [").append(busNumbers.get(i)).append("]\n");
            formattedData.append("Start Names: [").append(startNames.get(i)).append("]\n");
            formattedData.append("End Names: [").append(endNames.get(i)).append("]\n");
            formattedData.append("Start Coordinates: [").append(startCoordinates.get(i * 2)).append(", ")
                    .append(startCoordinates.get(i * 2 + 1)).append("]\n");
            formattedData.append("End Coordinates: [").append(endCoordinates.get(i * 2)).append(", ")
                    .append(endCoordinates.get(i * 2 + 1)).append("]\n\n");
        }
        return formattedData.toString();
    }

    @Override
    public String toString() {
        return "TransitRoute{" +
                "busNumbers=" + busNumbers +
                ", startNames=" + startNames +
                ", endNames=" + endNames +
                ", startCoordinates=" + startCoordinates +
                ", endCoordinates=" + endCoordinates +
                '}';
    }
}