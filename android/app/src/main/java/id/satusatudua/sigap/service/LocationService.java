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

import com.google.android.gms.location.LocationRequest;

import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.presenter.LocationPresenter;
import id.zelory.benih.util.BenihUtils;
import timber.log.Timber;

/**
 * Created on : November 23, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LocationService extends Service implements LocationPresenter.View {

    private LocationPresenter locationPresenter;

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

        if (BenihUtils.isMyAppRunning(getApplicationContext(), getPackageName())) {
            locationPresenter = new LocationPresenter(this, LocationRequest.PRIORITY_HIGH_ACCURACY);
        } else {
            locationPresenter = new LocationPresenter(this, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        Timber.d("Location updated from service: " + location);
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationPresenter != null) {
            locationPresenter.destroy();
            locationPresenter = null;
        }
    }
}
