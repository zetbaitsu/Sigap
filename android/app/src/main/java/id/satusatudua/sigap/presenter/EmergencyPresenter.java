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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserLocation;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.util.MapUtils;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EmergencyPresenter extends BenihPresenter<EmergencyPresenter.View> {

    private Case theCase;
    private List<CandidateHelper> candidateHelpers;
    private List<UserTrusted> trusteds;
    private boolean trustedUserDone = false;
    private boolean findHelperDone = false;

    public EmergencyPresenter(View view) {
        super(view);
        theCase = CacheManager.pluck().getLastCase();
        candidateHelpers = new ArrayList<>();
        trusteds = new ArrayList<>();
        listenHelperStatus();
    }

    public void init() {
        if (theCase.getStatus() == Case.Status.BARU) {
            view.showEmergencyLoading();
            addTrustedUser();
            findHelper();
        } else {
            loadHelper();
        }
    }

    private void sendData() {
        Map<String, Object> data = new HashMap<>();

        for (int i = 0; i < candidateHelpers.size(); i++) {
            Timber.d("Add helper: " + candidateHelpers.get(i).getCandidate().getName());
            //Simpan siapa saja yang didaftarkan untuk menolong kasus ini
            data.put("helperCases/" + theCase.getCaseId() + "/" + candidateHelpers.get(i).getCandidateId() + "/status/", "MENUNGGU");

            //Simpan kalau user ini pernah didaftarkan untuk menolong kasus ini
            data.put("userHelps/" + candidateHelpers.get(i).getCandidateId() + "/" + theCase.getCaseId() + "/status/", "MENUNGGU");
        }

        data.put("cases/" + theCase.getCaseId() + "/status", "BERJALAN");

        FirebaseApi.pluck().getApi().updateChildren(data);

        theCase.setStatus(Case.Status.BERJALAN);
        CacheManager.pluck().cacheLastCase(theCase);
        saveAddress();
    }

    private void saveAddress() {
        MapUtils.getAddress(theCase.getLatitude(), theCase.getLongitude())
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                .subscribe(address -> {
                    Timber.d("Try get address from: " + theCase.getLatitude() + ", " + theCase.getLongitude());
                    Timber.d("Address: " + address);
                    Map<String, Object> data = new HashMap<>();
                    data.put("cases/" + theCase.getCaseId() + "/address", address);
                    FirebaseApi.pluck().getApi().updateChildren(data);
                    theCase.setAddress(address);
                    CacheManager.pluck().cacheLastCase(theCase);
                }, throwable -> Timber.e("Error address: " + throwable.getMessage()));
    }

    public void addTrustedUser() {
        if (theCase.getStatus() == Case.Status.BARU) {
            RxFirebase.observeOnce(FirebaseApi.pluck().userTrusted(CacheManager.pluck().getCurrentUser().getUserId()))
                    .flatMap(dataSnapshots -> Observable.from(dataSnapshots.getChildren()))
                    .map(dataSnapshot -> {
                        UserTrusted userTrusted = new UserTrusted();
                        userTrusted.setUserTrustedId(dataSnapshot.getKey());
                        userTrusted.setStatus(UserTrusted.Status.valueOf(dataSnapshot.child("status").getValue().toString()));

                        int x = trusteds.indexOf(userTrusted);
                        if (x >= 0) {
                            trusteds.get(x).setStatus(userTrusted.getStatus());
                        } else {
                            trusteds.add(userTrusted);
                        }
                        return userTrusted;
                    })
                    .filter(userTrusted -> userTrusted.getStatus() == UserTrusted.Status.DITERIMA)
                    .flatMap(userTrusted -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userTrusted.getUserTrustedId()))
                            .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class)))
                    .filter(user -> user.getStatus() == User.Status.SIAP)
                    .map(user -> {
                        UserTrusted userTrusted = new UserTrusted();
                        userTrusted.setUserTrustedId(user.getUserId());

                        int x = trusteds.indexOf(userTrusted);
                        if (x >= 0) {
                            trusteds.get(x).setUser(user);
                        }

                        return trusteds.get(x);
                    })
                    .toList()
                    .doOnNext(userTrusteds -> {
                        for (int i = 0; i < userTrusteds.size(); i++) {
                            CandidateHelper candidateHelper = new CandidateHelper();
                            candidateHelper.setCandidateId(userTrusteds.get(i).getUser().getUserId());
                            candidateHelper.setStatus(CandidateHelper.Status.MENUNGGU);
                            candidateHelper.setCandidate(userTrusteds.get(i).getUser());

                            int x = candidateHelpers.indexOf(candidateHelper);
                            if (x >= 0) {
                                candidateHelpers.set(x, candidateHelper);
                            } else {
                                candidateHelpers.add(candidateHelper);
                            }
                        }

                        trustedUserDone = true;
                        if (findHelperDone) {
                            sendData();
                        }
                    })
                    .flatMap(users -> Observable.from(candidateHelpers))
                    .subscribe(candidateHelper -> {
                        if (view != null) {
                            view.onNewHelperAdded(candidateHelper);
                            view.dismissEmergencyLoading();
                        }
                    }, throwable -> {
                        findHelperDone = true;
                        Timber.e("Error add trusted: " + throwable.getMessage());
                        if (view != null) {
                            view.showError(throwable.getMessage());
                            view.dismissEmergencyLoading();
                        }
                    });
        }
    }

    public void findHelper() {
        if (theCase.getStatus() == Case.Status.BARU) {
            List<UserLocation> nearbyUsers = CacheManager.pluck().getNearbyUsers();
            if (nearbyUsers == null) {
                view.showError("Tidak dapat menemukan pengguna lain disekitar anda.");
                view.dismissEmergencyLoading();
                findHelperDone = true;
                if (trustedUserDone) {
                    sendData();
                }
            } else {
                Observable.from(nearbyUsers)
                        .flatMap(userLocation -> RxFirebase.observeOnce(FirebaseApi.pluck().users(userLocation.getUserId()))
                                .map(dataSnapshot -> dataSnapshot.getValue(User.class)))
                        .filter(user -> user.getStatus() == User.Status.SIAP)
                        .take(5)
                        .toList()
                        .doOnNext(users -> {
                            for (int i = 0; i < users.size(); i++) {
                                CandidateHelper candidateHelper = new CandidateHelper();
                                candidateHelper.setCandidateId(users.get(i).getUserId());
                                candidateHelper.setStatus(CandidateHelper.Status.MENUNGGU);
                                candidateHelper.setCandidate(users.get(i));

                                int x = candidateHelpers.indexOf(candidateHelper);
                                if (x >= 0) {
                                    candidateHelpers.set(x, candidateHelper);
                                } else {
                                    candidateHelpers.add(candidateHelper);
                                }
                            }

                            findHelperDone = true;
                            if (trustedUserDone) {
                                sendData();
                            }
                        })
                        .flatMap(users -> Observable.from(candidateHelpers))
                        .subscribe(candidateHelper -> {
                            if (view != null) {
                                view.onNewHelperAdded(candidateHelper);
                                view.dismissEmergencyLoading();
                            }
                        }, throwable -> {
                            findHelperDone = true;
                            Timber.e("Error find helper: " + throwable.getMessage());
                            if (view != null) {
                                view.showError(throwable.getMessage());
                                view.dismissEmergencyLoading();
                            }
                        });
            }
        }
    }

    public void loadHelper() {
        view.showEmergencyLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().helperCases(theCase.getCaseId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().users(dataSnapshot.getKey()))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class))
                        .map(user -> {
                            CandidateHelper candidateHelper = new CandidateHelper();
                            candidateHelper.setCandidateId(user.getUserId());
                            candidateHelper.setStatus(CandidateHelper.Status.valueOf(dataSnapshot
                                                                                             .child("status")
                                                                                             .getValue()
                                                                                             .toString()));
                            candidateHelper.setCandidate(user);

                            int x = candidateHelpers.indexOf(candidateHelper);
                            if (x >= 0) {
                                candidateHelpers.set(x, candidateHelper);
                            } else {
                                candidateHelpers.add(candidateHelper);
                            }
                            return candidateHelper;
                        })
                )
                .subscribe(candidateHelper -> {
                    if (view != null) {
                        view.onNewHelperAdded(candidateHelper);
                        view.dismissEmergencyLoading();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    Timber.e("Error load helper: " + throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                        view.dismissEmergencyLoading();
                    }
                });
    }

    private void listenHelperStatus() {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().helperCases(theCase.getCaseId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> {
                    CandidateHelper candidateHelper = new CandidateHelper();
                    candidateHelper.setCandidateId(dataSnapshot.getKey());
                    candidateHelper.setStatus(CandidateHelper.
                            Status.valueOf(dataSnapshot.child("status").getValue().toString()));

                    int x = candidateHelpers.indexOf(candidateHelper);
                    if (x >= 0) {
                        candidateHelpers.get(x).setStatus(candidateHelper.getStatus());
                        return candidateHelpers.get(x);
                    }
                    return null;
                })
                .subscribe(candidateHelper -> {
                    if (candidateHelper != null && view != null) {
                        view.onHelperStatusChanged(candidateHelper);
                    }
                }, throwable -> {
                    Timber.e("Error listen helper: " + throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat status penolong!");
                    }
                });
    }

    public Case getTheCase() {
        return theCase;
    }

    public List<CandidateHelper> getHelpers() {
        return candidateHelpers;
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("helpers", (ArrayList<CandidateHelper>) candidateHelpers);
    }

    @Override
    public void loadState(Bundle bundle) {
        candidateHelpers = bundle.getParcelableArrayList("helpers");
        if (candidateHelpers == null) {
            loadHelper();
        } else {
            Observable.from(candidateHelpers)
                    .subscribe(candidateHelper -> {
                        if (view != null) {
                            view.onNewHelperAdded(candidateHelper);
                            view.dismissEmergencyLoading();
                        }
                    }, throwable -> {
                        Timber.e("Error load state: " + throwable.getMessage());
                        if (view != null) {
                            view.showError(throwable.getMessage());
                            view.dismissEmergencyLoading();
                        }
                    });
        }
    }

    public interface View extends BenihPresenter.View {
        void onNewHelperAdded(CandidateHelper candidateHelper);

        void onHelperStatusChanged(CandidateHelper candidateHelper);
        
        void showEmergencyLoading();
        
        void dismissEmergencyLoading();
    }
}
