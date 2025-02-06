package com.sp.sgnotrash;

public class Report {
    private String report_id;
    private String name;
    private String description;
    private String location;
    private String image;
    private String lat;
    private String lon;

    // Getters and setters
    public String getReport_id() { return report_id; }
    public void setReport_id(String report_id) { this.report_id = report_id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }
}