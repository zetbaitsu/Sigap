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

import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.event.BookmarkEvent;
import id.zelory.benih.presenter.BenihPresenter;
import id.zelory.benih.util.BenihBus;

/**
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class BookmarkPresenter extends BenihPresenter<BookmarkPresenter.View> {

    private User currentUser;

    public BookmarkPresenter(View view) {
        super(view);
        currentUser = CacheManager.pluck().getCurrentUser();
    }

    public void bookmark(ImportantContact contact) {
        Map<String, Object> data = new HashMap<>();
        data.put(contact.getContactId() + "/contactId", contact.getContactId());

        FirebaseApi.pluck()
                .bookmarkContacts(currentUser.getUserId())
                .updateChildren(data, (firebaseError, firebase) -> {
                    if (firebaseError == null) {
                        contact.setBookmarked(true);
                        if (view != null) {
                            view.onBookmarked(contact);
                            BenihBus.pluck().send(new BookmarkEvent(contact));
                        }
                    } else {
                        contact.setBookmarked(false);
                        if (view != null) {
                            view.onUnBookmark(contact);
                        }
                    }
                });
    }

    public void unBookmark(ImportantContact contact) {
        FirebaseApi.pluck()
                .bookmarkContacts(currentUser.getUserId())
                .child(contact.getContactId())
                .removeValue((firebaseError, firebase) -> {
                    if (firebaseError == null) {
                        contact.setBookmarked(false);
                        if (view != null) {
                            view.onUnBookmark(contact);
                            BenihBus.pluck().send(new BookmarkEvent(contact));
                        }
                    } else {
                        contact.setBookmarked(true);
                        if (view != null) {
                            view.onBookmarked(contact);
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
        void onBookmarked(ImportantContact contact);

        void onUnBookmark(ImportantContact contact);
    }
}
