package org.break_out.breakout.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by florianschmidt on 07/12/2016.
 */
public class ProfilePic {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uploadToken")
    @Expose
    private Object uploadToken;
    @SerializedName("sizes")
    @Expose
    private List<Object> sizes = null;

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
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The uploadToken
     */
    public Object getUploadToken() {
        return uploadToken;
    }

    /**
     *
     * @param uploadToken
     * The uploadToken
     */
    public void setUploadToken(Object uploadToken) {
        this.uploadToken = uploadToken;
    }

    /**
     *
     * @return
     * The sizes
     */
    public List<Object> getSizes() {
        return sizes;
    }

    /**
     *
     * @param sizes
     * The sizes
     */
    public void setSizes(List<Object> sizes) {
        this.sizes = sizes;
    }

}
