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

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.GuardCandidate;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 11, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class RequestEscortPresenter extends BenihPresenter<RequestEscortPresenter.View> {

    private User currentUser;
    private List<GuardCandidate> guardCandidates;

    public RequestEscortPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
        guardCandidates = new ArrayList<>();
    }

    public void loadGuardCandidate() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().userTrusted(currentUser.getUserId()))
                .doOnNext(dataSnapshots -> {
                    if (dataSnapshots.getValue() == null) {
                        if (view != null) {
                            view.dismissLoading();
                        }
                    }
                })
                .flatMap(dataSnapshots -> Observable.from(dataSnapshots.getChildren()))
                .map(dataSnapshot -> {
                    GuardCandidate guardCandidate = new GuardCandidate();
                    guardCandidate.setUserTrustedId(dataSnapshot.getKey());
                    guardCandidate.setStatus(UserTrusted.Status.valueOf(dataSnapshot.child("status").getValue().toString()));

                    int x = guardCandidates.indexOf(guardCandidate);
                    if (x >= 0) {
                        guardCandidates.get(x).setStatus(guardCandidate.getStatus());
                    } else {
                        guardCandidates.add(guardCandidate);
                    }
                    return guardCandidate;
                })
                .flatMap(guardCandidate -> RxFirebase.observeOnce(FirebaseApi.pluck().users(guardCandidate.getUserTrustedId()))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                .map(user -> {
                    GuardCandidate guardCandidate = new GuardCandidate();
                    guardCandidate.setUserTrustedId(user.getUserId());

                    int x = guardCandidates.indexOf(guardCandidate);
                    if (x >= 0) {
                        guardCandidates.get(x).setUser(user);
                    }

                    return guardCandidates.get(x);
                })
                .subscribe(guardCandidate -> {
                    if (view != null) {
                        view.onGuardCandidateAdded(guardCandidate);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                        view.dismissLoading();
                    }
                });
    }

    public void sendRequest(Location location, String address, List<GuardCandidate> guardCandidates) {
        view.showLoading();
        Firebase api = FirebaseApi.pluck().getApi();
        String escortId = api.child("escorts").push().getKey();

        Map<String, Object> escortData = new HashMap<>();
        escortData.put("escortId", escortId);
        escortData.put("userId", currentUser.getUserId());
        escortData.put("date", new Date().getTime());
        escortData.put("destination", address);
        escortData.put("latitude", location.getLatitude());
        escortData.put("longitude", location.getLongitude());
        escortData.put("closed", false);

        Map<String, Object> data = new HashMap<>();
        data.put("escorts/" + escortId, escortData);
        data.put("users/" + currentUser.getUserId() + "/status/", "BAHAYA");
        data.put("userEscorts/" + currentUser.getUserId() + "/" + escortId + "/escortId/", escortId);

        for (int i = 0; i < guardCandidates.size(); i++) {
            data.put("guards/" + escortId + "/" + guardCandidates.get(i).getUserTrustedId() + "/status/", "MENUNGGU");
            data.put("userGuards/" + guardCandidates.get(i).getUserTrustedId() + "/" + escortId + "/status/", "MENUNGGU");
        }

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Maaf kami gagal memproses pemintaan anda!");
                    view.dismissLoading();
                }
            } else {
                currentUser.setStatus(User.Status.BAHAYA);
                CacheManager.pluck().cacheCurrentUser(currentUser);
                StateManager.pluck().setState(StateManager.State.DIKAWAL);

                Escort escort = new Escort();
                escort.setEscortId(escortId);
                escort.setDate(new Date());
                escort.setDestination(address);
                escort.setLatitude(location.getLatitude());
                escort.setLongitude(location.getLongitude());
                escort.setGuardCandidates(guardCandidates);
                CacheManager.pluck().cacheLastEscort(escort);

                if (view != null) {
                    view.onRequestSend();
                    view.dismissLoading();
                }
            }
        });
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("guardCandidates", (ArrayList<GuardCandidate>) guardCandidates);
    }

    @Override
    public void loadState(Bundle bundle) {
        guardCandidates = bundle.getParcelableArrayList("guardCandidates");
        if (guardCandidates == null) {
            loadGuardCandidate();
        } else {
            int size = guardCandidates.size();
            for (int i = 0; i < size; i++) {
                view.onGuardCandidateAdded(guardCandidates.get(i));
            }
        }
    }

    public interface View extends BenihPresenter.View {

        void onGuardCandidateAdded(GuardCandidate guardCandidate);

        void onRequestSend();
    }
}
