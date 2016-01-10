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

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.model.Case;
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
}
