package br.com.danceapp.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Generated("org.jsonschema2pojo")
public class Event implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("owner")
    @Expose
    private Integer owner;
    @SerializedName("external_id")
    @Expose
    private String externalId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("start_time")
    @Expose
    private Date startTime;
    @SerializedName("end_time")
    @Expose
    private Date endTime;
    @SerializedName("place_latitude")
    @Expose
    private String placeLatitude;
    @SerializedName("place_longitude")
    @Expose
    private String placeLongitude;
    @SerializedName("place_city")
    @Expose
    private String placeCity;
    @SerializedName("place_state")
    @Expose
    private String placeState;
    @SerializedName("place_country")
    @Expose
    private String placeCountry;
    @SerializedName("place_name")
    @Expose
    private String placeName;
    @SerializedName("place_address")
    @Expose
    private String placeAddress;
    @SerializedName("is_page_owned")
    @Expose
    private boolean isPageOwned;
    @SerializedName("attending_count")
    @Expose
    private Integer attendingCount;
    @SerializedName("interested_count")
    @Expose
    private Integer interestedCount;
    @SerializedName("cover_image_url")
    @Expose
    private String coverImageUrl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("origin")
    @Expose
    private String origin;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("created")
    @Expose
    private Date created;
    @SerializedName("last_modified")
    @Expose
    private Date lastModified;

    public static final Creator<Event> CREATOR = new Creator<Event>() {

        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    protected Event(Parcel in) {
        id = in.readInt();
        owner = in.readInt();
        externalId = in.readString();
        name = in.readString();
        description = in.readString();
        startTime = (Date) in.readSerializable();
        endTime = (Date) in.readSerializable();
        placeLatitude = in.readString();
        placeLongitude = in.readString();
        placeCity = in.readString();
        placeState = in.readString();
        placeCountry = in.readString();
        placeName = in.readString();
        placeAddress = in.readString();
        isPageOwned = in.readInt() == 1;
        attendingCount = in.readInt();
        interestedCount = in.readInt();
        coverImageUrl = in.readString();
        status = in.readString();
        origin = in.readString();
        active = in.readInt() == 1;
        created = (Date) in.readSerializable();
        lastModified = (Date) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(owner);
        parcel.writeString(externalId);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeSerializable(startTime);
        parcel.writeSerializable(endTime);
        parcel.writeString(placeLatitude);
        parcel.writeString(placeLongitude);
        parcel.writeString(placeCity);
        parcel.writeString(placeState);
        parcel.writeString(placeCountry);
        parcel.writeString(placeName);
        parcel.writeString(placeAddress);
        parcel.writeInt(isPageOwned ? 1 : 0);
        parcel.writeInt(attendingCount);
        parcel.writeInt(interestedCount);
        parcel.writeString(coverImageUrl);
        parcel.writeString(status);
        parcel.writeString(origin);
        parcel.writeInt(active ? 1 : 0);
        parcel.writeSerializable(created);
        parcel.writeSerializable(lastModified);
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
     * The owner
     */
    public Integer getOwner() {
        return owner;
    }

    /**
     *
     * @param owner
     * The owner
     */
    public void setOwner(Integer owner) {
        this.owner = owner;
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
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The start_time
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return
     * The endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime
     * The end_time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return
     * The placeLatitude
     */
    public String getPlaceLatitude() {
        return placeLatitude;
    }

    /**
     *
     * @param placeLatitude
     * The placeLatitude
     */
    public void setPlaceLatitude(String placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    /**
     *
     * @return
     * The placeLongitude
     */
    public String getPlaceLongitude() {
        return placeLongitude;
    }

    /**
     *
     * @param placeLongitude
     * The placeLongitude
     */
    public void setPlaceLongitude(String placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    /**
     *
     * @return
     * The placeCity
     */
    public String getPlaceCity() {
        return placeCity;
    }

    /**
     *
     * @param placeCity
     * The city_name
     */
    public void setPlaceCity(String placeCity) {
        this.placeCity = placeCity;
    }

    public String getPlaceState() {
        return placeState;
    }

    public void setPlaceState(String placeState) {
        this.placeState = placeState;
    }

    public String getPlaceCountry() {
        return placeCountry;
    }

    public void setPlaceCountry(String placeCountry) {
        this.placeCountry = placeCountry;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    /**
     *
     * @return
     * The isPageOwned
     */
    public Boolean getIsPageOwned() {
        return isPageOwned;
    }

    /**
     *
     * @param isPageOwned
     * The is_page_owned
     */
    public void setIsPageOwned(Boolean isPageOwned) {
        this.isPageOwned = isPageOwned;
    }

    /**
     *
     * @return
     * The attendingCount
     */
    public Integer getAttendingCount() {
        return attendingCount;
    }

    /**
     *
     * @param attendingCount
     * The attending_count
     */
    public void setAttendingCount(Integer attendingCount) {
        this.attendingCount = attendingCount;
    }

    /**
     *
     * @return
     * The interestedCount
     */
    public Integer getInterestedCount() {
        return interestedCount;
    }

    /**
     *
     * @param interestedCount
     * The interested_count
     */
    public void setInterestedCount(Integer interestedCount) {
        this.interestedCount = interestedCount;
    }

    /**
     *
     * @return
     * The coverImageUrl
     */
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    /**
     *
     * @param coverImageUrl
     * The cover_image_url
     */
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPageOwned() {
        return isPageOwned;
    }

    public void setPageOwned(boolean pageOwned) {
        isPageOwned = pageOwned;
    }

    public String getOrigin() {
        return origin;
    }

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
    public Date getCreated() {
        return created;
    }

    /**
     *
     * @param created
     * The created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     *
     * @return
     * The lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     *
     * @param lastModified
     * The last_modified
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return name;
    }
}
