package com.ddhigh.testlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ddhigh.mylibrary.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestParams params = new RequestParams();
        params.put("username","xialei");
        params.put("password","1111111");
        params.setUseJsonStreamer(true);
        HttpUtil.post("/user/login",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("1x1x1x",response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    JSONObject error = errorResponse.getJSONObject("error");
                    String msg = error.getString("message");
                    Log.e("1x1x1x",msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
