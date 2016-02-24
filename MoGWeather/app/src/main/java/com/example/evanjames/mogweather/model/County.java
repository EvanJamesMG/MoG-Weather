package com.example.evanjames.mogweather.model;

/**
 * Created by EvanJames on 2015/8/24.
 */
public class County {
    
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getCountyName(){
        return countyName;
    }
    public   void setCountyName(String countyName){
        this.countyName = countyName;
    }
    public String getCountyCode(){
        return  countyCode;
    }
    public void setCountyCode(String countyCode){
        this.countyCode = countyCode;
    }
    public int getCityId() {
        return cityId;
    }
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
