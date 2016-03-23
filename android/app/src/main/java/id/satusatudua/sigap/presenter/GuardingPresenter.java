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

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : January 15, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class GuardingPresenter extends BenihPresenter<GuardingPresenter.View> {

    private Escort escort;

    public GuardingPresenter(View view, Escort escort) {
        super(view);
        this.escort = escort;
    }

    private void listenCaseStatus() {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().escort(escort.getEscortId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .subscribe(dataSnapshot -> {
                    escort.setClosed(true);
                    CacheManager.pluck().cacheGuardingEscort(escort, CacheManager.pluck().getLastEscortReporter());
                    if (escort.isClosed()) {
                        if (view != null) {
                            view.onEscortClosed(escort);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat status pengawalan!");
                    }
                });
    }

    public void refreshData() {
        RxFirebase.observeOnce(FirebaseApi.pluck().escort(escort.getEscortId()))
                .map(dataSnapshot -> dataSnapshot.getValue(Escort.class))
                .subscribe(escort -> {
                    this.escort = escort;
                    CacheManager.pluck().cacheGuardingEscort(escort, CacheManager.pluck().getLastEscortReporter());
                    if (escort.isClosed()) {
                        if (view != null) {
                            view.onEscortClosed(escort);
                        }
                    } else {
                        listenCaseStatus();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal menyegarkan data pengawalan!");
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

        void onEscortClosed(Escort escort);
    }
}
