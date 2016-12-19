package org.break_out.breakout.api;

/**
 * Created by florianschmidt on 07/12/2016.
 */


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.break_out.breakout.model.BOLocation;
import org.break_out.breakout.model.BOMedia;
import org.break_out.breakout.sync.model.Posting;

public class NewPosting {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("date")
    @Expose
    private Integer date;
    @SerializedName("postingLocation")
    @Expose
    private PostingLocation postingLocation;
    @SerializedName("media")
    @Expose
    private List<Medium> media = null;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("hasLiked")
    @Expose
    private Boolean hasLiked;
    @SerializedName("hashtags")
    @Expose
    private List<Object> hashtags = null;
    @SerializedName("proves")
    @Expose
    private Object proves;

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
     * @return The text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
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
     * @return The postingLocation
     */
    public PostingLocation getPostingLocation() {
        return postingLocation;
    }

    /**
     * @param postingLocation The postingLocation
     */
    public void setPostingLocation(PostingLocation postingLocation) {
        this.postingLocation = postingLocation;
    }

    /**
     * @return The media
     */
    public List<Medium> getMedia() {
        return media;
    }

    /**
     * @param media The media
     */
    public void setMedia(List<Medium> media) {
        this.media = media;
    }

    /**
     * @return The user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return The comments
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * @param comments The comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * @return The likes
     */
    public Integer getLikes() {
        return likes;
    }

    /**
     * @param likes The likes
     */
    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    /**
     * @return The hasLiked
     */
    public Boolean getHasLiked() {
        return hasLiked;
    }

    /**
     * @param hasLiked The hasLiked
     */
    public void setHasLiked(Boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    /**
     * @return The hashtags
     */
    public List<Object> getHashtags() {
        return hashtags;
    }

    /**
     * @param hashtags The hashtags
     */
    public void setHashtags(List<Object> hashtags) {
        this.hashtags = hashtags;
    }

    /**
     * @return The proves
     */
    public Object getProves() {
        return proves;
    }

    /**
     * @param proves The proves
     */
    public void setProves(Object proves) {
        this.proves = proves;
    }

    public Posting transformToPosting() {

        String teamName = this.getUser().getParticipant().getTeamName();
        String message = this.getText();
        BOLocation loc = null;

        if (this.postingLocation != null) {
            Double latitude = this.postingLocation.getLatitude();
            Double longitude = this.postingLocation.getLongitude();
            Integer ts = this.postingLocation.getDate();
            loc = new BOLocation(ts, latitude, longitude);
        }

        return new Posting(teamName, message, loc, null);
    }

}

