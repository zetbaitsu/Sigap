package id.satusatudua.sigap.data.api;

import com.firebase.client.Firebase;

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

    FirebaseApi() {
        firebase = new Firebase("https://sigap.firebaseio.com/");
    }

    public static FirebaseApi pluck() {
        return INSTANCE;
    }

    public Firebase getApi() {
        return firebase;
    }
}
