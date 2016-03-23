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
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.GuardCandidate;
import id.satusatudua.sigap.data.model.Message;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 14, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EscortChatPresenter extends BenihPresenter<EscortChatPresenter.View> {

    private User currentUser;
    private Escort escort;
    private List<GuardCandidate> guardCandidates;


    public EscortChatPresenter(View view, Escort escort, User reporter) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
        this.escort = escort;
        guardCandidates = new ArrayList<>();
        GuardCandidate guardCandidate = new GuardCandidate();
        guardCandidate.setGuardingStatus(GuardCandidate.GuardingStatus.MENGAWAL);
        guardCandidate.setUserTrustedId(reporter.getUserId());
        guardCandidate.setUser(reporter);
        guardCandidates.add(guardCandidate);
        listenGuardStatus();
    }

    public void loadGuards() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().guards(escort.getEscortId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .flatMap(dataSnapshot -> RxFirebase.observeOnce(FirebaseApi.pluck().users(dataSnapshot.getKey()))
                        .map(dataSnapshot1 -> dataSnapshot1.getValue(User.class))
                        .map(user -> {
                            GuardCandidate guardCandidate = new GuardCandidate();
                            guardCandidate.setUserTrustedId(user.getUserId());
                            guardCandidate.setGuardingStatus(GuardCandidate.GuardingStatus.valueOf(dataSnapshot
                                                                                                           .child("status")
                                                                                                           .getValue()
                                                                                                           .toString()));
                            guardCandidate.setUser(user);

                            int x = guardCandidates.indexOf(guardCandidate);
                            if (x >= 0) {
                                guardCandidates.set(x, guardCandidate);
                            } else {
                                guardCandidates.add(guardCandidate);
                            }
                            return guardCandidate;
                        })
                )
                .toList()
                .subscribe(guardCandidates -> {
                    listenNewMessage();
                    if (view != null) {
                        view.showGuards(guardCandidates);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (view != null) {
                        view.showError(throwable.getMessage());
                        view.dismissLoading();
                    }
                });
    }

    private void listenGuardStatus() {
        RxFirebase.observeChildChanged(FirebaseApi.pluck().guards(escort.getEscortId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> {
                    GuardCandidate guardCandidate = new GuardCandidate();
                    guardCandidate.setUserTrustedId(dataSnapshot.getKey());
                    guardCandidate.setGuardingStatus(GuardCandidate.
                            GuardingStatus.valueOf(dataSnapshot.child("status").getValue().toString()));

                    int x = guardCandidates.indexOf(guardCandidate);
                    if (x >= 0) {
                        guardCandidates.get(x).setStatus(guardCandidate.getStatus());
                        return guardCandidates.get(x);
                    }
                    return null;
                })
                .subscribe(guardCandidate -> {
                    if (guardCandidate != null && view != null) {
                        view.onGuardStatusChanged(guardCandidate);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat status pengawal!");
                    }
                });
    }

    public void sendMessage(String content) {
        Firebase api = FirebaseApi.pluck().getApi();
        String messageId = api.child("escortMessages").child(escort.getEscortId()).push().getKey();
        Message message = generateTempMessage(content);
        message.setMessageId(messageId);
        message.setSending(true);
        view.onSendingMessage(message);

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("date", message.getDate().getTime());
        msgData.put("userId", message.getSenderId());
        msgData.put("content", message.getContent());

        Map<String, Object> data = new HashMap<>();
        data.put("escortMessages/" + escort.getEscortId() + "/" + messageId + "/", msgData);

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.onFailedSendMessage(message);
                    view.showError("Gagal mengirimkan pesan!");
                }
            } else {
                message.setSending(false);
                if (view != null) {
                    view.onSuccessSendMessage(message);
                }
            }
        });
    }

    private Message generateTempMessage(String content) {
        User currentUser = CacheManager.pluck().getCurrentUser();
        Message message = new Message();
        message.setContent(content);
        message.setDate(new Date());
        message.setFromMe(true);
        message.setSenderId(currentUser.getUserId());
        message.setSender(currentUser);

        return message;
    }

    private void listenNewMessage() {
        RxFirebase.observeChildAdded(FirebaseApi.pluck().escortMessages(escort.getEscortId()))
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> {
                    Message message = new Message();
                    message.setMessageId(dataSnapshot.getKey());
                    message.setContent(dataSnapshot.child("content").getValue().toString());
                    message.setDate(new Date(dataSnapshot.child("date").getValue(Long.class)));
                    message.setSenderId(dataSnapshot.child("userId").getValue().toString());
                    message.setFromMe(message.getSenderId().equals(currentUser.getUserId()));

                    User sender = new User();
                    sender.setUserId(message.getSenderId());
                    GuardCandidate guardCandidate = new GuardCandidate();
                    guardCandidate.setUserTrustedId(sender.getUserId());
                    int x = guardCandidates.indexOf(guardCandidate);
                    if (x >= 0) {
                        sender = guardCandidates.get(x).getUser();
                    }

                    message.setSender(sender);

                    return message;
                })
                .doOnNext(message -> CacheManager.pluck().cacheLastMessageTime(message.getDate().getTime()))
                .subscribe(message -> {
                    if (view != null) {
                        view.onNewMessage(message);
                    }
                }, throwable -> Timber.e(throwable.getMessage()));
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("guards", (ArrayList<GuardCandidate>) guardCandidates);
    }

    @Override
    public void loadState(Bundle bundle) {
        guardCandidates = bundle.getParcelableArrayList("guards");
        if (guardCandidates == null) {
            loadGuards();
        } else {
            view.showGuards(guardCandidates);
            listenNewMessage();
        }
    }

    public interface View extends BenihPresenter.View {
        void showGuards(List<GuardCandidate> guardCandidates);

        void onGuardStatusChanged(GuardCandidate guardCandidate);

        void onSendingMessage(Message message);

        void onSuccessSendMessage(Message message);

        void onFailedSendMessage(Message message);

        void onNewMessage(Message message);
    }
}
