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
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
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
public class ChatPresenter extends BenihPresenter<ChatPresenter.View> {

    private User currentUser;
    private Case theCase;
    private List<CandidateHelper> candidateHelpers;


    public ChatPresenter(View view, Case theCase) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
        candidateHelpers = new ArrayList<>();
        this.theCase = theCase;
        listenHelperStatus();
    }

    public void loadHelper() {
        view.showLoading();
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
                .toList()
                .subscribe(candidateHelpers -> {
                    listenNewMessage();
                    if (view != null) {
                        view.showHelpers(candidateHelpers);
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
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat status penolong!");
                    }
                });
    }

    public void sendMessage(String content) {
        Firebase api = FirebaseApi.pluck().getApi();
        String messageId = api.child("caseMessages").child(theCase.getCaseId()).push().getKey();
        Message message = generateTempMessage(content);
        message.setMessageId(messageId);
        message.setSending(true);
        view.onSendingMessage(message);

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("date", message.getDate().getTime());
        msgData.put("userId", message.getSenderId());
        msgData.put("content", message.getContent());

        Map<String, Object> data = new HashMap<>();
        data.put("caseMessages/" + theCase.getCaseId() + "/" + messageId + "/", msgData);

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
        RxFirebase.observeChildAdded(FirebaseApi.pluck().caseMessages(theCase.getCaseId()))
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
                    CandidateHelper helper = new CandidateHelper();
                    helper.setCandidateId(sender.getUserId());
                    int x = candidateHelpers.indexOf(helper);
                    if (x >= 0) {
                        sender = candidateHelpers.get(x).getCandidate();
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
        bundle.putParcelableArrayList("helpers", (ArrayList<CandidateHelper>) candidateHelpers);
    }

    @Override
    public void loadState(Bundle bundle) {
        candidateHelpers = bundle.getParcelableArrayList("helpers");
        if (candidateHelpers == null) {
            loadHelper();
        } else {
            view.showHelpers(candidateHelpers);
            listenNewMessage();
        }
    }

    public interface View extends BenihPresenter.View {
        void showHelpers(List<CandidateHelper> helpers);

        void onHelperStatusChanged(CandidateHelper candidateHelper);

        void onSendingMessage(Message message);

        void onSuccessSendMessage(Message message);

        void onFailedSendMessage(Message message);

        void onNewMessage(Message message);
    }
}
