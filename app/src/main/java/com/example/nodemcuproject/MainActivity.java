package com.example.nodemcuproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class MainActivity extends AppCompatActivity {

    private TextView feedback;
    private Button send;
    private EditText ip;
    private Switch light;
    private EditText message;
    public int counter = 0;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedback = (TextView) findViewById(R.id.feedback_lbl);
        send = (Button) findViewById(R.id.send_btn);
        ip = (EditText) findViewById(R.id.ip_txt);
        light = (Switch) findViewById(R.id.led_switch);
        message = (EditText) findViewById(R.id.message_txt);

        mPrefs = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("text", ip.getText().toString());
        editor.apply();

        light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String url;
                if(isChecked) {
                    // switch is turned on
                    url = "http://" + ip.getText().toString() +"/on";
                    GetRequest(url);
                } else {
                    // switch is turned off
                    url = "http://" + ip.getText().toString() +"/off";
                    GetRequest(url);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // feedback.setText(Integer.toString(counter));
                String url = "http://" + ip.getText().toString() +"/" + message.getText().toString();
                GetRequest(url);
                // counter += 1;
            }
        });

        // find esp IP address

        /*EspDiscovery espDiscovery = new EspDiscovery();
        Context context = getApplicationContext();
        String serviceType = "_http._tcp";
        espDiscovery.discoverEsp(context, serviceType);
        feedback.setText("IP: " + espDiscovery.espIp);*/


    }
    @Override
    protected void onPause() {
        super.onPause();
        EditText textBox = findViewById(R.id.ip_txt);
        String text = textBox.getText().toString();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("text", text);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText textBox = findViewById(R.id.ip_txt);
        String text = mPrefs.getString("text", "");
        textBox.setText(text);
    }

    private void GetRequest(String url){

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                feedback.setText("ERROR OCCURED");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if(response.isSuccessful()){
                        String myResponse = response.body().string();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                feedback.setText(myResponse);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    feedback.setText("ERROR OCCURED");
                }
            }
        });

    }
}