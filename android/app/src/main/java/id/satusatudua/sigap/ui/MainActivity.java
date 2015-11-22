package id.satusatudua.sigap.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> addUser());
    }

    private void addUser() {
        User user = new User.Builder()
                .setUsername("RyMey")
                .setPassword("SEMANGAT")
                .setName("Rya Meyvriska")
                .setBirthday("24051994")
                .setGender("F")
                .setStatus("Online")
                .setLocation(new Location(142, 20))
                .build();

        FirebaseApi.pluck().getApi().child("user").child(user.getUsername()).setValue(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
