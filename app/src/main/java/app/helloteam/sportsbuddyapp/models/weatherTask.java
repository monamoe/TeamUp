package app.helloteam.sportsbuddyapp.models;

import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.getForecast;
import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.getTemp;
import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.weatherAPI;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.helloteam.sportsbuddyapp.views.LandingPageActivity;

public class weatherTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q="+strings[0]+"&units=metric&appid=" + weatherAPI);
        return response;
    }


    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONObject main = jsonObj.getJSONObject("main");
            JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
            JSONObject sys = jsonObj.getJSONObject("sys");
            String city_name = jsonObj.getString("name");

            String countryname = sys.getString("country");
            Long updatedAt = jsonObj.getLong("dt");
            String updatedAtText = "Last Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
            String temperature = main.getString("temp");
            String cast = weather.getString("description");



            getTemp().setText(temperature + "°C");
            getForecast().setText(cast);

        } catch (Exception e) {

        }
    }
}