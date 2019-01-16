package com.example.spacejude;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

// info-menu luokka
public class infoMenu extends AppCompatActivity{
    private TextView about;
    private TextView version;
    private TextView api;

    // Näyttää tietoja sovelluksesta.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_about_app);

        about = (TextView) findViewById(R.id.about);                                    // haetaan tekstikentät ja täytetään ne
        about.setText(getString(R.string.about));    // haetaan stringit strings.xml tiedostosta

        String linkki = "https://github.com/r-spacex/SpaceX-API";                       // luodaan linkki
        api = (TextView) findViewById(R.id.api);
        api.setText(Html.fromHtml("<a href=\""+ linkki + "\">" + "/r/ SpaceX API" + "</a>"));
        api.setClickable(true);
        api.setMovementMethod (LinkMovementMethod.getInstance());

        version = (TextView) findViewById(R.id.version);                                // versionumero
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        version.setText("Versio: " + versionName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
