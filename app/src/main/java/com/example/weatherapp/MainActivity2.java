package com.example.weatherapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity2 extends AppCompatActivity {

    private TextView weatherTextView;
    private EditText cityEditText;
    private Button getWeatherButton;

    private static final String TAG = "MainActivity2";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "f7e493e1dee0dad637a80fcb18fff67a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setStatusBarColor("#0e0506");

        // Initialize views
        cityEditText = findViewById(R.id.cityEditText);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        weatherTextView = findViewById(R.id.weatherTextView);

        // Set click listener for get weather button
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    Toast.makeText(MainActivity2.this, "Voer een stad in", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setStatusBarColor(String colorHex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(colorHex));
        }
    }

    private void getWeatherData(String city) {
        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getWeather(city, API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    handleWeatherResponse(weatherResponse);
                } else {
                    handleWeatherError(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "Netwerkfout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Netwerkfout: ", t);
            }
        });
    }

    private void handleWeatherResponse(WeatherResponse weatherResponse) {
        int temperatureCelsius = (int) Math.round(weatherResponse.main.temp - 273.15);

        String weatherInfo = "Temperatuur: " + temperatureCelsius + "°C\n" +
                "Wind Snelheid: " + weatherResponse.wind.speed + " m/s\n" +
                "Wind Directie: " + convertDegreeToDirection(weatherResponse.wind.deg) + "\n" +
                "Regen: " + (weatherResponse.hasRain() ? "Ja" : "Nee") + "\n" +
                "Luchtvochtigheid: " + weatherResponse.main.humidity + "%";

        weatherTextView.setText(weatherInfo);
        weatherTextView.setVisibility(View.VISIBLE);
    }

    private void handleWeatherError(Response<WeatherResponse> response) {
        Toast.makeText(MainActivity2.this, "Fout bij het ophalen van de gegevens", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Response code: " + response.code());
        Log.e(TAG, "Response message: " + response.message());
        try {
            Log.e(TAG, "Response error body: " + response.errorBody().string());
        } catch (Exception e) {
            Log.e(TAG, "Fout bij het lezen van de foutboodschap", e);
        }
    }

    private String convertDegreeToDirection(float degree) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = Math.round(degree / 22.5f) % 16;
        return directions[index];
    }

    interface WeatherService {
        @GET("weather")
        Call<WeatherResponse> getWeather(@Query("q") String city, @Query("appid") String apiKey);
    }

    public static class WeatherResponse {
        Main main;
        Weather[] weather;
        Wind wind;

        public static class Main {
            float temp;
            float humidity;
        }

        public static class Weather {
            String main;
        }

        public static class Wind {
            float speed;
            float deg;
        }

        public boolean hasRain() {
            return weather != null && weather.length > 0 && weather[0].main.equals("Rain");
        }
    }
}
