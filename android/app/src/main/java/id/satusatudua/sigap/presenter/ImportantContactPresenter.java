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

import com.firebase.client.DataSnapshot;

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
                .flatMap(contact -> RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("reviews").child(contact.getContactId())))
                .doOnNext(dataSnapshot -> {
                    int size = contacts.size();
                    for (int i = 0; i < size; i++) {
                        if (contacts.get(i).getContactId().equals(dataSnapshot.getKey())) {
                            long count = dataSnapshot.getChildrenCount();
                            contacts.get(i).setTotalUserRate(count);
                            if (count > 0) {
                                long totalRate = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    totalRate += snapshot.child("rate").getValue(Long.class);
                                }
                                contacts.get(i).setTotalRate(totalRate);
                                contacts.get(i).setAvgRate(contacts.get(i).getTotalRate() / totalRate);
                            } else {
                                contacts.get(i).setTotalRate(0);
                                contacts.get(i).setAvgRate(0.0);
                            }
                        }
                    }
                })
                .toList()
                .flatMap(dataSnapshots1 -> Observable.from(contacts))
                .toSortedList((contact, contact2) -> {
                    return contact.getName().compareTo(contact2.getName());
                })
                .flatMap(Observable::from)
                .toSortedList((contact, contact2) -> {
                    if (contact.isBookmarked() && !contact2.isBookmarked()) {
                        return -1;
                    } else if (!contact.isBookmarked() && contact2.isBookmarked()) {
                        return 1;
                    }
                    return 0;
                })
                .subscribe(contacts -> {
                    if (view != null) {
                        view.showContacts(contacts);
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
        void showContacts(List<ImportantContact> contacts);

        void showFilteredContacts(List<ImportantContact> contacts);
    }
}
