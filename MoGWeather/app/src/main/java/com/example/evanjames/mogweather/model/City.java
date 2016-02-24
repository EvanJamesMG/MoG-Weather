package com.example.evanjames.mogweather.model;

/**
 * Created by EvanJames on 2015/8/24.
 */
public class City {

    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getCityName(){
        return cityName;
    }
    public   void setCityName(String cityName){
        this.cityName = cityName;
    }
    public String getCityCode(){
        return  cityCode;
    }
    public void setCityCode(String cityCode){
        this.cityCode = cityCode;
    }
    public int getProvinceId() {
        return provinceId;
    }
    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
