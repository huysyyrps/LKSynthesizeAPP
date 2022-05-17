package com.example.lksynthesizeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        imageView = findViewById(R.id.imageView);
        //新建一个OkHttpClient对象
        String url = "http://192.168.43.104:8080?action=snapshot";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()	//默认就是GET请求，可以省略
                .build();
        Call call = okHttpClient.newCall(request);
        while (true){
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "onFailure: ");
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //response.body().string() 获得服务器返回的数据
//                Log.e("TAG", "onResponse: " + response.body().string());
                    byte[] picture = response.body().bytes();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picture,0,picture.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            });
            call.cancel();
            call.clone();
        }
    }
}