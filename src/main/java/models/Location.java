package models;

public class Location {

    private static final double RADIUS = 6371.283;
    private final double latitude;
    private final double longitude;

    public Location(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double distanceTo(Location other) {
        double aRadLat = Math.toRadians(this.getLatitude());
        double aRadLong = Math.toRadians(this.getLongitude());
        double bRadLat = Math.toRadians(other.getLatitude());
        double bRadLong = Math.toRadians(other.getLongitude());

        return RADIUS * Math.acos(Math.cos(aRadLat)*Math.cos(bRadLat)*Math.cos(aRadLong - bRadLong) +
                Math.sin(aRadLat)*Math.sin(bRadLat)) * 1000;
    }
}
