package com.FirstApp.myapplication;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button MainButton;
    private TextView temp;
    private TextView weather;
    private TextView advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        MainButton = findViewById(R.id.MainButton);
        temp = findViewById(R.id.temp);
        weather = findViewById(R.id.weather);
        advice = findViewById(R.id.advice);

        MainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.NoInput, Toast.LENGTH_LONG).show();
                else
                {
                    String city = user_field.getText().toString();
                    String key = "2220d536ab2b76278aa4e4bbbfc802a3";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + ",&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }
    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            temp.setText("Щя всё будет, не торопи");
            advice.setText("Щя всё будет, не торопи");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);
                temp.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp"));

                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                weather.setText("Погода: " + jsonArray.getJSONObject(0).getString("description"));

               if (jsonArray.getJSONObject(0).getString("main").equals("Rain"))
                   advice.setText("На улице дождь. Хм-м. Думаю, лучше остаться дома" );
               else if (jsonArray.getJSONObject(0).getString("main").equals("Clouds"))
                   advice.setText("Если где-то есть облака, значит может быть и дождь, так что, захвати зонтик" );
               else if (jsonArray.getJSONObject(0).getString("main").equals("Clear"))
                   advice.setText("На небе нет ни облачка. Хороший день, чтобы прогуляться" );
               else if (jsonArray.getJSONObject(0).getString("main").equals("Snow"))
                   advice.setText("Любишь снег? А мне без разницы, я же программа, лол. Хватай друзей и бегом лепить снеговиков" );
               else if (jsonArray.getJSONObject(0).getString("main").equals("Mist"))
                   advice.setText("Аккуратнее на дороге, туман - это не шутка" );

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}