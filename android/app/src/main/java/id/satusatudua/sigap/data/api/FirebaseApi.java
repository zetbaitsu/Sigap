package id.satusatudua.sigap.data.api;

import com.firebase.client.Firebase;

/**
 * Author     : Zetra - https://github.com/zetbaitsu
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
