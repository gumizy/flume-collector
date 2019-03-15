package com.datacloudsec.config.geo.datablock;

import org.apache.commons.lang.StringUtils;

/**
 * data block class
 *
 * @author chenxin<chenxin619315@gmail.com>
 */
public class DataBlock {

    /**
     * city id
     */
    private int city_id;

    /**
     * region address
     */
    //private String region;

    /**
     * region ptr in the db file
     */
    private int dataPtr;

    private String country;

    private String countryCode;

    private String zone;

    private String province;

    private String city;

    private String operator;

    private double lat;

    private double lng;

    //中国|华北|北京市|北京市|联通|41847

    public DataBlock() {

    }

    /**
     * construct method
     *
     * @param city_id city_id
     * @param region  region string
     * @param dataPtr data ptr
     */
    public DataBlock(int city_id, String region, int dataPtr) {
        this.city_id = city_id;
        this.country = "";
        this.countryCode = "";
        this.province = "";
        this.city = "";
        this.lat = 0;
        this.lng = 0;

        //this.region = region;
        this.dataPtr = dataPtr;
        String[] regions = StringUtils.split(region, '|');
        this.country = regions[0];

        if (!regions[1].equals("0")) {
            this.zone = regions[1];
        }

        if (!regions[2].equals("0")) {
            this.province = regions[2];
        }

        if (!regions[3].equals("0")) {
            this.city = regions[3];
        }

        if (!regions[4].equals("0")) {
            this.operator = regions[4];
        }
    }

    public DataBlock(int city_id, String region) {
        this(city_id, region, 0);
    }

    public int getCityId() {
        return city_id;
    }

    public DataBlock setCityId(int city_id) {
        this.city_id = city_id;
        return this;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /*
    public String getRegion() {
        return region;
    }

    public DataBlock setRegion(String region) {
        this.region = region;
        return this;
    }
    */

    public int getDataPtr() {
        return dataPtr;
    }

    public DataBlock setDataPtr(int dataPtr) {
        this.dataPtr = dataPtr;
        return this;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(city_id).append('|').append(region).append('|').append(dataPtr);
        return sb.toString();
    }
    */
}
