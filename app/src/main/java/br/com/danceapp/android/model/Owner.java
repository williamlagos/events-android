package br.com.danceapp.android.model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Owner {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("external_id")
    @Expose
    private String externalId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner_type")
    @Expose
    private String ownerType;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("last_modified")
    @Expose
    private String lastModified;

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
     * The externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     *
     * @param externalId
     * The external_id
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The ownerType
     */
    public String getOwnerType() {
        return ownerType;
    }

    /**
     *
     * @param externalType
     * The owner_type
     */
    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    /**
     *
     * @return
     * The origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     *
     * @param origin
     * The status
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     *
     * @return
     * The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     *
     * @param active
     * The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     *
     * @return
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     *
     * @return
     * The lastModified
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     *
     * @param lastModified
     * The last_modified
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return name;
    }
}
