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
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.MapUtils;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import timber.log.Timber;

/**
 * Created on : January 10, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ConfirmHelpingPresenter extends BenihPresenter<ConfirmHelpingPresenter.View> {

    private User currentUser;
    private Case theCase;
    private User reporter;

    public ConfirmHelpingPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void loadCaseData(String caseId) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().cases(caseId))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                .map(dataSnapshot -> dataSnapshot.getValue(Case.class))
                .subscribe(theCase -> {
                    this.theCase = theCase;
                    if (view != null) {
                        view.showCaseData(this.theCase);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data kasus!");
                        view.dismissLoading();
                    }
                });
    }

    public void loadCaseAddress(Case theCase) {
        view.showLoading();
        MapUtils.getAddress(theCase.getLatitude(), theCase.getLongitude())
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                .subscribe(address -> {
                    if (view != null) {
                        view.showCaseAddress(address);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data alamat lokasi");
                        view.dismissLoading();
                    }
                });
    }

    public void loadReporterData(Case theCase) {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().users(theCase.getUserId()))
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
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
        data.put("helperCases/" + theCase.getCaseId() + "/" + currentUser.getUserId() + "/status/", "MENOLONG");
        data.put("userHelps/" + currentUser.getUserId() + "/" + theCase.getCaseId() + "/status/", "MENOLONG");
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

                if (view != null) {
                    view.onConfirmed(theCase, reporter);
                    view.dismissLoading();
                }
            }
        });
    }

    public void decline() {
        view.showLoading();
        Map<String, Object> data = new HashMap<>();
        data.put("helperCases/" + theCase.getCaseId() + "/" + currentUser.getUserId() + "/status/", "MENOLAK");
        data.put("userHelps/" + currentUser.getUserId() + "/" + theCase.getCaseId() + "/status/", "MENOLAK");
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

        void showCaseData(Case theCase);

        void showCaseAddress(String address);

        void showReporter(User reporter);

        void onConfirmed(Case theCase, User reporter);

        void onDeclined();
    }
}
