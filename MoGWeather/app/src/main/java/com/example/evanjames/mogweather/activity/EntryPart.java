package com.example.evanjames.mogweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.evanjames.mogweather.R;

/**
 * Created by EvanJames on 2015/9/7.
 */
public class EntryPart extends Activity {
    private static final int SHOW_TIME_MIN = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrypart);

        new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... params) {

                long startTime = System.currentTimeMillis();
                //result = loadingCache();
                long loadingTime = System.currentTimeMillis() - startTime;
                if (loadingTime < SHOW_TIME_MIN) {
                    try {
                        Thread.sleep(SHOW_TIME_MIN - loadingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                Intent intent = new Intent(EntryPart.this, MainActivity.class);
                startActivity(intent);
                finish();

                //两个参数分别表示进入的动画,退出的动画
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }


        }.execute(new Void[]{});
    }
}
