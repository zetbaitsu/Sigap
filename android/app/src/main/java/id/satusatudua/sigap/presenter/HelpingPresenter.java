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
import id.satusatudua.sigap.data.model.Case;
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
public class HelpingPresenter extends BenihPresenter<HelpingPresenter.View> {

    private Case theCase;

    public HelpingPresenter(View view, Case theCase) {
        super(view);
        this.theCase = theCase;
    }

    private void listenCaseStatus() {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().cases(theCase.getCaseId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> dataSnapshot.getValue(Case.class))
                .subscribe(theCase -> {
                    this.theCase = theCase;
                    CacheManager.pluck().cacheHelpingCase(this.theCase, CacheManager.pluck().getLastCaseReporter());
                    if (theCase.getStatus() == Case.Status.DITUTUP) {
                        if (view != null) {
                            view.onCaseClosed(this.theCase);
                        }
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat status kasus!");
                    }
                });
    }

    public void refreshData() {
        RxFirebase.observeOnce(FirebaseApi.pluck().cases(theCase.getCaseId()))
                .map(dataSnapshot -> dataSnapshot.getValue(Case.class))
                .subscribe(theCase -> {
                    this.theCase = theCase;
                    CacheManager.pluck().cacheHelpingCase(this.theCase, CacheManager.pluck().getLastCaseReporter());
                    if (theCase.getStatus() == Case.Status.DITUTUP) {
                        if (view != null) {
                            view.onCaseClosed(this.theCase);
                        }
                    } else {
                        listenCaseStatus();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal menyegarkan data kasus!");
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
        void onCaseClosed(Case theCase);
    }
}
