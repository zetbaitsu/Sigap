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

package id.satusatudua.sigap.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.UserLocation;
import timber.log.Timber;

/**
 * Created on : December 25, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class NearbyService extends Service implements GeoQueryEventListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag(getClass().getSimpleName());
        Timber.d(getClass().getSimpleName() + " is creating");

        UserLocation currentLocation = CacheManager.pluck().getUserLocation();
        if (currentLocation == null) {
            currentLocation = new UserLocation();
            currentLocation.setLatitude(-7.76772);
            currentLocation.setLongitude(110.37863);
        }
        GeoLocation location = new GeoLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

        GeoQuery query = FirebaseApi.pluck().userLocations().queryAtLocation(location, 20);

        CacheManager.pluck().listenUserLocation()
                .subscribe(userLocation -> {
                    if (userLocation != null) {
                        GeoLocation newLocation = new GeoLocation(userLocation.getLatitude(),
                                                                  userLocation.getLongitude());
                        query.setCenter(newLocation);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                });

        query.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Timber.d("onKeyEntered(" + key + ")");
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId(key);
        userLocation.setLatitude(location.latitude);
        userLocation.setLongitude(location.longitude);
        CacheManager.pluck().cacheNearbyUser(userLocation);
    }

    @Override
    public void onKeyExited(String key) {
        Timber.d("onKeyExited(" + key + ")");
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Timber.d("onKeyMoved(" + key + ")");
    }

    @Override
    public void onGeoQueryReady() {
        Timber.d("onGeoQueryReady()");
    }

    @Override
    public void onGeoQueryError(FirebaseError error) {
        Timber.d("onGeoQueryError()");
        Timber.e(error.getMessage());
    }
}
