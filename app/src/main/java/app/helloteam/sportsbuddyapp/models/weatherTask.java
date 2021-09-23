package app.helloteam.sportsbuddyapp.models;

import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.getForecast;
import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.getTemp;
import static app.helloteam.sportsbuddyapp.views.LandingPageActivityKt.weatherAPI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.helloteam.sportsbuddyapp.views.LandingPageActivity;

public class weatherTask extends AsyncTask<String, Void, String> {

    public ImageView x;
    public weatherTask(ImageView im){
        x = im;
    }
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
            Log.i("weather", weather.toString());
            JSONObject sys = jsonObj.getJSONObject("sys");
            String city_name = jsonObj.getString("name");
            Log.i("weather", "hi2");

            String countryname = sys.getString("country");
            String temperature = main.getString("temp");
            String cast = weather.getString("description");
            String iconUrl = "http://openweathermap.org/img/w/" + weather.getString("icon") + ".png";


            getTemp().setText(temperature + "°C");
            getForecast().setText(cast.toUpperCase());

            Picasso.get().load(iconUrl).into(x);

        } catch (Exception e) {

        }
    }
}