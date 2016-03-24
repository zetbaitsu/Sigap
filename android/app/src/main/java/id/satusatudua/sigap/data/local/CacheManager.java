/*
 * Copyright (c) 2015 SatuSatuDua.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.satusatudua.sigap.data.local;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.satusatudua.sigap.util.Sorter;
import id.zelory.benih.util.Bson;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : December 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public enum CacheManager {
    HARVEST;

    private final SharedPreferences sharedPreferences;
    private final RxSharedPreferences rxPreferences;

    CacheManager() {
        sharedPreferences = SigapApp.pluck().getSharedPreferences("sigap.zl", Context.MODE_PRIVATE);
        rxPreferences = RxSharedPreferences.create(sharedPreferences);
        Timber.tag(getClass().getSimpleName());
    }

    public static CacheManager pluck() {
        return HARVEST;
    }

    public void cacheCurrentUser(User user) {
        sharedPreferences.edit().putString("current_user", Bson.pluck().getParser().toJson(user)).apply();
    }

    public Observable<User> listenCurrentUser() {
        return rxPreferences.getString("current_user", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, User.class));
    }

    public User getCurrentUser() {
        String json = sharedPreferences.getString("current_user", "");
        return Bson.pluck().getParser().fromJson(json, User.class);
    }

    public void cacheUserLocation(UserLocation userLocation) {
        if (getUserLocation() == null) {
            SigapApp.pluck().sendBroadcast(new Intent("id.satusatudua.sigap.GOT_LOCATION"));
        }
        Timber.d("cacheUserLocation " + userLocation);
        sharedPreferences.edit().putString("user_location", Bson.pluck().getParser().toJson(userLocation)).apply();

    }

    public Observable<UserLocation> listenUserLocation() {
        return rxPreferences.getString("user_location", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, UserLocation.class));
    }

    public UserLocation getUserLocation() {
        String json = sharedPreferences.getString("user_location", "");
        return Bson.pluck().getParser().fromJson(json, UserLocation.class);
    }

    public void cacheNearbyUser(UserLocation userLocation) {
        if (userLocation.getUserId().equals(getCurrentUser().getUserId())) {
            return;
        }
        List<UserLocation> userLocations = getNearbyUsers();
        if (userLocations == null) {
            userLocations = new ArrayList<>();
        }
        if (!userLocations.contains(userLocation)) {
            userLocations.add(userLocation);
            userLocations = Sorter.sortUserLocation(userLocations);
            if (userLocations.size() > 10) {
                userLocations = userLocations.subList(0, 10);
            }

            sharedPreferences.edit().putString("nearby_users", Bson.pluck().getParser().toJson(userLocations)).apply();
        }
    }

    public Observable<List<UserLocation>> listenNearbyUsers() {
        return rxPreferences.getString("nearby_users", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, new TypeToken<List<UserLocation>>() {}.getType()));
    }

    public List<UserLocation> getNearbyUsers() {
        String json = sharedPreferences.getString("nearby_users", "");
        return Bson.pluck().getParser().fromJson(json, new TypeToken<List<UserLocation>>() {}.getType());
    }

    public void cacheCase(String caseId) {
        List<String> lastCase = getLastCases();
        if (lastCase == null) {
            lastCase = new ArrayList<>();
        }
        if (!lastCase.contains(caseId)) {
            lastCase.add(caseId);
            sharedPreferences.edit().putString("last_cases", Bson.pluck().getParser().toJson(lastCase)).apply();
        }
    }

    public List<String> getLastCases() {
        String json = sharedPreferences.getString("last_cases", "");
        return Bson.pluck().getParser().fromJson(json, new TypeToken<List<String>>() {}.getType());
    }

    public void cacheLastCase(Case caseTemp) {
        sharedPreferences.edit().putString("last_case", Bson.pluck().getParser().toJson(caseTemp)).apply();
    }

    public Case getLastCase() {
        String json = sharedPreferences.getString("last_case", "");
        return Bson.pluck().getParser().fromJson(json, Case.class);
    }

    public List<String> getLastGuarding() {
        String json = sharedPreferences.getString("last_escorts", "");
        return Bson.pluck().getParser().fromJson(json, new TypeToken<List<String>>() {}.getType());
    }

    public void cacheGuarding(String escortId) {
        List<String> lastEscorts = getLastGuarding();
        if (lastEscorts == null) {
            lastEscorts = new ArrayList<>();
        }
        if (!lastEscorts.contains(escortId)) {
            lastEscorts.add(escortId);
            sharedPreferences.edit().putString("last_escorts", Bson.pluck().getParser().toJson(lastEscorts)).apply();
        }
    }

    public void cacheLastEscort(Escort escort) {
        sharedPreferences.edit().putString("last_escort", Bson.pluck().getParser().toJson(escort)).apply();
    }

    public Escort getLastEscort() {
        String json = sharedPreferences.getString("last_escort", "");
        return Bson.pluck().getParser().fromJson(json, Escort.class);
    }

    public void cacheHelpingCase(Case theCase, User reporter) {
        sharedPreferences.edit().putString("helping_case", Bson.pluck().getParser().toJson(theCase)).apply();
        sharedPreferences.edit().putString("last_case_reporter", Bson.pluck().getParser().toJson(reporter)).apply();
    }

    public Case getLastHelpingCase() {
        String json = sharedPreferences.getString("helping_case", "");
        return Bson.pluck().getParser().fromJson(json, Case.class);
    }

    public Observable<Case> listenLastHelpingCase() {
        return rxPreferences.getString("helping_case", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, Case.class));
    }

    public void cacheGuardingEscort(Escort escort, User reporter) {
        sharedPreferences.edit().putString("guarding_escort", Bson.pluck().getParser().toJson(escort)).apply();
        sharedPreferences.edit().putString("last_escort_reporter", Bson.pluck().getParser().toJson(reporter)).apply();
    }

    public Escort getLastGuardingEscort() {
        String json = sharedPreferences.getString("guarding_escort", "");
        return Bson.pluck().getParser().fromJson(json, Escort.class);
    }

    public Observable<Escort> listenLastGuardingEscort() {
        return rxPreferences.getString("guarding_escort", "")
                .asObservable()
                .map(s -> Bson.pluck().getParser().fromJson(s, Escort.class));
    }

    public User getLastCaseReporter() {
        String json = sharedPreferences.getString("last_case_reporter", "");
        return Bson.pluck().getParser().fromJson(json, User.class);
    }

    public User getLastEscortReporter() {
        String json = sharedPreferences.getString("last_escort_reporter", "");
        return Bson.pluck().getParser().fromJson(json, User.class);
    }

    public void cacheLastMessageTime(long time) {
        sharedPreferences.edit().putLong("last_time_message", time).apply();
    }

    public long getLastMessageTime() {
        return sharedPreferences.getLong("last_time_message", 0);
    }

    public void setRingtone(String path) {
        sharedPreferences.edit().putString("ringtone", path).apply();
    }

    public String getRingtone() {
        return sharedPreferences.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
    }

    public void setVibrate(boolean isVibrate) {
        sharedPreferences.edit().putBoolean("vibrate", isVibrate).apply();
    }

    public boolean isVibrate() {
        return sharedPreferences.getBoolean("vibrate", true);
    }

    public void setNotifyNearby(boolean isNotifyNearby) {
        sharedPreferences.edit().putBoolean("notify_nearby", isNotifyNearby).apply();
    }

    public boolean isNotifyNearby() {
        return sharedPreferences.getBoolean("notify_nearby", true);
    }

    public void setShowOnStatusBar(boolean isShowOnStatusBar) {
        sharedPreferences.edit().putBoolean("show_status_bar", isShowOnStatusBar).apply();
    }

    public boolean isShowOnStatusBar() {
        return sharedPreferences.getBoolean("show_status_bar", false);
    }

    public Observable<Boolean> listenShowOnStatusBar() {
        return rxPreferences.getBoolean("show_status_bar", false).asObservable();
    }

    public void setShakeToNotify(boolean isShakeToNotify) {
        sharedPreferences.edit().putBoolean("shaking", isShakeToNotify).apply();
    }

    public boolean isShakeToNotify() {
        return sharedPreferences.getBoolean("shaking", true);
    }

    public void cacheLastPicturePath(String path) {
        sharedPreferences.edit().putString("last_picture_path", path).apply();
    }

    public String getLastPicturePath() {
        return sharedPreferences.getString("last_picture_path", "");
    }
}
