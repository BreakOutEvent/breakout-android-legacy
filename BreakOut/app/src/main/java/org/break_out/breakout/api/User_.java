package org.break_out.breakout.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by florianschmidt on 07/12/2016.
 */
public class User_ {

    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("participant")
    @Expose
    private Object participant;
    @SerializedName("profilePic")
    @Expose
    private ProfilePic_ profilePic;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("blocked")
    @Expose
    private Boolean blocked;

    /**
     *
     * @return
     * The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     *
     * @param firstname
     * The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     *
     * @return
     * The lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     *
     * @param lastname
     * The lastname
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     *
     * @return
     * The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     *
     * @param gender
     * The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The participant
     */
    public Object getParticipant() {
        return participant;
    }

    /**
     *
     * @param participant
     * The participant
     */
    public void setParticipant(Object participant) {
        this.participant = participant;
    }

    /**
     *
     * @return
     * The profilePic
     */
    public ProfilePic_ getProfilePic() {
        return profilePic;
    }

    /**
     *
     * @param profilePic
     * The profilePic
     */
    public void setProfilePic(ProfilePic_ profilePic) {
        this.profilePic = profilePic;
    }

    /**
     *
     * @return
     * The roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     *
     * @param roles
     * The roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     *
     * @return
     * The blocked
     */
    public Boolean getBlocked() {
        return blocked;
    }

    /**
     *
     * @param blocked
     * The blocked
     */
    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

}
