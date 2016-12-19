package org.break_out.breakout.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostingLocation {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("teamId")
    @Expose
    private Integer teamId;
    @SerializedName("event")
    @Expose
    private String event;
    @SerializedName("eventId")
    @Expose
    private Integer eventId;

//    @SerializedName("locationData")
//    @Expose
//    private com.example.LocationData locationData;

    @SerializedName("duringEvent")
    @Expose
    private Boolean duringEvent;

    /**
     * @return The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The date
     */
    public Integer getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(Integer date) {
        this.date = date;
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * @param distance The distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     * @return The team
     */
    public String getTeam() {
        return team;
    }

    /**
     * @param team The team
     */
    public void setTeam(String team) {
        this.team = team;
    }

    /**
     * @return The teamId
     */
    public Integer getTeamId() {
        return teamId;
    }

    /**
     * @param teamId The teamId
     */
    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    /**
     * @return The event
     */
    public String getEvent() {
        return event;
    }

    /**
     * @param event The event
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * @return The eventId
     */
    public Integer getEventId() {
        return eventId;
    }

    /**
     * @param eventId The eventId
     */
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    /**
     * @return The locationData
     */
//    public LocationData getLocationData() {
//        return locationData;
//    }

    /**
     * @param locationData The locationData
     */
//    public void setLocationData(LocationData locationData) {
//        this.locationData = locationData;
//    }

    /**
     * @return The duringEvent
     */
    public Boolean getDuringEvent() {
        return duringEvent;
    }

    /**
     * @param duringEvent The duringEvent
     */
    public void setDuringEvent(Boolean duringEvent) {
        this.duringEvent = duringEvent;
    }

}