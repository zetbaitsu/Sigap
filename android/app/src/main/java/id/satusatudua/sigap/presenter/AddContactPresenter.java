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
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : January 19, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class AddContactPresenter extends BenihPresenter<AddContactPresenter.View> {

    public AddContactPresenter(View view) {
        super(view);
    }

    public void addContact(String name, String phoneNumber, String address) {
        view.showLoading();
        User currentUser = CacheManager.pluck().getCurrentUser();
        Firebase api = FirebaseApi.pluck().getApi();
        String id = api.child("importantContacts").push().getKey();

        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", name);
        contactData.put("phoneNumber", phoneNumber);
        contactData.put("address", address);
        contactData.put("createdAt", new Date().getTime());
        contactData.put("userId", currentUser.getUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("importantContacts/" + id, contactData);
        data.put("userContacts/" + currentUser.getUserId() + "/" + id + "/contactId/", id);

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError == null) {
                if (view != null) {
                    view.onContactAdded();
                    view.dismissLoading();
                }
            } else {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Gagal mengirimkan data kontak baru!");
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
        void onContactAdded();
    }
}
