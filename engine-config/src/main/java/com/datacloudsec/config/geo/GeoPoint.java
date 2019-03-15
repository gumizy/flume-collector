package com.datacloudsec.config.geo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * GeoPoint
 */
public class GeoPoint implements Serializable {

    private String country;
    private String countryCode;
    private String province;
    private String city;

    private double lng;
    private double lat;

    // 附加信息
    private Map<String, Object> infoMap = new HashMap<>();

    public GeoPoint() {
        super();
    }

    public GeoPoint(double lng, double lat) {
        super();
        this.lng = lng;
        this.lat = lat;
    }

    public GeoPoint(String country, String province, String city, String countryCode) {
        super();
        this.country = country;
        this.province = province;
        this.city = city;
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setInfo(String key, Object value) {
        if (key != null) {
            infoMap.put(key, value);
        }
    }

    public Object getInfo(String key) {
        return key != null ? infoMap.get(key) : null;
    }

    public void clearInfo() {
        infoMap.clear();
    }
}
