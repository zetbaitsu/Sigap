package id.satusatudua.sigap;

import android.app.Application;

import com.firebase.client.Firebase;

import timber.log.Timber;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */

public class SigapApp extends Application {

    private static SigapApp sigapApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sigapApp = this;
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.tag(getClass().getSimpleName());
        }
    }

    public static SigapApp pluck() {
        return sigapApp;
    }
}
