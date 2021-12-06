package models;

public class BusStop {

    private String name;
    private String direction;
    private Location location;

    public BusStop(String name, String direction, Location location) {
        this.name = name;
        this.direction = direction;
        this.location = location;
    }

    public BusStop() { }

    public String getName() {return this.name; }

    public String getDirection() {return this.direction; }

    public Location getLocation() {return this.location; }
}
