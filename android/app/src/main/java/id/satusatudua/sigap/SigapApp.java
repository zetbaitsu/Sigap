package id.satusatudua.sigap;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Author     : Zetra - https://github.com/zetbaitsu
 */
public class SigapApp extends Application {

    private static SigapApp sigapApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sigapApp = this;
        Firebase.setAndroidContext(this);
    }

    public static SigapApp pluck() {
        return sigapApp;
    }
}
