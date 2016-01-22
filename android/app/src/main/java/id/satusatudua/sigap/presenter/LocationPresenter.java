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

package id.satusatudua.sigap.presenter;

import android.os.Bundle;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationRequest;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.zelory.benih.presenter.BenihPresenter;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import timber.log.Timber;

/**
 * Created on : November 23, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LocationPresenter extends BenihPresenter<LocationPresenter.View> {

    private LocationRequest request;
    private ReactiveLocationProvider locationProvider;
    private User currentUser;

    public LocationPresenter(View view, int priority) {
        super(view);
        listenCurrentUser();
        request = LocationRequest.create()
                .setPriority(priority)
                .setInterval(100);
        locationProvider = new ReactiveLocationProvider(SigapApp.pluck().getApplicationContext());
        listenLocationUpdate();
    }

    private void listenCurrentUser() {
        CacheManager.pluck()
                .listenCurrentUser()
                .subscribe(user -> {
                    if (user != null){
                        currentUser = user;
                    }
                });
    }

    private void listenLocationUpdate() {
        locationProvider.getUpdatedLocation(request)
                .subscribe(location -> {
                    if (StateManager.pluck().getState().equals(StateManager.State.LOGGED) ||
                            StateManager.pluck().getState().equals(StateManager.State.ADDING_TRUSTED_USER) ||
                            StateManager.pluck().getState().equals(StateManager.State.MENOLONG) ||
                            StateManager.pluck().getState().equals(StateManager.State.DITOLONG)) {
                        UserLocation userLocation = new UserLocation();
                        userLocation.setUserId(currentUser.getUserId());
                        userLocation.setLatitude(location.getLatitude());
                        userLocation.setLongitude(location.getLongitude());
                        CacheManager.pluck().cacheUserLocation(userLocation);
                        FirebaseApi.pluck()
                                .userLocations()
                                .setLocation(currentUser.getUserId(),
                                             new GeoLocation(userLocation.getLatitude(), userLocation.getLongitude()));
                        if (view != null) {
                            view.onLocationUpdated(userLocation);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public void destroy() {
        request = null;
        locationProvider = null;
    }

    public interface View extends BenihPresenter.View {
        void onLocationUpdated(Location location);
    }
}
