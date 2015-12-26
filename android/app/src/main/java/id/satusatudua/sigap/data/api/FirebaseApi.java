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
    private GeoFire userLocations;
    private GeoFire caseLocations;

    FirebaseApi() {
        firebase = new Firebase("https://sigap.firebaseio.com/");
        userLocations = new GeoFire(firebase.child("userLocations"));
        caseLocations = new GeoFire(firebase.child("caseLocations"));
    }

    public static FirebaseApi pluck() {
        return INSTANCE;
    }

    public Firebase getApi() {
        return firebase;
    }

    public Firebase users(String userId) {
        return firebase.child("users").child(userId);
    }

    public GeoFire userLocations() {
        return userLocations;
    }

    public Firebase userTrusted(String userId) {
        return firebase.child("userTrusted").child(userId);
    }

    public Firebase covers(String userId) {
        return firebase.child("covers").child(userId);
    }

    public Firebase userCovers(String userId) {
        return firebase.child("userCovers").child(userId);
    }

    public Firebase coverMessages(String userId) {
        return firebase.child("coverMessages").child(userId);
    }

    public Firebase cases() {
        return firebase.child("cases");
    }

    public Firebase cases(String caseId) {
        return firebase.child("cases").child(caseId);
    }

    public GeoFire caseLocations() {
        return caseLocations;
    }

    public Firebase userCases(String userId) {
        return firebase.child("userCases").child(userId);
    }

    public Firebase helperCases(String caseId) {
        return firebase.child("helperCases").child(caseId);
    }

    public Firebase userHelps(String userId) {
        return firebase.child("userHelps").child(userId);
    }

    public Firebase caseMessages(String caseId) {
        return firebase.child("caseMessages").child(caseId);
    }

    public Firebase importantContacts(int page) {
        return firebase.child("importantContacts");
    }

    public Firebase importantContact(String contactId) {
        return firebase.child("importantContacts").child(contactId);
    }

    public Firebase userContacts(String userId) {
        return firebase.child("userContacts").child(userId);
    }

    public Firebase contactMessages(String contactId) {
        return firebase.child("contactMessages").child(contactId);
    }

    public Firebase bookmarkContacts(String userId) {
        return firebase.child("bookmarkContacts").child(userId);
    }
}
