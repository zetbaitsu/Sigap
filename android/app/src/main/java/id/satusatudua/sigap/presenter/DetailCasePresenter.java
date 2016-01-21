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

import java.util.ArrayList;
import java.util.List;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.CaseReview;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class DetailCasePresenter extends BenihPresenter<DetailCasePresenter.View> {

    private Case theCase;
    private User reporter;
    private List<CaseReview> reviews;

    public DetailCasePresenter(View view, Case theCase, User reporter) {
        super(view);
        this.theCase = theCase;
        this.reporter = reporter;
        reviews = new ArrayList<>();
        CaseReview caseReview = new CaseReview();
        caseReview.setUserId(reporter.getUserId());
        caseReview.setUser(reporter);
        caseReview.setStatus("Pelapor");
        caseReview.setReview(theCase.getDetail());
        reviews.add(caseReview);
    }

    public void loadReviews() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().helperCases(theCase.getCaseId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .filter(dataSnapshot -> dataSnapshot.child("feedback").getValue() != null)
                .map(dataSnapshot -> {
                    CaseReview caseReview = new CaseReview();
                    caseReview.setUserId(dataSnapshot.getKey());
                    caseReview.setStatus("Penolong");
                    caseReview.setReview(dataSnapshot.child("feedback").getValue().toString());

                    int x = reviews.indexOf(caseReview);
                    if (x >= 0) {
                        reviews.set(x, caseReview);
                    } else {
                        reviews.add(caseReview);
                    }

                    return caseReview;
                })
                .flatMap(caseReview -> RxFirebase.observeOnce(FirebaseApi.pluck().users(caseReview.getUserId())))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .doOnNext(user -> {
                    int size = reviews.size();
                    for (int i = 0; i < size; i++) {
                        if (reviews.get(i).getUserId().equals(user.getUserId())) {
                            reviews.get(i).setUser(user);
                        }
                    }
                })
                .toList()
                .subscribe(users -> {
                    if (view != null) {
                        view.showReviews(reviews);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data ulasan kasus!");
                        view.dismissLoading();
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
        void showReviews(List<CaseReview> reviews);
    }
}
