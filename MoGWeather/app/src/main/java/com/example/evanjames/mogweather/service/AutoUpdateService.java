package com.example.evanjames.mogweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.evanjames.mogweather.receiver.AutoUpdateReceiver;
import com.example.evanjames.mogweather.util.HttpCallbackListener;
import com.example.evanjames.mogweather.util.HttpUtil;
import com.example.evanjames.mogweather.util.Utility;


/**
 * Created by EvanJames on 2015/8/24.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateWeather();
            }

        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 2 * 60 * 60 * 1000; // 这是2小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void UpdateWeather() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String cityname = pref.getString("cityName", "");
        queryWeatherInfoId(cityname);
        //setInformation();
    }

    /**
     * 根据城市名称查询对应的天气。
     */
    public void queryWeatherInfoId(String city_name) {
        //获取当前的时间
        java.util.Calendar rightNow = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sim = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String timedate = sim.format(rightNow.getTime());

        String address = "https://route.showapi.com/9-2?area="+city_name +
                "&areaid="+"&needHourData=0" +
                "&needIndex=1&needMoreDay=1" +
                "&showapi_appid=7530" +
                "&showapi_timestamp="+timedate +
                "&showapi_sign=29cd2588a4c04d3684a14bb5f1281539";

        mainQueryFromServer(address, 2);
    }

    /**
     * 根据接口地址下载相应的json数据，转化并存储数据。
     */
    public void mainQueryFromServer(final String address, final int i) {


        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

            @Override
            public void onFinish(final String response) {
                boolean result = false;
                // 处理服务器返回的天气信息
                result = Utility.handleWeatherResponse(AutoUpdateService.this, response, i);
            }

            @Override
            public void onError(Exception e) {
                    e.printStackTrace();
            }
        });
    }

}
