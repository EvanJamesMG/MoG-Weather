package com.example.evanjames.mogweather.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.example.evanjames.mogweather.R;
import com.example.evanjames.mogweather.db.CityListDatabaseHelper;
import com.example.evanjames.mogweather.service.AutoUpdateService;
import com.example.evanjames.mogweather.util.Utility;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements SensorEventListener {

    private CityListDatabaseHelper dbHelper;
    private String listcityname;
    private TextView cityNameText;
    private Button addCityBtn;
    private Button leftmenuBtn;
    private DrawerLayout mDrawerLayout;
    private static boolean AutoPositionLabel;
    private static boolean AutoUpdateLabel;
    private static boolean NoticLabel;
    private static boolean changebackgroundlabel;

    private String Latitude;
    private String Longitude;
    private static LocationClient mLocationClient = null;
    private int CityNums = 0;//列表中显示的添加的城市的个数
    private ListView listview;
    private MyAdapter adapter;
    private ProgressDialog progressDialog;
    private boolean OpenLabelone;
    private boolean OpenLabeltwo;
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
    private static RequestQueue mQueue;
    private Button autopositionopenBtn;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private SensorManager mSensorManager;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private RelativeLayout mainbackimg;
    private static  int changenum = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);



        mQueue = Volley.newRequestQueue(this);
        initView();  //抽屉菜单的初始化
        OpenLabelone = true;
        OpenLabeltwo = true;

        changeSwitchButton();//滑动开关的控制

        OpenLabelone = false;
        OpenLabeltwo = false;


        mainbackimg = (RelativeLayout) findViewById(R.id.mainbackimg_id);

        cityNameText = (TextView) findViewById(R.id.city_name);
        autopositionText = (TextView) findViewById(R.id.autopositonText);//自动定位的城市
        addCityBtn = (Button) findViewById(R.id.addcitybtn);
        autopositionopenBtn = (Button) findViewById(R.id.autopositionOpenBtn);

        nowweatherpic_set = (ImageView)findViewById(R.id.weatherpicture);
        nowtemperature_set = (TextView)findViewById(R.id.temperature);
        nowweather_set = (TextView)findViewById(R.id.weatherdes);
        pm2_5_set = (TextView)findViewById(R.id.PM2_5_one);
        weekday1_set = (TextView)findViewById(R.id.week1);
        weekday2_set = (TextView)findViewById(R.id.week2);
        weekday3_set = (TextView)findViewById(R.id.week3);
        weekday4_set = (TextView)findViewById(R.id.week4);
        weekday5_set = (TextView)findViewById(R.id.week5);
        weekday1_weatherpic_set = (ImageView)findViewById(R.id.weekweatherimg1);
        weekday2_weatherpic_set = (ImageView)findViewById(R.id.weekweatherimg2);
        weekday3_weatherpic_set = (ImageView)findViewById(R.id.weekweatherimg3);
        weekday4_weatherpic_set = (ImageView)findViewById(R.id.weekweatherimg4);
        weekday5_weatherpic_set = (ImageView)findViewById(R.id.weekweatherimg5);
        weekday1_temperature_set = (TextView)findViewById(R.id.weektemperature1);
        weekday2_temperature_set = (TextView)findViewById(R.id.weektemperature2);
        weekday3_temperature_set = (TextView)findViewById(R.id.weektemperature3);
        weekday4_temperature_set = (TextView)findViewById(R.id.weektemperature4);
        weekday5_temperature_set = (TextView)findViewById(R.id.weektemperature5);


        jiangyu_set = (TextView)findViewById(R.id.jiangyu);
        sd_set = (TextView) findViewById(R.id.sd);
        wind_direction_set = (TextView) findViewById(R.id.wind_direction);
        wind_power_set = (TextView) findViewById(R.id.wind_power);
        uv_set = (TextView) findViewById(R.id.uv);
        sun_begin_end_set = (TextView) findViewById(R.id.sun_begin_end);

        co_set = (TextView) findViewById(R.id.co_id);
        no2_set = (TextView) findViewById(R.id.NO2_id);
        o3_set = (TextView) findViewById(R.id.O3_id);
        pm10_set = (TextView) findViewById(R.id.PM10_id);
        pm2_5_two_set = (TextView) findViewById(R.id.PM2_5_id);
        so2_set_set = (TextView) findViewById(R.id.SO2_id);
        primary_pollutant_set = (TextView) findViewById(R.id.primary_pollutant_id);
        quality_set = (TextView) findViewById(R.id.quality_id);

        clothes_set = (TextView) findViewById(R.id.clothes_des_id);
        sports_set = (TextView)findViewById(R.id.sports_des_id);
        travel_set = (TextView) findViewById(R.id.travel_des_id);
        washcar_set = (TextView) findViewById(R.id.washcar_des_id);


        listview = (ListView) findViewById(R.id.CityListView);
        adapter = new MyAdapter(this);
        listview.setAdapter(adapter);

        //为自动定位城市打开按钮设置监听
        autopositionopenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从存储的数据中恢复出自动定位的城市的名称
                SharedPreferences prefs = getSharedPreferences("AutoPositionCity", MODE_PRIVATE);
                listcityname = prefs.getString("autocity","");
                queryWeatherInfoId(listcityname);
            }
        });

        //为添加城市按钮设置监听
        addCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
                startActivity(intent);
                finish();

                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //finish();
            }
        });

        dbHelper = new CityListDatabaseHelper(this, "CityListDB.db", null, 1);
        dbHelper.getWritableDatabase();

        //从数据库中恢复已经添加的城市列表中的信息
        SQLiteDatabase Listdb = dbHelper.getWritableDatabase();
        Cursor cursor=Listdb.query("CityList", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String cityname = cursor.getString(cursor. getColumnIndex("cityname"));
                if(cityname!=null&&cityname.length()!=0) {
                    adapter.arr.add(cityname);
                    CityNums++;
                }
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();

        //从存储的数据中恢复出自动定位的城市的名称
        SharedPreferences prefs = getSharedPreferences("AutoPositionCity", MODE_PRIVATE);
        listcityname = prefs.getString("autocity","");
        autopositionText.setText("目前位置 "+listcityname);


        Intent mainintent = getIntent();
        listcityname=mainintent.getStringExtra("cityname");

        //首先从数据库中恢复数据
        SharedPreferences pref = getSharedPreferences("AppSetting",MODE_PRIVATE);
        if(pref != null) {
            AutoPositionLabel = pref.getBoolean("AutoPositionLabel", true);
            AutoUpdateLabel = pref.getBoolean("AutoUpdateLabel", true);
            NoticLabel = pref.getBoolean("NoticLabel", true);
            changebackgroundlabel = pref.getBoolean("changebackgroundlabel", true);

        }

        //cityname 若为空表示是开机初始化，不为空表示从选择城市的界面跳转过来的
        if(listcityname==null||listcityname.length()==0) {

            if(AutoPositionLabel) {
                AutoPositionStart();

            } else {
                //自动定位关闭 而且列表中没有其他城市时，跳转到添加城市界面
                if (adapter.arr.isEmpty()) {
                    //InitViewPager(); //直接加载空白视图
                    // Toast.makeText(this,"自动定位已关闭，请手动添加城市",Toast.LENGTH_SHORT);
                    Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);//或者直接跳转到添加城市界面
                    startActivity(intent);
                    finish();
                }else {
                    //自动定位关闭 城市列表不为空的时候,跳转到城市列表中的第一个上面
                    String temname = adapter.arr.get(0);
                    queryWeatherInfoId(temname);
                }
            }

        } else{
            cursor = Listdb.query("CityList", null, "cityname = ?",
                    new String[] { listcityname }, null, null, null);
            //此时表明已经添加的城市里面没有，这才添加进去
            if(cursor.getCount()==0) {
                ContentValues values = new ContentValues();
                values.put("cityname", listcityname);
                Listdb.insert("CityList", null, values); // 插入第一条数据
                values.clear();
                adapter.arr.add(listcityname);
                adapter.notifyDataSetChanged();
                SharedPreferences.Editor editor1 = PreferenceManager.getDefaultSharedPreferences(this).edit();//若原来有缓存将原来获取的城市位置获取出来
                editor1.putString("cityName", listcityname);
                editor1.commit();

            }else {
                Toast.makeText(this,"城市已存在",Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            cityNameText.setText(listcityname);
            SharedPreferences.Editor editor2 = getSharedPreferences("cityselectedlabel",MODE_PRIVATE).edit();
            editor2.putBoolean("openselected", true);
            editor2.apply();
            queryWeatherInfoId(listcityname);
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //当传感器精度改变时回调该方法，Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER)
        {
            if ((Math.abs(values[0]) > 13 || Math.abs(values[1]) > 13 || Math
                    .abs(values[2]) > 13))
            {
                Log.d("sensor x ", "============ values[0] = " + values[0]);
                Log.d("sensor y ", "============ values[1] = " + values[1]);
                Log.d("sensor z ", "============ values[2] = " + values[2]);

                if(changebackgroundlabel) {
                    randomchangebackground(mainbackimg, ++changenum);
                    //摇动手机后，再伴随震动提示~~
                    vibrator.vibrate(200);
                }
            }

        }
    }

    public void randomchangebackground(RelativeLayout mainbackimg,int nums){

        int randomnum = nums%8;//生成1-6的随机数
        switch (randomnum){

            case 0:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_1);
                break;
            case 1:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_2);
                break;
            case 2:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_3);
                break;
            case 3:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_4);
                break;
            case 4:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_5);
                break;
            case 5:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_6);
                break;
            case 6:
                mainbackimg.setBackgroundResource(R.drawable.mainbackimg_7);
                break;
            case 7:
                mainbackimg.setBackgroundResource(R.drawable.mainback);
                break;
            default:
                break;
        }



    }



    /**
     * 左菜单的监听
     * @param view
     */
    public void OpenLeftMenu(View view)
    {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
                Gravity.LEFT);
    }

    /**
     * 初始化抽出菜单
     */
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);
        mDrawerLayout.setScrimColor(00000000);//滑动时不改变透明度
        //setDrawerLeftEdgeSize(this, mDrawerLayout, 0.5f);//设置滑动距离打开抽屉
    }

    /**
     * 开始自动定位并将自动定位信息传递为网络接口，找到json数据并解析保存
     */
    private void AutoPositionStart() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
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
                Latitude = Double.toString(location.getLatitude());
                Longitude = Double.toString(location.getLongitude());
                Log.d("Test", Latitude + ": 开机定位!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                Log.d("Test", Longitude + ": 开机定位!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                //根据获取的经纬度获得json数据解析并保存
                queryWeatherInfo(Latitude, Longitude);


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
                    Toast.makeText(getApplicationContext(), "请检查是否连接网络", Toast.LENGTH_LONG).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    Toast.makeText(getApplicationContext(), "请检查是否连接网络", Toast.LENGTH_LONG).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    Toast.makeText(getApplicationContext(), "请检查是否连接网络", Toast.LENGTH_LONG).show();
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
        });    //注册监听函数

        initLocation();
        mLocationClient.start();
    }


    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=10;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

    }

    /**
     * 根据经纬度查询对应的天气。
     */
    public void queryWeatherInfo(String Latitude,String Longitude) {


        //获取当前的时间
        java.util.Calendar rightNow = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sim = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String timedate = sim.format(rightNow.getTime());

        String str="from5lat"+Latitude+"lng"+Longitude+"needHourData1needIndex1needMoreDay1showapi_appid"+10519+"showapi_timestamp"+timedate;
        str=str+"4f5bc34c418f435b988aee6ed0b875f6";

        String sign= null;
        sign = new String(Hex.encodeHex(DigestUtils.md5(str)));


        String address = "https://route.showapi.com/9-5?from=5&lat="+Latitude+"&lng=" +Longitude+
                "&needHourData=1&needIndex=1&needMoreDay=1" +
                "&showapi_appid=10519" +
                "&showapi_timestamp="+timedate+
                "&showapi_sign="+sign;
        mainQueryFromServer(address, 1);
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
                        result = Utility.handleWeatherResponse(MainActivity.this, response.toString(), i);
                        if (result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeProgressDialog();
                                    showWeather(i);
                                    try {
                                        if (NoticLabel) {
                                            showNotification();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
        });
        mQueue.add(jsonObjectRequest);

    }


    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather(int i) {

        SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


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
        if (i == 1) {
            SharedPreferences prefs = getSharedPreferences("AutoPositionCity", MODE_PRIVATE);
            listcityname = prefs.getString("autocity", "");
            cityNameText.setText(listcityname);
            autopositionText.setText("目前位置 "+listcityname);

            //自动定位打开的时候，开机自动在第一项添加本地定位城市
            SQLiteDatabase Listdb = dbHelper.getWritableDatabase();

            Cursor cursor = Listdb.query("CityList", null, "cityname = ?",
                    new String[] { listcityname }, null, null, null);
            //此时表明已经添加的城市里面没有，这才添加进去
            if(cursor.getCount()==0) {
                if(listcityname!=null&&listcityname.length()!=0) {
                    ContentValues values = new ContentValues();
                    values.put("cityname", listcityname);
                    Listdb.insert("CityList", null, values); // 插入第一条数据
                    values.clear();
                    adapter.arr.add(listcityname);
                    adapter.notifyDataSetChanged();
                }
            }else {
                //Toast.makeText(this,"定位城市已存在",Toast.LENGTH_SHORT).show();
            }
            cursor.close();

        } else {
            cityNameText.setText(cityname);
        }

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
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
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
     * 城市列表listview监听器
     */
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        public ArrayList<String> arr;
        public MyAdapter(Context context) {
            super();
            this.context = context;
            inflater = LayoutInflater.from(context);
            arr = new ArrayList<String>();

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arr.size();
        }
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        @Override
        public View getView(final int position, View view, ViewGroup arg2) {
            // TODO Auto-generated method stub
            if(view == null){
                view = inflater.inflate(R.layout.swipemenulistviewson, null);
            }
            final String listcityname = arr.get(position).toString();

            final TextView open = (Button) view.findViewById(R.id.openbutton);

            final TextView del = (Button) view.findViewById(R.id.deletbutton);

            ImageView img = (ImageView) view.findViewById(R.id.iv_icon);

            TextView listcitynametext = (TextView) view.findViewById(R.id.findcity_name);

            listcitynametext.setText(listcityname);

            open.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        //设置按钮按下之后的阴影效果
                        open.setTextColor(Color.parseColor("#3F51B5"));
                        open.setShadowLayer(10, 3, 3, Color.parseColor("#ff000000"));


                        //cityNameText.setText(listcityname);
                        //mDrawerLayout.closeDrawers();

                        queryWeatherInfoId(listcityname);


                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        //改为抬起时的图片
                        open.setTextColor(Color.parseColor("#FFFFFF"));
                        open.setShadowLayer(10, 10, 10, Color.parseColor("#ff000000"));
                        adapter.notifyDataSetChanged();

                    }
                    return false;
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub


                    SQLiteDatabase Listdb = dbHelper.getWritableDatabase();
                    Listdb.delete("CityList", "cityname = ?", new String[]{listcityname});
                    arr.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            return view;
        }
    }
    /**
     * 四个滑动开关的逻辑
     */
    private void changeSwitchButton() {

        final SwitchView viewSwitch1 = (SwitchView) findViewById(R.id.view_switch1);
        final SwitchView viewSwitch2 = (SwitchView) findViewById(R.id.view_switch2);
        final SwitchView viewSwitch3 = (SwitchView) findViewById(R.id.view_switch3);
        final SwitchView viewSwitch4 = (SwitchView) findViewById(R.id.view_switch4);

        //首先从数据库中恢复数据
        SharedPreferences pref = getSharedPreferences("AppSetting",MODE_PRIVATE);
        if(pref != null) {
            AutoPositionLabel = pref.getBoolean("AutoPositionLabel", true);
            AutoUpdateLabel = pref.getBoolean("AutoUpdateLabel", true);
            NoticLabel = pref.getBoolean("NoticLabel", true);
            changebackgroundlabel = pref.getBoolean("changebackgroundlabel",true);
        }
        if(AutoPositionLabel) {
            viewSwitch1.setState(true); // 设置初始状态。true为开;false为关[默认]。
        }else{
            viewSwitch1.setState(false);
        }
        if(AutoUpdateLabel) {
            viewSwitch2.setState(true);
        }else{
            viewSwitch2.setState(false);
        }
        if(NoticLabel){
            viewSwitch4.setState(true);
        }else{
            viewSwitch4.setState(false);
        }
        if(changebackgroundlabel){
            viewSwitch3.setState(true);
        }else{
            viewSwitch3.setState(false);
        }
        //viewSwitch3.setState(true);

        final SharedPreferences.Editor editor = getSharedPreferences("AppSetting",MODE_PRIVATE).edit();

        viewSwitch1.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                // 原本为关闭的状态，被点击后
                // 执行一些耗时的业务逻辑操作
                viewSwitch1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewSwitch1.toggleSwitch(true); //以动画效果切换到打开的状态
                    }
                }, 100);
                AutoPositionLabel = true;//自动定位被打开
                editor.putBoolean("AutoPositionLabel",AutoPositionLabel);
                editor.apply();

                if (!OpenLabelone) {
                    OpenLabelone = false;//避免开机键位初始化时执行逻辑操作
                    Log.d("Test", "自动定位打开了~~~~~~~~~~~~~~~");

                    //下面是自动定位并查找城市，并在相应的城市上显示显示天气
                    AutoPositionStart();

                }
            }

            @Override
            public void toggleToOff() {
                // 原本为打开的状态，被点击后
                viewSwitch1.toggleSwitch(false);
                AutoPositionLabel = false;//自动定位被关闭
                editor.putBoolean("AutoPositionLabel",AutoPositionLabel);
                editor.apply();
                if (!OpenLabeltwo) {
                    OpenLabeltwo = false;
                    Log.d("Test", "自动定位关闭了啊！！！！！！！！");
                    //删除此时显示的城市
                    SQLiteDatabase Listdb = dbHelper.getWritableDatabase();
                    SharedPreferences pref = getSharedPreferences("AutoPositionCity",MODE_PRIVATE);
                    String cityname = pref.getString("autocity", "");
                    Listdb.delete("CityList", "cityname = ?", new String[]{cityname});

                    if(cityname!=null&&cityname.length()!=0) {
                        try {
                            adapter.arr.remove(adapter.arr.indexOf(cityname));
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                            //Toast
                        }
                    }
                    //若此时的城市列表中没有其他的城市，就跳转到添加城市界面
                    if (CityNums == 0) {
                        Intent intent  = new Intent(MainActivity.this,ChooseAreaActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        viewSwitch2.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                // 原本为关闭的状态，被点击后
                // 执行一些耗时的业务逻辑操作
                AutoUpdateLabel =true;
                editor.putBoolean("AutoUpdateLabel",AutoUpdateLabel);
                editor.apply();
                viewSwitch2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewSwitch2.toggleSwitch(true); //以动画效果切换到打开的状态
                    }},100);
            }

            @Override
            public void toggleToOff() {
                // 原本为打开的状态，被点击后
                AutoUpdateLabel = false;
                editor.putBoolean("AutoUpdateLabel",AutoUpdateLabel);
                editor.apply();
                viewSwitch2.toggleSwitch(false);
            }
        });
        viewSwitch3.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                // 原本为关闭的状态，被点击后
                // 执行一些耗时的业务逻辑操作
                changebackgroundlabel =true;
                editor.putBoolean("changebackgroundlabel",changebackgroundlabel);
                editor.apply();
                viewSwitch3.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewSwitch3.toggleSwitch(true); //以动画效果切换到打开的状态
                    }},100);
            }

            @Override
            public void toggleToOff() {
                // 原本为打开的状态，被点击后
                changebackgroundlabel = false;
                editor.putBoolean("changebackgroundlabel",changebackgroundlabel);
                editor.apply();
                viewSwitch3.toggleSwitch(false);
            }
        });
        viewSwitch4.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                // 原本为关闭的状态，被点击后
                // 执行一些耗时的业务逻辑操作
                NoticLabel = true;
                editor.putBoolean("NoticLabel",NoticLabel);
                editor.apply();

                showNotification();

                viewSwitch4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewSwitch4.toggleSwitch(true); //以动画效果切换到打开的状态
                    }},100);

            }

            @Override
            public void toggleToOff() {
                // 原本为打开的状态，被点击后
                NoticLabel = false;
                editor.putBoolean("NoticLabel", NoticLabel);
                editor.apply();

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1);

                viewSwitch4.toggleSwitch(false);

            }
        });
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Notification notification = new Notification();

        notification.icon = R.drawable.noticeimg;
        //notification.tickerText = "Custom!";

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String noticeautopositioncity = prefs.getString("cityName", "");
        String noticetemperature = prefs.getString("now_temperature","");
        String noticeweather = prefs.getString("now_weather","");
        String noticpublictime = prefs.getString("now_temperature_time","");


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notice);
        contentView.setImageViewResource(R.id.noticeimg, R.drawable.noticeimg);
        contentView.setImageViewResource(R.id.noticeautopositonImg, R.drawable.img_1);
        contentView.setTextViewText(R.id.noticecity, noticeautopositioncity);
        contentView.setTextViewText(R.id.notictemperature, noticetemperature+"℃");
        contentView.setTextViewText(R.id.noticeweather,noticeweather);
        contentView.setTextViewText(R.id.noticepublictime,"发布时间:"+noticpublictime);
        notification.contentView = contentView;
        manager.notify(1, notification);

    }

    /**
     *捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认退出?"); //设置内容
        builder.setIcon(R.drawable.quiteasyicon);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();

    }


}

