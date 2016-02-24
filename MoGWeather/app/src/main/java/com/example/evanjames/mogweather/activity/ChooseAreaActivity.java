package com.example.evanjames.mogweather.activity;

/**
 * Created by EvanJames on 2015/8/24.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evanjames.mogweather.R;
import com.example.evanjames.mogweather.db.FeatureWeatherDB;
import com.example.evanjames.mogweather.model.City;
import com.example.evanjames.mogweather.model.County;
import com.example.evanjames.mogweather.model.Province;
import com.example.evanjames.mogweather.util.HttpCallbackListener;
import com.example.evanjames.mogweather.util.HttpUtil;
import com.example.evanjames.mogweather.util.Utility;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by EvanJames on 2015/8/24.
 * 遍历省市县数据的活动
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private FeatureWeatherDB featureWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    // 选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    //是否从WeatherActivity中跳转过来
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        //已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到 WeatherActivity
        SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(this);

//        if (prefs.getBoolean("city_selected", false)&& !isFromWeatherActivity) {
//            Intent intent = new Intent(this, WeatherActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        featureWeatherDB = FeatureWeatherDB.getInstance(this);//获取FeatureWeatherDB实例
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }  else if (currentLevel == LEVEL_COUNTY) {
                    String countyName=countyList.get(position).getCountyName();
                    Intent intent = new Intent(ChooseAreaActivity.this,MainActivity.class);
                    intent.putExtra("cityname",countyName);
                    startActivity(intent);
                    Log.d("Test", countyName + "~~~~~~~~~~~~~~~~~~~~");

                    finish();

                    //两个参数分别表示进入的动画,退出的动画
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
        queryProvinces(); // 加载省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = featureWeatherDB.loadProvinces();//从数据库读取省级数据
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容,可以实现动态的刷新列表的功能
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");//从服务器上查询数据
        }
    }

    /**
     *  查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        cityList = featureWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            } adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        countyList = featureWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     *  根据传入的代号和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(final String code, final String type) {

        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        /*定了查询地址之后，接下来就调用 HttpUtil的 sendHttpRequest()方法来向服务器发送请求，
          响应的数据会回调到 onFinish()方法中，然后我们在这里去调用 Utility 的 handleProvincesResponse()方法来
          解析和处理服务器返回的数据， 并存储到数据库中*/
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(featureWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(featureWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(featureWeatherDB, response, selectedCity.getId());
                }

                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
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
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     *捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChooseAreaActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
