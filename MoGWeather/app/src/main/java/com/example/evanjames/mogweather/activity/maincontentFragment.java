package com.example.evanjames.mogweather.activity;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.evanjames.mogweather.R;
import com.example.evanjames.mogweather.service.AutoUpdateService;
import com.example.evanjames.mogweather.util.Utility;

import org.json.JSONObject;

/**
 * Created by EvanJames on 2015/10/6.
 */
public class maincontentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private View rootview;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressDialog progressDialog;
    private static boolean AutoPositionLabel;
    private static boolean NoticLabel;
    private static boolean AutoUpdateLabel;
    private RequestQueue mQueue;
    private String listcityname;

    private TextView nowtemperature_set;
    private TextView nowweather_set;
    private TextView pm2_5_set;
    private TextView weekday1_set;
    private ImageView nowweatherpic_set;
    private TextView weekday2_set;
    private TextView weekday3_set;
    private TextView weekday4_set;
    private TextView weekday5_set;
    private TextView jiangyu_set;
    private TextView sd_set;
    private TextView wind_direction_set;
    private TextView wind_power_set;
    private TextView uv_set;
    private TextView sun_begin_end_set;
    private TextView co_set;
    private TextView no2_set;
    private TextView o3_set;
    private TextView pm10_set;
    private TextView pm2_5_two_set;
    private TextView so2_set_set;
    private TextView primary_pollutant_set;
    private TextView quality_set;
    private TextView clothes_set;
    private TextView sports_set;
    private TextView travel_set;
    private TextView washcar_set;
    private ImageView weekday1_weatherpic_set;
    private ImageView weekday2_weatherpic_set;
    private ImageView weekday3_weatherpic_set;
    private ImageView weekday4_weatherpic_set;
    private ImageView weekday5_weatherpic_set;
    private TextView weekday1_temperature_set;
    private TextView weekday2_temperature_set;
    private TextView weekday3_temperature_set;
    private TextView weekday4_temperature_set;
    private TextView weekday5_temperature_set;
    private TextView autopositionText;
    private TextView cityNameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootview =  inflater.inflate(R.layout.maincontent_fragment, container, false);

        mQueue = Volley.newRequestQueue(getActivity());



        nowweatherpic_set = (ImageView)rootview.findViewById(R.id.weatherpicture);
        nowtemperature_set = (TextView)rootview.findViewById(R.id.temperature);
        nowweather_set = (TextView)rootview.findViewById(R.id.weatherdes);
        pm2_5_set = (TextView)rootview.findViewById(R.id.PM2_5_one);
        weekday1_set = (TextView)rootview.findViewById(R.id.week1);
        weekday2_set = (TextView)rootview.findViewById(R.id.week2);
        weekday3_set = (TextView)rootview.findViewById(R.id.week3);
        weekday4_set = (TextView)rootview.findViewById(R.id.week4);
        weekday5_set = (TextView)rootview.findViewById(R.id.week5);
        weekday1_weatherpic_set = (ImageView)rootview.findViewById(R.id.weekweatherimg1);
        weekday2_weatherpic_set = (ImageView)rootview.findViewById(R.id.weekweatherimg2);
        weekday3_weatherpic_set = (ImageView)rootview.findViewById(R.id.weekweatherimg3);
        weekday4_weatherpic_set = (ImageView)rootview.findViewById(R.id.weekweatherimg4);
        weekday5_weatherpic_set = (ImageView)rootview.findViewById(R.id.weekweatherimg5);
        weekday1_temperature_set = (TextView)rootview.findViewById(R.id.weektemperature1);
        weekday2_temperature_set = (TextView)rootview.findViewById(R.id.weektemperature2);
        weekday3_temperature_set = (TextView)rootview.findViewById(R.id.weektemperature3);
        weekday4_temperature_set = (TextView)rootview.findViewById(R.id.weektemperature4);
        weekday5_temperature_set = (TextView)rootview.findViewById(R.id.weektemperature5);


        jiangyu_set = (TextView)rootview.findViewById(R.id.jiangyu);
        sd_set = (TextView) rootview.findViewById(R.id.sd);
        wind_direction_set = (TextView) rootview.findViewById(R.id.wind_direction);
        wind_power_set = (TextView) rootview.findViewById(R.id.wind_power);
        uv_set = (TextView) rootview.findViewById(R.id.uv);
        sun_begin_end_set = (TextView) rootview.findViewById(R.id.sun_begin_end);

        co_set = (TextView) rootview.findViewById(R.id.co_id);
        no2_set = (TextView) rootview.findViewById(R.id.NO2_id);
        o3_set = (TextView) rootview.findViewById(R.id.O3_id);
        pm10_set = (TextView) rootview.findViewById(R.id.PM10_id);
        pm2_5_two_set = (TextView) rootview.findViewById(R.id.PM2_5_id);
        so2_set_set = (TextView) rootview.findViewById(R.id.SO2_id);
        primary_pollutant_set = (TextView) rootview.findViewById(R.id.primary_pollutant_id);
        quality_set = (TextView) rootview.findViewById(R.id.quality_id);

        clothes_set = (TextView) rootview.findViewById(R.id.clothes_des_id);
        sports_set = (TextView)rootview.findViewById(R.id.sports_des_id);
        travel_set = (TextView) rootview.findViewById(R.id.travel_des_id);
        washcar_set = (TextView) rootview.findViewById(R.id.washcar_des_id);


        //首先从数据库中恢复数据
        SharedPreferences pref = getActivity().getSharedPreferences("AppSetting", getActivity().MODE_PRIVATE);
        if(pref != null) {
            AutoPositionLabel = pref.getBoolean("AutoPositionLabel", true);
            AutoUpdateLabel = pref.getBoolean("AutoUpdateLabel", true);
            NoticLabel = pref.getBoolean("NoticLabel", true);
        }

        //下拉刷新
        mySwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_container);

        mySwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_red_light,
                android.R.color.holo_orange_light,android.R.color.holo_blue_light);

        mySwipeRefreshLayout.setOnRefreshListener(this);
        return  rootview;
    }


    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String cityname = prefs2.getString("cityName", "");
                queryWeatherInfoId(cityname);
                mySwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "已是最新", Toast.LENGTH_SHORT).show();
            }
        }, 2000);

    }

    /**
     * 根据城市名称查询对应的天气。
     */
    public void queryWeatherInfoId(String city_name) {

        //获取当前的时间
        java.util.Calendar rightNow = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sim = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String timedate = sim.format(rightNow.getTime());

        String str="area"+java.net.URLEncoder.encode(city_name)+"needHourData1needIndex1needMoreDay1showapi_appid10519showapi_timestamp"+timedate;
        str=str+"4f5bc34c418f435b988aee6ed0b875f6";

//        String sign= null;
//        sign = new String(Hex.encodeHex(DigestUtils.md5(str)));

        String address = "https://route.showapi.com/9-2?area="+java.net.URLEncoder.encode(city_name) +
                "&areaid="+
                "&needHourData=1" +
                "&needIndex=1" +
                "&needMoreDay=1" +
                "&showapi_appid=10519"+
                "&showapi_timestamp="+timedate +
                "&showapi_sign="+"4f5bc34c418f435b988aee6ed0b875f6";

        mainQueryFromServer(address, 2);

    }




    /**
     * 根据接口地址下载相应的json数据，转化并存储数据。
     */
    public void mainQueryFromServer(String address, final int i) {

        showProgressDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(address, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean result = false;
                        result = Utility.handleWeatherResponse(getActivity(), response.toString(), i);
                        if (result) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeProgressDialog();
                                    showWeather(i);
                                    try {
                                        if (NoticLabel) {
                                            showNotification();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);

    }


    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather(int i) {

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getActivity());


        String cityname = prefs2.getString("cityName","");
        String nowweatherpicture = prefs2.getString("now_weather_pic_str", "");
        String nowtemperature = prefs2.getString("now_temperature", "");
        String nowweather = prefs2.getString("now_weather", "");
        String pm2_5 = prefs2.getString("pm2_5", "");

        String weekday2 = prefs2.getString("weekday2", "");
        String weekday3 = prefs2.getString("weekday3", "");
        String weekday4 = prefs2.getString("weekday4", "");
        String weekday5 = prefs2.getString("weekday5", "");
        String weekday6 = prefs2.getString("weekday6", "");

        String day2_weather_pic = prefs2.getString("day2_day_weather_pic", "");
        String day3_weather_pic = prefs2.getString("day3_day_weather_pic", "");
        String day4_weather_pic = prefs2.getString("day4_day_weather_pic", "");
        String day5_weather_pic = prefs2.getString("day5_day_weather_pic", "");
        String day6_weather_pic = prefs2.getString("day6_day_weather_pic", "");

        String day2_day_temperature = prefs2.getString("day2_day_air_temperature", "");
        String day2_night_temperature = prefs2.getString("day2_night_air_temperature", "");
        String day3_day_temperature = prefs2.getString("day3_day_air_temperature", "");
        String day3_night_temperature = prefs2.getString("day3_night_air_temperature", "");
        String day4_day_temperature = prefs2.getString("day4_day_air_temperature", "");
        String day4_night_temperature = prefs2.getString("day4_night_air_temperature", "");
        String day5_day_temperature = prefs2.getString("day5_day_air_temperature", "");
        String day5_night_temperature = prefs2.getString("day5_night_air_temperature", "");
        String day6_day_temperature = prefs2.getString("day6_day_air_temperature", "");
        String day6_night_temperature = prefs2.getString("day6_night_air_temperature", "");


        String jiangyu = prefs2.getString("jiangshui1", "");
        String sd = prefs2.getString("sd", "");
        String wind_direction = prefs2.getString("day1_wind_direction", "");
        String wind_power = prefs2.getString("day1_wind_power", "");
        String uv = prefs2.getString("uv1_title", "");
        String sun_begin_end = prefs2.getString("sun1_begin_end", "");

        String co = prefs2.getString("co", "");
        String no2 = prefs2.getString("no2", "");
        String no3 = prefs2.getString("o3", "");
        String pm10 = prefs2.getString("pm10", "");
        String so2 = prefs2.getString("so2", "");
        String primary_pollutant = prefs2.getString("primary_pollutant", "");
        String quality = prefs2.getString("quality", "");

        String clothes = prefs2.getString("clothes1_desc", "");
        String sports = prefs2.getString("sports1_desc", "");
        String travel = prefs2.getString("travel1_desc", "");
        String washcar = prefs2.getString("wash_car1_desc", "");


        SetWeatherPicture(nowweatherpicture, nowweatherpic_set);
        nowtemperature_set.setText(nowtemperature + "℃");
        nowweather_set.setText(nowweather);
        pm2_5_set.setText("PM2.5: " + pm2_5);
        pm2_5_two_set.setText(pm2_5);


        jiangyu_set.setText(jiangyu);
        sd_set.setText(sd);
        wind_direction_set.setText(wind_direction);
        wind_power_set.setText(wind_power);
        uv_set.setText(uv);
        sun_begin_end_set.setText(sun_begin_end);

        ShowWeekday(weekday1_set, weekday2);
        ShowWeekday(weekday2_set, weekday3);
        ShowWeekday(weekday3_set, weekday4);
        ShowWeekday(weekday4_set, weekday5);
        ShowWeekday(weekday5_set, weekday6);


        SetWeatherPicture2(day2_weather_pic, weekday1_weatherpic_set);
        SetWeatherPicture2(day3_weather_pic, weekday2_weatherpic_set);
        SetWeatherPicture2(day4_weather_pic, weekday3_weatherpic_set);
        SetWeatherPicture2(day5_weather_pic, weekday4_weatherpic_set);
        SetWeatherPicture2(day6_weather_pic, weekday5_weatherpic_set);
        weekday1_temperature_set.setText(day2_day_temperature + "℃" + "-" + day2_night_temperature + "℃");
        weekday2_temperature_set.setText(day3_day_temperature + "℃" + "-" + day3_night_temperature + "℃");
        weekday3_temperature_set.setText(day4_day_temperature + "℃" + "-" + day4_night_temperature + "℃");
        weekday4_temperature_set.setText(day5_day_temperature + "℃" + "-" + day5_night_temperature + "℃");
        weekday5_temperature_set.setText(day6_day_temperature + "℃" + "-" + day6_night_temperature + "℃");

        co_set.setText(co);
        no2_set.setText(no2);
        o3_set.setText(co);
        pm10_set.setText(co);
        so2_set_set.setText(so2);
        primary_pollutant_set.setText(primary_pollutant);
        quality_set.setText(quality);

        clothes_set.setText(clothes);
        sports_set.setText(sports);
        travel_set.setText(travel);
        washcar_set.setText(washcar);


        if (AutoUpdateLabel) {
            Intent intent = new Intent(getActivity(), AutoUpdateService.class);
            getActivity().startService(intent);
        }

    }


    /**
     * 根据json数据显示星期几
     * @param weekday_set
     * @param weekday_num
     */
    private void ShowWeekday(TextView weekday_set, String weekday_num) {

        switch (Integer.parseInt(weekday_num)){
            case 1:
                weekday_set.setText("MON");
                break;
            case 2:
                weekday_set.setText("TUE");
                break;
            case 3:
                weekday_set.setText("WEN");
                break;
            case 4:
                weekday_set.setText("THU");
                break;
            case 5:
                weekday_set.setText("FRI");
                break;
            case 6:
                weekday_set.setText("SAT");
                break;
            case 7:
                weekday_set.setText("SUN");
                break;
            default:
                break;
        }


    }


    /**
     * 根据网络获取的天气图片的地址，设置相应的天气图片
     * @param weather_pic_str
     * @param weatherpic
     */
    private void SetWeatherPicture(String weather_pic_str,ImageView weatherpic) {
        switch (weather_pic_str) {
            case "http://appimg.showapi.com/images/weather/icon/day/00.png":
                weatherpic.setBackgroundResource(R.drawable.day00);break;
            case "http://appimg.showapi.com/images/weather/icon/day/01.png":
                weatherpic.setBackgroundResource(R.drawable.day01);break;
            case "http://appimg.showapi.com/images/weather/icon/day/02.png":
                weatherpic.setBackgroundResource(R.drawable.day02);break;
            case "http://appimg.showapi.com/images/weather/icon/day/03.png":
                weatherpic.setBackgroundResource(R.drawable.day03);break;
            case "http://appimg.showapi.com/images/weather/icon/day/04.png":
                weatherpic.setBackgroundResource(R.drawable.day04);break;
            case "http://appimg.showapi.com/images/weather/icon/day/05.png":
                weatherpic.setBackgroundResource(R.drawable.day05);break;
            case "http://appimg.showapi.com/images/weather/icon/day/06.png":
                weatherpic.setBackgroundResource(R.drawable.day06);break;
            case "http://appimg.showapi.com/images/weather/icon/day/07.png":
                weatherpic.setBackgroundResource(R.drawable.day07);break;
            case "http://appimg.showapi.com/images/weather/icon/day/08.png":
                weatherpic.setBackgroundResource(R.drawable.day08);break;
            case "http://appimg.showapi.com/images/weather/icon/day/09.png":
                weatherpic.setBackgroundResource(R.drawable.day09);break;
            case "http://appimg.showapi.com/images/weather/icon/day/10.png":
                weatherpic.setBackgroundResource(R.drawable.day10);break;
            case "http://appimg.showapi.com/images/weather/icon/day/11.png":
                weatherpic.setBackgroundResource(R.drawable.day11);break;
            case "http://appimg.showapi.com/images/weather/icon/day/12.png":
                weatherpic.setBackgroundResource(R.drawable.day12);break;
            case "http://appimg.showapi.com/images/weather/icon/day/13.png":
                weatherpic.setBackgroundResource(R.drawable.day13);break;
            case "http://appimg.showapi.com/images/weather/icon/day/14.png":
                weatherpic.setBackgroundResource(R.drawable.day14);break;
            case "http://appimg.showapi.com/images/weather/icon/day/15.png":
                weatherpic.setBackgroundResource(R.drawable.day15);break;
            case "http://appimg.showapi.com/images/weather/icon/day/16.png":
                weatherpic.setBackgroundResource(R.drawable.day16);break;
            case "http://appimg.showapi.com/images/weather/icon/day/17.png":
                weatherpic.setBackgroundResource(R.drawable.day17);break;
            case "http://appimg.showapi.com/images/weather/icon/day/18.png":
                weatherpic.setBackgroundResource(R.drawable.day18);break;
            case "http://appimg.showapi.com/images/weather/icon/day/19.png":
                weatherpic.setBackgroundResource(R.drawable.day19);break;
            case "http://appimg.showapi.com/images/weather/icon/day/20.png":
                weatherpic.setBackgroundResource(R.drawable.day20);break;
            case "http://appimg.showapi.com/images/weather/icon/day/21.png":
                weatherpic.setBackgroundResource(R.drawable.day21);break;
            case "http://appimg.showapi.com/images/weather/icon/day/22.png":
                weatherpic.setBackgroundResource(R.drawable.day22);break;
            case "http://appimg.showapi.com/images/weather/icon/day/23.png":
                weatherpic.setBackgroundResource(R.drawable.day23);break;
            case "http://appimg.showapi.com/images/weather/icon/day/24.png":
                weatherpic.setBackgroundResource(R.drawable.day24);break;
            case "http://appimg.showapi.com/images/weather/icon/day/25.png":
                weatherpic.setBackgroundResource(R.drawable.day25);break;
            case "http://appimg.showapi.com/images/weather/icon/day/26.png":
                weatherpic.setBackgroundResource(R.drawable.day26);break;
            case "http://appimg.showapi.com/images/weather/icon/day/27.png":
                weatherpic.setBackgroundResource(R.drawable.day27);break;
            case "http://appimg.showapi.com/images/weather/icon/day/28.png":
                weatherpic.setBackgroundResource(R.drawable.day28);break;
            case "http://appimg.showapi.com/images/weather/icon/day/29.png":
                weatherpic.setBackgroundResource(R.drawable.day29);break;
            case "http://appimg.showapi.com/images/weather/icon/day/30.png":
                weatherpic.setBackgroundResource(R.drawable.day30);break;
            case "http://appimg.showapi.com/images/weather/icon/day/31.png":
                weatherpic.setBackgroundResource(R.drawable.day31);break;
            case "http://appimg.showapi.com/images/weather/icon/day/53.png":
                weatherpic.setBackgroundResource(R.drawable.day53);break;
            case "http://appimg.showapi.com/images/weather/icon/night/00.png":
                weatherpic.setBackgroundResource(R.drawable.night00);break;
            case "http://appimg.showapi.com/images/weather/icon/night/01.png":
                weatherpic.setBackgroundResource(R.drawable.night01);break;
            case "http://appimg.showapi.com/images/weather/icon/night/02.png":
                weatherpic.setBackgroundResource(R.drawable.night02);break;
            case "http://appimg.showapi.com/images/weather/icon/night/03.png":
                weatherpic.setBackgroundResource(R.drawable.night03);break;
            case "http://appimg.showapi.com/images/weather/icon/night/04.png":
                weatherpic.setBackgroundResource(R.drawable.night04);break;
            case "http://appimg.showapi.com/images/weather/icon/night/05.png":
                weatherpic.setBackgroundResource(R.drawable.night05);break;
            case "http://appimg.showapi.com/images/weather/icon/night/06.png":
                weatherpic.setBackgroundResource(R.drawable.night06);break;
            case "http://appimg.showapi.com/images/weather/icon/night/07.png":
                weatherpic.setBackgroundResource(R.drawable.night07);break;
            case "http://appimg.showapi.com/images/weather/icon/night/08.png":
                weatherpic.setBackgroundResource(R.drawable.night08);break;
            case "http://appimg.showapi.com/images/weather/icon/night/09.png":
                weatherpic.setBackgroundResource(R.drawable.night09);break;
            case "http://appimg.showapi.com/images/weather/icon/night/10.png":
                weatherpic.setBackgroundResource(R.drawable.night10);break;
            case "http://appimg.showapi.com/images/weather/icon/night/11.png":
                weatherpic.setBackgroundResource(R.drawable.night11);break;
            case "http://appimg.showapi.com/images/weather/icon/night/12.png":
                weatherpic.setBackgroundResource(R.drawable.night12);break;
            case "http://appimg.showapi.com/images/weather/icon/night/13.png":
                weatherpic.setBackgroundResource(R.drawable.night13);break;
            case "http://appimg.showapi.com/images/weather/icon/night/14.png":
                weatherpic.setBackgroundResource(R.drawable.night14);break;
            case "http://appimg.showapi.com/images/weather/icon/night/15.png":
                weatherpic.setBackgroundResource(R.drawable.night15);break;
            case "http://appimg.showapi.com/images/weather/icon/night/16.png":
                weatherpic.setBackgroundResource(R.drawable.night16);break;
            case "http://appimg.showapi.com/images/weather/icon/night/17.png":
                weatherpic.setBackgroundResource(R.drawable.night17);break;
            case "http://appimg.showapi.com/images/weather/icon/night/18.png":
                weatherpic.setBackgroundResource(R.drawable.night18);break;
            case "http://appimg.showapi.com/images/weather/icon/night/19.png":
                weatherpic.setBackgroundResource(R.drawable.night19);break;
            case "http://appimg.showapi.com/images/weather/icon/night/20.png":
                weatherpic.setBackgroundResource(R.drawable.night20);break;
            case "http://appimg.showapi.com/images/weather/icon/night/21.png":
                weatherpic.setBackgroundResource(R.drawable.night21);break;
            case "http://appimg.showapi.com/images/weather/icon/night/22.png":
                weatherpic.setBackgroundResource(R.drawable.night22);break;
            case "http://appimg.showapi.com/images/weather/icon/night/23.png":
                weatherpic.setBackgroundResource(R.drawable.night23);break;
            case "http://appimg.showapi.com/images/weather/icon/night/24.png":
                weatherpic.setBackgroundResource(R.drawable.night24);break;
            case "http://appimg.showapi.com/images/weather/icon/night/25.png":
                weatherpic.setBackgroundResource(R.drawable.night25);break;
            case "http://appimg.showapi.com/images/weather/icon/night/26.png":
                weatherpic.setBackgroundResource(R.drawable.night26);break;
            case "http://appimg.showapi.com/images/weather/icon/night/27.png":
                weatherpic.setBackgroundResource(R.drawable.night27);break;
            case "http://appimg.showapi.com/images/weather/icon/night/28.png":
                weatherpic.setBackgroundResource(R.drawable.night28);break;
            case "http://appimg.showapi.com/images/weather/icon/night/29.png":
                weatherpic.setBackgroundResource(R.drawable.night29);break;
            case "http://appimg.showapi.com/images/weather/icon/night/30.png":
                weatherpic.setBackgroundResource(R.drawable.night30);break;
            case "http://appimg.showapi.com/images/weather/icon/night/31.png":
                weatherpic.setBackgroundResource(R.drawable.night31);break;
            case "http://appimg.showapi.com/images/weather/icon/night/53.png":
                weatherpic.setBackgroundResource(R.drawable.night53);break;
        }
    }

    /**
     * 根据网络获取的天气图片的地址，设置相应的天气图片
     * @param weather_pic_str
     * @param weatherpic
     */
    private void SetWeatherPicture2(String weather_pic_str,ImageView weatherpic) {
        switch (weather_pic_str) {
            case "http://app1.showapi.com/weather/icon/day/00.png":
                weatherpic.setBackgroundResource(R.drawable.day00);break;
            case "http://app1.showapi.com/weather/icon/day/01.png":
                weatherpic.setBackgroundResource(R.drawable.day01);break;
            case "http://app1.showapi.com/weather/icon/day/02.png":
                weatherpic.setBackgroundResource(R.drawable.day02);break;
            case "http://app1.showapi.com/weather/icon/day/03.png":
                weatherpic.setBackgroundResource(R.drawable.day03);break;
            case "http://app1.showapi.com/weather/icon/day/04.png":
                weatherpic.setBackgroundResource(R.drawable.day04);break;
            case "http://app1.showapi.com/weather/icon/day/05.png":
                weatherpic.setBackgroundResource(R.drawable.day05);break;
            case "http://app1.showapi.com/weather/icon/day/06.png":
                weatherpic.setBackgroundResource(R.drawable.day06);break;
            case "http://app1.showapi.com/weather/icon/day/07.png":
                weatherpic.setBackgroundResource(R.drawable.day07);break;
            case "http://app1.showapi.com/weather/icon/day/08.png":
                weatherpic.setBackgroundResource(R.drawable.day08);break;
            case "http://app1.showapi.com/weather/icon/day/09.png":
                weatherpic.setBackgroundResource(R.drawable.day09);break;
            case "http://app1.showapi.com/weather/icon/day/10.png":
                weatherpic.setBackgroundResource(R.drawable.day10);break;
            case "http://app1.showapi.com/weather/icon/day/11.png":
                weatherpic.setBackgroundResource(R.drawable.day11);break;
            case "http://app1.showapi.com/weather/icon/day/12.png":
                weatherpic.setBackgroundResource(R.drawable.day12);break;
            case "http://app1.showapi.com/weather/icon/day/13.png":
                weatherpic.setBackgroundResource(R.drawable.day13);break;
            case "http://app1.showapi.com/weather/icon/day/14.png":
                weatherpic.setBackgroundResource(R.drawable.day14);break;
            case "http://app1.showapi.com/weather/icon/day/15.png":
                weatherpic.setBackgroundResource(R.drawable.day15);break;
            case "http://app1.showapi.com/weather/icon/day/16.png":
                weatherpic.setBackgroundResource(R.drawable.day16);break;
            case "http://app1.showapi.com/weather/icon/day/17.png":
                weatherpic.setBackgroundResource(R.drawable.day17);break;
            case "http://app1.showapi.com/weather/icon/day/18.png":
                weatherpic.setBackgroundResource(R.drawable.day18);break;
            case "http://app1.showapi.com/weather/icon/day/19.png":
                weatherpic.setBackgroundResource(R.drawable.day19);break;
            case "http://app1.showapi.com/weather/icon/day/20.png":
                weatherpic.setBackgroundResource(R.drawable.day20);break;
            case "http://app1.showapi.com/weather/icon/day/21.png":
                weatherpic.setBackgroundResource(R.drawable.day21);break;
            case "http://app1.showapi.com/weather/icon/day/22.png":
                weatherpic.setBackgroundResource(R.drawable.day22);break;
            case "http://app1.showapi.com/weather/icon/day/23.png":
                weatherpic.setBackgroundResource(R.drawable.day23);break;
            case "http://app1.showapi.com/weather/icon/day/24.png":
                weatherpic.setBackgroundResource(R.drawable.day24);break;
            case "http://app1.showapi.com/weather/icon/day/25.png":
                weatherpic.setBackgroundResource(R.drawable.day25);break;
            case "http://app1.showapi.com/weather/icon/day/26.png":
                weatherpic.setBackgroundResource(R.drawable.day26);break;
            case "http://app1.showapi.com/weather/icon/day/27.png":
                weatherpic.setBackgroundResource(R.drawable.day27);break;
            case "http://app1.showapi.com/weather/icon/day/28.png":
                weatherpic.setBackgroundResource(R.drawable.day28);break;
            case "http://app1.showapi.com/weather/icon/day/29.png":
                weatherpic.setBackgroundResource(R.drawable.day29);break;
            case "http://app1.showapi.com/weather/icon/day/30.png":
                weatherpic.setBackgroundResource(R.drawable.day30);break;
            case "http://app1.showapi.com/weather/icon/day/31.png":
                weatherpic.setBackgroundResource(R.drawable.day31);break;
            case "http://app1.showapi.com/weather/icon/day/53.png":
                weatherpic.setBackgroundResource(R.drawable.day53);break;
            case "http://app1.showapi.com/weather/icon/night/00.png":
                weatherpic.setBackgroundResource(R.drawable.night00);break;
            case "http://app1.showapi.com/weather/icon/night/01.png":
                weatherpic.setBackgroundResource(R.drawable.night01);break;
            case "http://app1.showapi.com/weather/icon/night/02.png":
                weatherpic.setBackgroundResource(R.drawable.night02);break;
            case "http://app1.showapi.com/weather/icon/night/03.png":
                weatherpic.setBackgroundResource(R.drawable.night03);break;
            case "http://app1.showapi.com/weather/icon/night/04.png":
                weatherpic.setBackgroundResource(R.drawable.night04);break;
            case "http://app1.showapi.com/weather/icon/night/05.png":
                weatherpic.setBackgroundResource(R.drawable.night05);break;
            case "http://app1.showapi.com/weather/icon/night/06.png":
                weatherpic.setBackgroundResource(R.drawable.night06);break;
            case "http://app1.showapi.com/weather/icon/night/07.png":
                weatherpic.setBackgroundResource(R.drawable.night07);break;
            case "http://app1.showapi.com/weather/icon/night/08.png":
                weatherpic.setBackgroundResource(R.drawable.night08);break;
            case "http://app1.showapi.com/weather/icon/night/09.png":
                weatherpic.setBackgroundResource(R.drawable.night09);break;
            case "http://app1.showapi.com/weather/icon/night/10.png":
                weatherpic.setBackgroundResource(R.drawable.night10);break;
            case "http://app1.showapi.com/weather/icon/night/11.png":
                weatherpic.setBackgroundResource(R.drawable.night11);break;
            case "http://app1.showapi.com/weather/icon/night/12.png":
                weatherpic.setBackgroundResource(R.drawable.night12);break;
            case "http://app1.showapi.com/weather/icon/night/13.png":
                weatherpic.setBackgroundResource(R.drawable.night13);break;
            case "http://app1.showapi.com/weather/icon/night/14.png":
                weatherpic.setBackgroundResource(R.drawable.night14);break;
            case "http://app1.showapi.com/weather/icon/night/15.png":
                weatherpic.setBackgroundResource(R.drawable.night15);break;
            case "http://app1.showapi.com/weather/icon/night/16.png":
                weatherpic.setBackgroundResource(R.drawable.night16);break;
            case "http://app1.showapi.com/weather/icon/night/17.png":
                weatherpic.setBackgroundResource(R.drawable.night17);break;
            case "http://app1.showapi.com/weather/icon/night/18.png":
                weatherpic.setBackgroundResource(R.drawable.night18);break;
            case "http://app1.showapi.com/weather/icon/night/19.png":
                weatherpic.setBackgroundResource(R.drawable.night19);break;
            case "http://app1.showapi.com/weather/icon/night/20.png":
                weatherpic.setBackgroundResource(R.drawable.night20);break;
            case "http://app1.showapi.com/weather/icon/night/21.png":
                weatherpic.setBackgroundResource(R.drawable.night21);break;
            case "http://app1.showapi.com/weather/icon/night/22.png":
                weatherpic.setBackgroundResource(R.drawable.night22);break;
            case "http://app1.showapi.com/weather/icon/night/23.png":
                weatherpic.setBackgroundResource(R.drawable.night23);break;
            case "http://app1.showapi.com/weather/icon/night/24.png":
                weatherpic.setBackgroundResource(R.drawable.night24);break;
            case "http://app1.showapi.com/weather/icon/night/25.png":
                weatherpic.setBackgroundResource(R.drawable.night25);break;
            case "http://app1.showapi.com/weather/icon/night/26.png":
                weatherpic.setBackgroundResource(R.drawable.night26);break;
            case "http://app1.showapi.com/weather/icon/night/27.png":
                weatherpic.setBackgroundResource(R.drawable.night27);break;
            case "http://app1.showapi.com/weather/icon/night/28.png":
                weatherpic.setBackgroundResource(R.drawable.night28);break;
            case "http://app1.showapi.com/weather/icon/night/29.png":
                weatherpic.setBackgroundResource(R.drawable.night29);break;
            case "http://app1.showapi.com/weather/icon/night/30.png":
                weatherpic.setBackgroundResource(R.drawable.night30);break;
            case "http://app1.showapi.com/weather/icon/night/31.png":
                weatherpic.setBackgroundResource(R.drawable.night31);break;
            case "http://app1.showapi.com/weather/icon/night/53.png":
                weatherpic.setBackgroundResource(R.drawable.night53);break;
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        try {
            progressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * The notification is the icon and associated expanded entry in the
     * status bar.
     */
    protected void showNotification()
    {
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);


        Notification notification = new Notification();

        notification.icon = R.drawable.noticeimg;
        //notification.tickerText = "Custom!";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String noticeautopositioncity = prefs.getString("cityName", "");
        String noticetemperature = prefs.getString("now_temperature","");
        String noticeweather = prefs.getString("now_weather","");
        String noticpublictime = prefs.getString("now_temperature_time","");


        RemoteViews contentView = new RemoteViews(getActivity().getPackageName(), R.layout.notice);
        contentView.setImageViewResource(R.id.noticeimg, R.drawable.noticeimg);
        contentView.setImageViewResource(R.id.noticeautopositonImg, R.drawable.img_1);
        contentView.setTextViewText(R.id.noticecity, noticeautopositioncity);
        contentView.setTextViewText(R.id.notictemperature, noticetemperature+"℃");
        contentView.setTextViewText(R.id.noticeweather,noticeweather);
        contentView.setTextViewText(R.id.noticepublictime,"发布时间:"+noticpublictime);
        notification.contentView = contentView;
        manager.notify(1, notification);

    }
}
