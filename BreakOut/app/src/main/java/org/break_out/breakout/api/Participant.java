package org.break_out.breakout.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by florianschmidt on 07/12/2016.
 */
public class Participant {

    @SerializedName("eventId")
    @Expose
    private Integer eventId;
    @SerializedName("eventCity")
    @Expose
    private String eventCity;
    @SerializedName("teamId")
    @Expose
    private Integer teamId;
    @SerializedName("teamName")
    @Expose
    private String teamName;
    @SerializedName("tshirtSize")
    @Expose
    private String tshirtSize;

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
     * @return The eventCity
     */
    public String getEventCity() {
        return eventCity;
    }

    /**
     * @param eventCity The eventCity
     */
    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
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
     * @return The teamName
     */
    public String getTeamName() {
        return teamName;
    }

    /**
     * @param teamName The teamName
     */
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    /**
     * @return The tshirtSize
     */
    public String getTshirtSize() {
        return tshirtSize;
    }

    /**
     * @param tshirtSize The tshirtSize
     */
    public void setTshirtSize(String tshirtSize) {
        this.tshirtSize = tshirtSize;
    }

}
