package com.example.spacejude;
// sovellus seuraavien SpaceX laukaisujen hakuun
// @author ville nykänen
// @version 16.1.2018

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


//@author https://www.tutorialspoint.com/android/android_json_parser.htm & Ville Nykänen
// pääohjelma
public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private Handler handler;
    private Runnable runnable;
    private TextView lTimer;
    private TextView lDate;
    private TextView lName;
    private TextView lRocket;

    HashMap launchInfoList;      // tänne tallennetaan jsonista parisut tiedot




    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchInfoList = new HashMap();
        lTimer = (TextView) findViewById(R.id.timer);
        lDate = (TextView) findViewById(R.id.launchDate);
        lName = (TextView) findViewById(R.id.missionName);
        lRocket = (TextView) findViewById(R.id.rocketName);

        // kutsutaan aliohjlemaa pyttämään lataamaan json
        new GetInfo().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.infoButton:
                Intent intent1 = new Intent(this, infoMenu.class);
                this.startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void luoAjastin(final Date futureDate) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);

                try {
                   // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                   // Date futureDate = dateFormat.parse("2019-5-30");
                    // Please here set your event date//YYYY-MM-DD
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        lTimer.setText(String.format("%02d", days) + " päivää " + String.format("%02d", hours) + " tuntia " + String.format("%02d", minutes) +  " minuuttia " + String.format("%02d", seconds) + " sekuntia");
                    } else {
                        lTimer.setText("Laukaistu");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    // Haetaan data
    private class GetInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Ladataan tietoja",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();             // uusi httphandler jolla ladataan tiedot
            // Making a request to url and getting response
            String url = "https://api.spacexdata.com/v2/launches/next";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject nextLaunch = new JSONObject(jsonStr);
                    String mission_name = nextLaunch.getString("mission_name");

                    int date = nextLaunch.getInt("launch_date_unix");
                    final Date utcdate = new java.util.Date(date*1000L);
                    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                    jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                    String java_date = jdf.format(utcdate);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                luoAjastin(utcdate);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    JSONObject rocket = nextLaunch.getJSONObject("rocket");
                    String rocket_name = rocket.getString("rocket_name");

                    HashMap<String, String> launch = new HashMap<>();
                    launchInfoList.put("missionName", mission_name);
                    launchInfoList.put("rocketName", rocket_name);
                    launchInfoList.put("launchDate", java_date);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "JSONia ei saatu noudettua.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Tietoja ei saatu noudettua!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        // asetetaan tiedot näkymään
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            lName.setText((String)launchInfoList.get("missionName"));
            lRocket.setText((String)launchInfoList.get("rocketName"));
            lDate.setText((String)launchInfoList.get("launchDate"));

        }
    }
}