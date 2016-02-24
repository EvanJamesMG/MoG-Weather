package com.example.evanjames.mogweather.util;

public interface HttpCallbackListener {

	void onFinish(String response);

	void onError(Exception e);

}
