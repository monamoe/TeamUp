package app.helloteam.sportsbuddyapp.models;

import android.os.AsyncTask;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import app.helloteam.sportsbuddyapp.views.LandingPageKt;

public class weatherTask extends AsyncTask<String, Void, String> {

    private OkHttpClient client = new OkHttpClient();

    public weatherTask() {}

    @Override
    protected String doInBackground(String... params) {
        String lat = params[0];
        String lon = params[1];
        String appId = params[2];

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                "&lon=" + lon + "&units=metric&appid=" + appId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            Log.e("weatherTask", "Request failed", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Log.e("weatherTask", "No response received");
            return;
        }
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

            String temperature = main.getString("temp");
            String cast = weather.getString("description");
            String iconUrl = "http://openweathermap.org/img/w/" + weather.getString("icon") + ".png";

            LandingPageKt.setTemp(temperature + "°C");
            LandingPageKt.setForecast(cast.toUpperCase());
            LandingPageKt.setWeatherIcon(iconUrl);

        } catch (Exception e) {
            Log.e("weatherTask", "JSON parsing error", e);
        }
    }

    public static boolean weatherDone() {
        return !LandingPageKt.getTemp().isEmpty();
    }
}
