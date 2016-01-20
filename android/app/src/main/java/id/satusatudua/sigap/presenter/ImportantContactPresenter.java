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
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ImportantContactPresenter extends BenihPresenter<ImportantContactPresenter.View> {

    private List<ImportantContact> contacts;

    public ImportantContactPresenter(View view) {
        super(view);
        contacts = new ArrayList<>();
        listenNewContact();
    }

    public void loadContacts() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().importantContacts())
                .doOnNext(dataSnapshots -> {
                    if (dataSnapshots.getValue() == null) {
                        if (view != null) {
                            view.dismissLoading();
                        }
                    }
                })
                .flatMap(dataSnapshots -> Observable.from(dataSnapshots.getChildren()))
                .map(dataSnapshot -> {
                    ImportantContact contact = dataSnapshot.getValue(ImportantContact.class);
                    contact.setContactId(dataSnapshot.getKey());

                    int x = contacts.indexOf(contact);
                    if (x >= 0) {
                        contacts.set(x, contact);
                    } else {
                        contacts.add(contact);
                    }

                    return contact;
                })
                .subscribe(contact -> {
                    if (view != null) {
                        view.onNewContactAdded(contact);
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

    private void listenNewContact() {
        RxFirebase.observeChildAdded(FirebaseApi.pluck().importantContacts())
                .map(firebaseChildEvent -> firebaseChildEvent.snapshot)
                .map(dataSnapshot -> {
                    ImportantContact contact = dataSnapshot.getValue(ImportantContact.class);
                    contact.setContactId(dataSnapshot.getKey());

                    int x = contacts.indexOf(contact);
                    if (x >= 0) {
                        contacts.set(x, contact);
                    } else {
                        contacts.add(contact);
                    }

                    return contact;
                })
                .subscribe(contact -> {
                    if (view != null) {
                        view.onNewContactAdded(contact);
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError(throwable.getMessage());
                    }
                });
    }

    public void filter(String keyword) {
        view.showLoading();
        Observable.from(contacts)
                .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                .filter(contact -> contact.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        contact.getAddress().toLowerCase().contains(keyword.toLowerCase()) ||
                        contact.getPhoneNumber().toLowerCase().contains(keyword.toLowerCase()))
                .toList()
                .subscribe(contacts -> {
                    if (view != null) {
                        view.showFilteredContacts(contacts);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memfilter data kontak");
                        view.dismissLoading();
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelableArrayList("contacts", (ArrayList<ImportantContact>) contacts);
    }

    @Override
    public void loadState(Bundle bundle) {
        contacts = bundle.getParcelableArrayList("contacts");
        if (contacts == null) {
            loadContacts();
        } else {
            view.showFilteredContacts(contacts);
        }
    }

    public interface View extends BenihPresenter.View {
        void onNewContactAdded(ImportantContact importantContact);

        void showFilteredContacts(List<ImportantContact> contacts);
    }
}
