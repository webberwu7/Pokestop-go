package com.example.webberwu.pokestop_go;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button buttonsss;
    private TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        new RunWork().start();
    }
    private void findViews(){
        buttonsss = (Button) findViewById(R.id.button1);
        textview = (TextView) findViewById(R.id.textview1);
    }


    /*上網抓資料，需要另外開執行緒做處理(Android機制)*/
    class RunWork extends Thread
    {
        String path_json ="http://nyapass.gear.host/";
        String result_json = null;

        /* This program downloads a URL and print its contents as a string.*/
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("query_string","SELECT * FROM `TABLE 1` LIMIT 20")
                .build();
        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        Runnable task = new Runnable()
        {
            @Override
            public void run() {
                //使用 gson 解析 json 資料
                Gson gson = new Gson();
                PokeStop[] pokestops = gson.fromJson(result_json,PokeStop[].class);

                StringBuilder sb = new StringBuilder();
                for(PokeStop pokestop :pokestops){
                    sb.append("PokeStopID:").append(pokestop.getStopID()).append(" ")
                            .append("經度:").append(pokestop.getLat()).append(" ")
                            .append("緯度:").append(pokestop.getLng()).append("\n\n");
                }
                textview.setText(sb);

            }
        };

        @Override
        public void run()
        {
            try {
                //1.抓資料
                result_json = run(path_json);
                //2.改變畫面內容只能用主執行緒(Android機制)
                runOnUiThread(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
