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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.seismic.ShakeDetector;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.SigapApp;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.presenter.TombolPresenter;
import id.satusatudua.sigap.ui.EmergencyActivity;
import id.satusatudua.sigap.ui.TombolActivity;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EmergencyService extends Service implements ShakeDetector.Listener,
        TombolPresenter.View {

    private TombolPresenter tombolPresenter;

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

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.start(sensorManager);

        listenShowEmergencyButton();

        tombolPresenter = new TombolPresenter(this);
    }

    private void listenShowEmergencyButton() {
        StateManager.pluck().listenState()
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .flatMap(state -> {
                    if (state == StateManager.State.LOGGED) {
                        return CacheManager.pluck().listenShowOnStatusBar()
                                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO));
                    } else {
                        return Observable.just(false);
                    }
                })
                .subscribe(show -> {
                    Timber.d("Show button: " + show);
                    if (show) {
                        showEmergencyButton();
                    } else {
                        NotificationManagerCompat.from(SigapApp.pluck().getApplicationContext()).cancel(696961);
                    }
                }, throwable -> Timber.e(throwable.getMessage()));
    }

    @Override
    public void hearShake() {
        Timber.d("Shaking.....");
        if (StateManager.pluck().getState() == StateManager.State.LOGGED && CacheManager.pluck().isShakeToNotify()) {
            tombolPresenter.createCase();
        }
    }

    @Override
    public void onCaseCreated(String caseId) {
        Intent intent = new Intent(this, EmergencyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    private void showEmergencyButton() {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                                                TombolActivity.generateIntent(this, true),
                                                                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_emergency_button);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(views)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();

        NotificationManagerCompat
                .from(SigapApp.pluck().getApplicationContext())
                .notify(696961, notification);
    }
}
