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

package id.satusatudua.sigap.controller;

import android.os.Bundle;

import com.google.android.gms.location.LocationRequest;

import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.controller.event.ErrorEvent;
import id.satusatudua.sigap.data.LocalDataManager;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.controller.BenihController;
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
public class LocationController extends BenihController<LocationController.Presenter> {

    private LocationRequest request;
    private ReactiveLocationProvider locationProvider;

    public LocationController(Presenter presenter) {
        super(presenter);
        request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100);
        locationProvider = new ReactiveLocationProvider(SigapApp.pluck().getApplicationContext());
        listenLocationUpdate();
    }

    public void loadLastLocation() {
        locationProvider.getLastKnownLocation()
                .map(location -> new Location(location.getLatitude(), location.getLongitude()))
                .subscribe(location -> {
                    if (presenter != null) {
                        presenter.showLastKnownLocation(location);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (presenter != null) {
                        presenter.showError(ErrorEvent.GENERAL);
                    }
                });
    }

    private void listenLocationUpdate() {
        locationProvider.getUpdatedLocation(request)
                .map(location -> new Location(location.getLatitude(), location.getLongitude()))
                .subscribe(location -> {
                    if (presenter != null) {
                        User user = LocalDataManager.getCurrentUser();
                        user.setLocation(location);
                        FirebaseApi.pluck().getApi().child("users").child(user.getUid()).setValue(user);
                        LocalDataManager.saveCurrentUser(user);
                        presenter.onLocationUpdated(location);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (presenter != null) {
                        presenter.showError(ErrorEvent.GENERAL);
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface Presenter extends BenihController.Presenter {
        void showLastKnownLocation(Location location);

        void onLocationUpdated(Location location);
    }
}
