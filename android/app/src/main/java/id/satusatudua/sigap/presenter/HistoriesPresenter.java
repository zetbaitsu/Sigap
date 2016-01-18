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
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ActivityHistory;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class HistoriesPresenter extends BenihPresenter<HistoriesPresenter.View> {

    private List<ActivityHistory> histories;
    private User currentUser;
    private boolean myCaseDone;
    private boolean helpCaseDone;

    public HistoriesPresenter(View view) {
        super(view);
        histories = new ArrayList<>();
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public HistoriesPresenter(View view, User user) {
        super(view);
        histories = new ArrayList<>();
        currentUser = user;
    }

    public void loadHistories() {
        view.showLoading();
        myCaseDone = false;
        helpCaseDone = false;
        loadMyCase();
        loadHelpCase();
    }

    private void loadHelpCase() {
        RxFirebase.observeOnce(FirebaseApi.pluck().userHelps(currentUser.getUserId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .filter(dataSnapshot -> !dataSnapshot.child("status").getValue().toString().equals("MENUNGGU"))
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().cases(dataSnapshot.getKey())))
                .map(dataSnapshot -> dataSnapshot.getValue(Case.class))
                .map(theCase -> {
                    ActivityHistory history = new ActivityHistory();
                    history.setTheCase(theCase);
                    User owner = new User();
                    owner.setUserId(theCase.getUserId());
                    history.setUser(owner);

                    int x = histories.indexOf(history);
                    if (x >= 0) {
                        histories.set(x, history);
                    } else {
                        histories.add(history);
                    }

                    return history;
                })
                .flatMap(history -> RxFirebase.observeOnce(FirebaseApi.pluck().users(history.getUser().getUserId())))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .flatMap(user -> {
                    for (int i = 0; i < histories.size(); i++) {
                        if (histories.get(i).getUser().equals(user)) {
                            histories.get(i).setUser(user);
                        }
                    }

                    return Observable.from(histories);
                })
                .filter(history -> history.getUser().getName() != null)
                .distinct()
                .toList()
                .subscribe(histories -> {
                    helpCaseDone = true;
                    if (myCaseDone) {
                        sortAndShow();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    helpCaseDone = true;
                    if (view != null) {
                        view.showError("Gagal memuat data kasus yang pernah anda tolong!");
                    }
                });


    }

    private void sortAndShow() {
        Observable.from(histories)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.COMPUTATION))
                .toSortedList((history, history2) -> history2.getTheCase().getDate().compareTo(history.getTheCase().getDate()))
                .subscribe(histories -> {
                    this.histories = histories;
                    if (view != null) {
                        view.showHistories(histories);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data riwayat aktifitas anda!");
                        view.dismissLoading();
                    }
                });
    }

    private void loadMyCase() {
        RxFirebase.observeOnce(FirebaseApi.pluck().userCases(currentUser.getUserId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().cases(dataSnapshot.getKey())))
                .map(dataSnapshot -> dataSnapshot.getValue(Case.class))
                .map(theCase -> {
                    ActivityHistory history = new ActivityHistory();
                    history.setTheCase(theCase);
                    history.setUser(currentUser);
                    history.setFromMe(true);
                    return history;
                })
                .doOnNext(history -> {
                    int x = histories.indexOf(history);
                    if (x >= 0) {
                        histories.set(x, history);
                    } else {
                        histories.add(history);
                    }
                })
                .toList()
                .subscribe(histories -> {
                    myCaseDone = true;
                    if (helpCaseDone) {
                        sortAndShow();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    myCaseDone = true;
                    if (view != null) {
                        view.showError("Gagal memuat data kasus anda!");
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("histories", (ArrayList<ActivityHistory>) histories);
    }

    @Override
    public void loadState(Bundle bundle) {
        histories = bundle.getParcelableArrayList("histories");
        if (histories == null) {
            loadHistories();
        } else {
            sortAndShow();
        }
    }

    public interface View extends BenihPresenter.View {
        void showHistories(List<ActivityHistory> histories);
    }
}
