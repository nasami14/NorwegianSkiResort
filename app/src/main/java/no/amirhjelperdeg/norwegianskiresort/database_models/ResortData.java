package no.amirhjelperdeg.norwegianskiresort.database_models;

/**
 * Created by apple on 1/20/18.
 */

public  class ResortData {

    public String  name;
    public String description;
    public String address;
    public String mobile;
    public String email;
    public String zipcode;
    public String province;
    public String country;
    public String openHours;
    public String closeHOurs;
    public String totalLifts;
    public String slopDistance;
    public String totalSlopes;
    public String totalCharges;
    public String imagePath;

    public String easySlope;
    public String intermediateSlope;
    public String advancedSlope;

    public String adultCharges;
    public String childrenCharges;
    public String youthChildren;

    public String chairlLifts;
    public String towRopeLifts;
    public String tBarJBarLifts;

    // default constructor
    public ResortData ()
    {

    }

    public ResortData(String name, String description, String address,
                      String mobile, String emailId, String zipcode, String province,
                      String country, String openHours, String closeHOurs, String totalLifts,
                      String slopDistance, String totalSlopes, String totalCharges,String imagePath,
                      String easySlope ,String intermediateSlope , String advancedSlope,
                      String adultCharges , String childrenCharges , String youthChildren,
                      String chairlLifts , String towRopeLifts , String tBarJBarLifts
                      ) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.mobile = mobile;
        this.email = emailId;
        this.zipcode = zipcode;
        this.province = province;
        this.country = country;
        this.openHours = openHours;
        this.closeHOurs = closeHOurs;
        this.totalLifts = totalLifts;
        this.slopDistance = slopDistance;
        this.totalSlopes = totalSlopes;
        this.totalCharges = totalCharges;
        this.imagePath = imagePath;
        this.easySlope= easySlope;
        this.intermediateSlope=intermediateSlope;
        this.advancedSlope= advancedSlope;
        this.adultCharges=adultCharges;
        this.childrenCharges=childrenCharges;
        this.youthChildren=youthChildren;
        this.chairlLifts=chairlLifts;
        this.towRopeLifts=towRopeLifts;
        this.tBarJBarLifts=tBarJBarLifts;
    }

    public String getEasySlope() {
        return easySlope;
    }

    public void setEasySlope(String easySlope) {
        this.easySlope = easySlope;
    }

    public String getIntermediateSlope() {
        return intermediateSlope;
    }

    public void setIntermediateSlope(String intermediateSlope) {
        this.intermediateSlope = intermediateSlope;
    }

    public String getAdvancedSlope() {
        return advancedSlope;
    }

    public void setAdvancedSlope(String advancedSlope) {
        this.advancedSlope = advancedSlope;
    }

    public String getAdultCharges() {
        return adultCharges;
    }

    public void setAdultCharges(String adultCharges) {
        this.adultCharges = adultCharges;
    }

    public String getChildrenCharges() {
        return childrenCharges;
    }

    public void setChildrenCharges(String childrenCharges) {
        this.childrenCharges = childrenCharges;
    }

    public String getYouthChildren() {
        return youthChildren;
    }

    public void setYouthChildren(String youthChildren) {
        this.youthChildren = youthChildren;
    }

    public String getChairlLifts() {
        return chairlLifts;
    }

    public void setChairlLifts(String chairlLifts) {
        this.chairlLifts = chairlLifts;
    }

    public String getTowRopeLifts() {
        return towRopeLifts;
    }

    public void setTowRopeLifts(String towRopeLifts) {
        this.towRopeLifts = towRopeLifts;
    }

    public String gettBarJBarLifts() {
        return tBarJBarLifts;
    }

    public void settBarJBarLifts(String tBarJBarLifts) {
        this.tBarJBarLifts = tBarJBarLifts;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String name) {
        this.imagePath = imagePath;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String emailId) {
        this.email = emailId;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getCloseHOurs() {
        return closeHOurs;
    }

    public void setCloseHOurs(String closeHOurs) {
        this.closeHOurs = closeHOurs;
    }

    public String getTotalLifts() {
        return totalLifts;
    }

    public void setTotalLifts(String totalLifts) {
        this.totalLifts = totalLifts;
    }

    public String getSlopDistance() {
        return slopDistance;
    }

    public void setSlopDistance(String slopDistance) {
        this.slopDistance = slopDistance;
    }

    public String getTotalSlopes() {
        return totalSlopes;
    }

    public void setTotalSlopes(String totalSlopes) {
        this.totalSlopes = totalSlopes;
    }

    public String getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(String totalCharges) {
        this.totalCharges = totalCharges;
    }
}
