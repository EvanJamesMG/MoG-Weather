package com.example.evanjames.mogweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.evanjames.mogweather.db.FeatureWeatherDB;
import com.example.evanjames.mogweather.model.City;
import com.example.evanjames.mogweather.model.County;
import com.example.evanjames.mogweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by EvanJames on 2015/8/24.
 * 由于服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的，所以我们最好再提供一个工具类来解析和处理这种数据。
 */
public class Utility  {
    private static String comfort1_desc;
    private static String comfort1_title;
    private static String clothes1_desc;
    private static String clothes1_title;
    private static String uv1_desc;
    private static String uv1_title;
    private static String wash_car1_desc;
    private static String wash_car1_title;
    private static String sports1_desc;
    private static String sports1_title;
    private static String sportxs1_title;
    private static String travel1_desc;
    private static String travel1_title;

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(FeatureWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);

                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(FeatureWeatherDB featureWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到City表
                    featureWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(FeatureWeatherDB featureWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);

                    featureWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */

    public static boolean handleWeatherResponse(Context context,String response,int i){
        try{
            if (!TextUtils.isEmpty(response)) {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject==null){
                    return false;
                }
                JSONObject res_body = jsonObject.getJSONObject("showapi_res_body");


                JSONObject cityInfo = res_body.getJSONObject("cityInfo");
                String cityName = cityInfo.getString("c3");//城市名字
                String cityID = cityInfo.getString("c1");// 城市id

                JSONObject now = res_body.getJSONObject("now");//实时天气
                String sd = now.getString("sd");//空气湿度
                String now_temperature = now.getString("temperature");//实时气温
                String now_temperature_time = now.getString("temperature_time");//发布时间
                String now_weather = now.getString("weather");//实时天气
                String now_weather_pic_str = now.getString("weather_pic");// 格式"http://appimg.showapi.com/images/weather/icon/day/21.png",

                JSONObject aqiDetail = now.getJSONObject("aqiDetail");//详细的空气信息
                String co = aqiDetail.getString("co");
                String no2 = aqiDetail.getString("no2");
                String o3 = aqiDetail.getString("o3");
                String pm10 = aqiDetail.getString("pm10");
                String pm2_5 = aqiDetail.getString("pm2_5");
                String so2 = aqiDetail.getString("so2");
                String primary_pollutant = aqiDetail.getString("primary_pollutant");
                String quality = aqiDetail.getString("quality");




                JSONObject f1 = res_body.getJSONObject("f1");//第一天的天气
                String day1_wind_direction = f1.getString("day_wind_direction");//风向
                String day1_wind_power = f1.getString("day_wind_power");//风力
                String sun1_begin_end = f1.getString("sun_begin_end");//日出日落时间
                JSONObject index1 = f1.getJSONObject("index");

                if(index1.has("comfort")) {
                    JSONObject comfort1 = index1.getJSONObject("comfort");//舒适度
                     comfort1_desc = comfort1.getString("desc");
                     comfort1_title = comfort1.getString("title");
                }else{
                    comfort1_desc = "暂无信息";
                    comfort1_title = "暂无信息";

                }
                if(index1.has("clothes")){
                    JSONObject clothes1 = index1.getJSONObject("clothes");//穿衣指数
                    clothes1_desc = clothes1.getString("desc");
                    clothes1_title = clothes1.getString("title");
                }else {
                    clothes1_desc = "暂无信息";
                    clothes1_title = "暂无信息";
                }

                if(index1.has("uv")){
                    JSONObject uv1 = index1.getJSONObject("uv");//紫外线指数
                    uv1_desc = uv1.getString("desc");
                    uv1_title = uv1.getString("title");

                }else {
                    uv1_desc = "暂无信息";
                    uv1_title = "暂无信息";
                }

                if(index1.has("wash_car")){
                    JSONObject wash_car1 = index1.getJSONObject("wash_car");//洗车指数
                    wash_car1_desc = wash_car1.getString("desc");
                    wash_car1_title = wash_car1.getString("title");
                }else {
                    wash_car1_desc =  "暂无信息";
                    wash_car1_title =  "暂无信息";
                }

                if(index1.has("sports")){
                    JSONObject sports1 = index1.getJSONObject("sports");//运动指数
                    sports1_desc = sports1.getString("desc");
                    sports1_title = sports1.getString("title");
                }else{
                    sports1_desc =  "暂无信息";
                    sportxs1_title =  "暂无信息";
                }

                if(index1.has("travel")){
                    JSONObject travel1 = index1.getJSONObject("travel");//感冒指数
                    travel1_desc = travel1.getString("desc");
                    travel1_title = travel1.getString("title");
                }else {
                    travel1_desc = "暂无信息";
                    travel1_title = "暂无信息";
                }

                String jiangshui1 = f1.getString("jiangshui");//降水概率
                String weekday1 = f1.getString("weekday");//星期几


                JSONObject f2 = res_body.getJSONObject("f2");//第二天的天气
                String day2_day_air_temperature = f2.getString("day_air_temperature");//白天温度
                String day2_night_air_temperature = f2.getString("night_air_temperature");//夜晚温度
                String day2_day_weather = f2.getString("day_weather");//天气描述
                String day2_day_weather_pic = f2.getString("day_weather_pic");//天气图片
                String weekday2 = f2.getString("weekday");//星期几



                JSONObject f3 = res_body.getJSONObject("f3");//第三天的天气
                String day3_day_air_temperature = f3.getString("day_air_temperature");//温度
                String day3_night_air_temperature = f3.getString("night_air_temperature");//夜晚温度
                String day3_day_weather = f3.getString("day_weather");//天气描述
                String day3_day_weather_pic = f3.getString("day_weather_pic");//天气图片
                String weekday3 = f3.getString("weekday");//星期几


                JSONObject f4 = res_body.getJSONObject("f4");//第四天的天气
                String day4_day_air_temperature = f4.getString("day_air_temperature");//温度
                String day4_night_air_temperature = f4.getString("night_air_temperature");//夜晚温度
                String day4_day_weather = f4.getString("day_weather");//天气描述
                String day4_day_weather_pic = f4.getString("day_weather_pic");//天气图片
                String weekday4 = f4.getString("weekday");//星期几

                JSONObject f5 = res_body.getJSONObject("f5");//第五天的天气
                String day5_day_air_temperature = f5.getString("day_air_temperature");//温度
                String day5_night_air_temperature = f5.getString("night_air_temperature");//夜晚温度
                String day5_day_weather = f5.getString("day_weather");//天气描述
                String day5_day_weather_pic = f5.getString("day_weather_pic");//天气图片
                String weekday5 = f5.getString("weekday");//星期几


                JSONObject f6 = res_body.getJSONObject("f6");//第六天的天气
                String day6_day_air_temperature = f6.getString("day_air_temperature");//温度
                String day6_night_air_temperature = f6.getString("night_air_temperature");//夜晚温度
                String day6_day_weather = f6.getString("day_weather");//天气描述
                String day6_day_weather_pic = f6.getString("day_weather_pic");//天气图片
                String weekday6 = f6.getString("weekday");//星期几


                JSONObject f7 = res_body.getJSONObject("f7");//第七天的天气
                String day7_day_air_temperature = f7.getString("day_air_temperature");//温度
                String day7_night_air_temperature = f7.getString("night_air_temperature");//夜晚温度
                String day7_day_weather = f7.getString("day_weather");//天气描述
                String day7_day_weather_pic = f7.getString("day_weather_pic");//天气图片
                String weekday7 = f7.getString("weekday");//星期几



                saveWeatherInfo(context, cityName, cityID,co,no2,o3,pm10,pm2_5,so2,primary_pollutant,quality, sd, now_temperature, now_temperature_time, now_weather, now_weather_pic_str,
                        day1_wind_direction, day1_wind_power, sun1_begin_end, comfort1_desc, comfort1_title, clothes1_desc, clothes1_title,
                        uv1_desc, uv1_title, wash_car1_desc, wash_car1_title, sports1_desc, sports1_title, travel1_desc, travel1_title,jiangshui1,weekday1,

                        day2_day_air_temperature, day2_night_air_temperature, day2_day_weather, day2_day_weather_pic,weekday2,


                        day3_day_air_temperature, day3_night_air_temperature, day3_day_weather, day3_day_weather_pic,weekday3,


                        day4_day_air_temperature, day4_night_air_temperature, day4_day_weather, day4_day_weather_pic,weekday4,

                        day5_day_air_temperature, day5_night_air_temperature, day5_day_weather, day5_day_weather_pic,weekday5,

                        day6_day_air_temperature, day6_night_air_temperature, day6_day_weather, day6_day_weather_pic,weekday6,

                        day7_day_air_temperature, day7_night_air_temperature, day7_day_weather, day7_day_weather_pic,weekday7,i);
            }else{
                return false;
            }

        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context,String cityName, String cityID,String co,String no2,String o3,String pm10,String pm2_5,String so2,String primary_pollutant,String quality, String sd, String now_temperature,String  now_temperature_time,String  now_weather,String  now_weather_pic_str,
                                       String day1_wind_direction, String day1_wind_power, String sun1_begin_end, String comfort1_desc, String comfort1_title, String clothes1_desc,String  clothes1_title,
                                       String uv1_desc, String uv1_title,String  wash_car1_desc,String  wash_car1_title, String sports1_desc, String sports1_title, String travel1_desc, String travel1_title,String jiangshui1,String weekday1,

                                       String  day2_day_air_temperature, String day2_night_air_temperature, String day2_day_weather, String day2_day_weather_pic,String weekday2,


                                       String day3_day_air_temperature,String  day3_night_air_temperature, String day3_day_weather, String day3_day_weather_pic,String weekday3,


                                       String day4_day_air_temperature,String  day4_night_air_temperature, String day4_day_weather, String day4_day_weather_pic,String weekday4,

                                       String day5_day_air_temperature,String  day5_night_air_temperature, String day5_day_weather, String day5_day_weather_pic,String weekday5,

                                       String day6_day_air_temperature, String day6_night_air_temperature, String day6_day_weather, String day6_day_weather_pic,String weekday6,

                                       String day7_day_air_temperature, String day7_night_air_temperature, String day7_day_weather,String  day7_day_weather_pic,String weekday7,int i){

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();


        if(editor==null){
            Toast.makeText(context, "存储数据过程中出现错误，请重新加载...", Toast.LENGTH_LONG);
        }

        editor.putString("cityName", cityName);
        editor.putString("cityId", cityID);


        if(i==1){
            SharedPreferences.Editor editor1 = context.getSharedPreferences("AutoPositionCity",Context.MODE_PRIVATE).edit();
            editor1.putString("autocity",cityName);
            editor1.apply();
        }

        editor.putString("sd",sd);

        editor.putString("co",co);
        editor.putString("no2",no2);
        editor.putString("o3",o3);
        editor.putString("pm10",pm10);
        editor.putString("pm2_5",pm2_5);
        editor.putString("so2",so2);
        editor.putString("primary_pollutant",primary_pollutant);
        editor.putString("quality",quality);


        editor.putString("now_temperature", now_temperature);//实时温度
        editor.putString("now_temperature_time", now_temperature_time);//发布时间
        editor.putString("now_weather",now_weather);//实时天气描述
        editor.putString("now_weather_pic_str",now_weather_pic_str);//实时天气图片

        editor.putString("day1_wind_direction",day1_wind_direction);//风向
        editor.putString("day1_wind_power",day1_wind_power);//风力
        editor.putString("sun1_begin_end",sun1_begin_end);//日出日落时间
        editor.putString("comfort1_desc",comfort1_desc);
        editor.putString("comfort1_title",comfort1_title);
        editor.putString("clothes1_desc",clothes1_desc);//穿衣指数
        editor.putString("clothes1_title",clothes1_title);
        editor.putString("uv1_desc",uv1_desc);
        editor.putString("uv1_title",uv1_title);//紫外线强度
        editor.putString("wash_car1_desc",wash_car1_desc);//洗车指数
        editor.putString("wash_car1_title",wash_car1_title);
        editor.putString("sports1_desc",sports1_desc);//运动指数
        editor.putString("sports1_title",sports1_title);
        editor.putString("travel1_title",travel1_title);//出游指数
        editor.putString("travel1_desc",travel1_desc);
        editor.putString("jiangshui1",jiangshui1);//降水概率
        editor.putString("weekday1",weekday1);//星期几

        editor.putString("day2_day_air_temperature", day2_day_air_temperature);
        editor.putString("day2_night_air_temperature", day2_night_air_temperature);
        editor.putString("day2_day_weather",day2_day_weather);
        editor.putString("day2_day_weather_pic",day2_day_weather_pic);
        editor.putString("weekday2",weekday2);
        
        editor.putString("day3_day_air_temperature", day3_day_air_temperature);
        editor.putString("day3_night_air_temperature", day3_night_air_temperature);
        editor.putString("day3_day_weather",day3_day_weather);
        editor.putString("day3_day_weather_pic",day3_day_weather_pic);
        editor.putString("weekday3",weekday3);

        editor.putString("day4_day_air_temperature", day4_day_air_temperature);
        editor.putString("day4_night_air_temperature", day4_night_air_temperature);
        editor.putString("day4_day_weather",day4_day_weather);
        editor.putString("day4_day_weather_pic",day4_day_weather_pic);
        editor.putString("weekday4",weekday4);

        editor.putString("day5_day_air_temperature", day5_day_air_temperature);
        editor.putString("day5_night_air_temperature", day5_night_air_temperature);
        editor.putString("day5_day_weather",day5_day_weather);
        editor.putString("day5_day_weather_pic",day5_day_weather_pic);
        editor.putString("weekday5",weekday5);

        editor.putString("day6_day_air_temperature", day6_day_air_temperature);
        editor.putString("day6_night_air_temperature", day6_night_air_temperature);
        editor.putString("day6_day_weather",day6_day_weather);
        editor.putString("day6_day_weather_pic",day6_day_weather_pic);
        editor.putString("weekday6",weekday6);

        editor.putString("day7_day_air_temperature", day7_day_air_temperature);
        editor.putString("day7_night_air_temperature", day7_night_air_temperature);
        editor.putString("day7_day_weather",day7_day_weather);
        editor.putString("day7_day_weather_pic",day7_day_weather_pic);
        editor.putString("weekday7",weekday7);


        editor.apply();

    }

}