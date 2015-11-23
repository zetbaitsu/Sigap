package id.satusatudua.sigap.data.api;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */

public enum FirebaseApi {

    INSTANCE;
    private Firebase firebase;
    private GeoFire geoFire;

    FirebaseApi() {
        firebase = new Firebase("https://sigap.firebaseio.com/");
        geoFire = new GeoFire(firebase);
    }

    public static FirebaseApi pluck() {
        return INSTANCE;
    }

    public Firebase getApi() {
        return firebase;
    }

    public GeoFire getGeoFire() {
        return geoFire;
    }
}
