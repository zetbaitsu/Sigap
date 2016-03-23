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

import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ConfirmGuardingPresenter extends BenihPresenter<ConfirmGuardingPresenter.View> {

    private User currentUser;
    private Escort escort;
    private User reporter;

    public ConfirmGuardingPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void loadEscortData(String escortId) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().escort(escortId))
                .doOnNext(dataSnapshot -> Timber.d("Data: " + dataSnapshot))
                .map(dataSnapshot -> dataSnapshot.getValue(Escort.class))
                .doOnNext(escort -> Timber.d("Guarding for: " + escort))
                .subscribe(escort -> {
                    this.escort = escort;
                    if (view != null) {
                        view.showEscortData(this.escort);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data pengawalan!");
                        view.dismissLoading();
                    }
                });
    }

    public void loadReporterData(Escort escort) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().users(escort.getUserId()))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .subscribe(user -> {
                    reporter = user;
                    if (view != null) {
                        view.showReporter(reporter);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data pelapor!");
                        view.dismissLoading();
                    }
                });
    }

    public void confirm() {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("guards/" + escort.getEscortId() + "/" + currentUser.getUserId() + "/status/", "MENGAWAL");
        data.put("userGuards/" + currentUser.getUserId() + "/" + escort.getEscortId() + "/status/", "MENGAWAL");
        data.put("users/" + currentUser.getUserId() + "/status/", "MENOLONG");

        FirebaseApi.pluck().getApi().updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Gagal mengirimkan data konfirmasi!");
                    view.dismissLoading();
                }
            } else {
                User currentUser = CacheManager.pluck().getCurrentUser();
                currentUser.setStatus(User.Status.MENOLONG);
                CacheManager.pluck().cacheCurrentUser(currentUser);
                StateManager.pluck().backupState();
                StateManager.pluck().setState(StateManager.State.MENGAWAL);
                CacheManager.pluck().cacheGuardingEscort(escort, reporter);

                if (view != null) {
                    view.onConfirmed(escort, reporter);
                    view.dismissLoading();
                }
            }
        });
    }

    public void decline() {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("guards/" + escort.getEscortId() + "/" + currentUser.getUserId() + "/status/", "MENOLAK");
        data.put("userGuards/" + currentUser.getUserId() + "/" + escort.getEscortId() + "/status/", "MENOLAK");
        data.put("users/" + currentUser.getUserId() + "/status/", "SIAP");

        FirebaseApi.pluck().getApi().updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Gagal mengirimkan data konfirmasi!");
                    view.dismissLoading();
                }
            } else {
                if (view != null) {
                    view.onDeclined();
                    view.dismissLoading();
                }
            }
        });
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {

        void showEscortData(Escort escort);

        void showReporter(User reporter);

        void onConfirmed(Escort escort, User reporter);

        void onDeclined();
    }
}
