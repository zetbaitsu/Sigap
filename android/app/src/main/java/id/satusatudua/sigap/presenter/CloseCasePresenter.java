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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
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
public class CloseCasePresenter extends BenihPresenter<CloseCasePresenter.View> {

    private Case theCase;
    private List<CandidateHelper> helpers;
    private User currentUser;

    public CloseCasePresenter(View view, Case theCase, List<CandidateHelper> helpers) {
        super(view);
        this.theCase = theCase;
        this.helpers = helpers;
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void sendChronology(String chronology) {
        view.showLoading();
        Firebase api = FirebaseApi.pluck().getApi();
        Map<String, Object> data = new HashMap<>();

        String messageId = api.child("caseMessages").child(theCase.getCaseId()).push().getKey();
        data.put("caseMessages/" + theCase.getCaseId() + "/" + messageId + "/", generateMessage());

        for (CandidateHelper helper : helpers) {
            data.put("users/" + helper.getCandidateId() + "/status/", "SIAP");
        }

        data.put("users/" + currentUser.getUserId() + "/status/", "SIAP");
        data.put("cases/" + theCase.getCaseId() + "/detail/", chronology);
        data.put("cases/" + theCase.getCaseId() + "/status/", "DITUTUP");

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Gagal mengirimkan data kronologi!");
                    view.dismissLoading();
                }
            } else {
                User currentUser = CacheManager.pluck().getCurrentUser();
                currentUser.setStatus(User.Status.SIAP);
                CacheManager.pluck().cacheCurrentUser(currentUser);
                StateManager.pluck().setState(StateManager.State.LOGGED);

                if (view != null) {
                    view.onCaseClosed();
                    view.dismissLoading();
                }
            }
        });
    }

    private Map<String, Object> generateMessage() {
        Map<String, Object> msgData = new HashMap<>();
        msgData.put("date", new Date().getTime());
        msgData.put("userId", "Sigap");
        msgData.put("content", "[CLOSED]" + currentUser.getName().split(" ")[0]
                + " telah menyatakan dirinya selamat kawan \uD83D\uDE00. Klik pesan ini untuk memberikan ulasan anda akan kejadian![/CLOSED]");

        return msgData;
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onCaseClosed();
    }
}
