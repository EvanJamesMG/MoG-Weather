package com.example.evanjames.mogweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;

import java.util.List;

/**
 * Created by EvanJames on 2015/9/4.
 */
public class MyLocationListener extends Activity implements BDLocationListener {

    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");
            sb.append("\n所在省 : ");
            sb.append(location.getProvince());
            sb.append("\n所在城市 : ");
            sb.append(location.getCity());
            sb.append("\n所在城市的代码 : ");
            sb.append(location.getCityCode());
            sb.append("\n所在区县 : ");
            sb.append(location.getDistrict());


            double tem1 = location.getLatitude();
            double tem2 = location.getLongitude();
            String Latitude = Double.toString(location.getLatitude());
            String Longitude = Double.toString(location.getLongitude());
            Log.d("Test", Latitude + ": 开机定位!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            Log.d("Test", Longitude + ": 开机定位!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");


            //把得到的经纬度存储起来
            SharedPreferences.Editor editor1 = getSharedPreferences("CurrentPosition", MODE_PRIVATE).edit();
            editor1.putString("Latitude", Latitude);
            editor1.putString("Longitude", Longitude);
            editor1.apply();

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.i("BaiduLocationApiDem", sb.toString());
    }
}