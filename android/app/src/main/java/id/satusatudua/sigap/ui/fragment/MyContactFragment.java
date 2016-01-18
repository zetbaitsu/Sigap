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

package id.satusatudua.sigap.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.MainActivity;
import id.satusatudua.sigap.ui.adapter.MyContactAdapter;
import id.satusatudua.sigap.util.PasswordUtils;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihUtils;
import id.zelory.benih.util.BenihWorker;
import timber.log.Timber;

/**
 * Created on : January 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class MyContactFragment extends BenihFragment implements
        SwipeRefreshLayout.OnRefreshListener {
    private static final String KEY_USER = "extra_user";

    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    private User user;
    private MyContactAdapter myContactAdapter;
    private List<ImportantContact> contacts;

    public static MyContactFragment newInstance(User user) {
        MyContactFragment fragment = new MyContactFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_my_contact;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveUser(savedInstanceState);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        myContactAdapter = new MyContactAdapter(getActivity());
        recyclerView.setAdapter(myContactAdapter);
        recyclerView.setUpAsList();
        myContactAdapter.setOnItemClickListener((view, position) -> onItemContactClicked(myContactAdapter.getData().get(position)));

        BenihWorker.pluck()
                .doInComputation(() -> contacts = generateDummyData())
                .subscribe(o -> {
                    myContactAdapter.add(contacts);
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                });
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        Timber.d(importantContact.toString());
    }

    private void resolveUser(Bundle savedInstanceState) {
        user = getArguments().getParcelable(KEY_USER);

        if (user == null && savedInstanceState != null) {
            user = savedInstanceState.getParcelable(KEY_USER);
        }

        if (user == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onRefresh() {

    }

    private List<ImportantContact> generateDummyData() {
        List<ImportantContact> contacts = new ArrayList<>();
        List<ImportantContact> bookmarkedContacts = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            ImportantContact importantContact = new ImportantContact();
            importantContact.setContactId(i + "");
            importantContact.setBookmarked((BenihUtils.randInt(1, i + 2) % (i + 1)) == 0);
            importantContact.setName(PasswordUtils.generatePassword().substring(0, 8) + " "
                                             + PasswordUtils.generatePassword().substring(8, 12));
            importantContact.setAvgRate((double) BenihUtils.randInt(1, 5) / (double) BenihUtils.randInt(1, 2));
            importantContact.setUserId(PasswordUtils.generatePassword().substring(0, 8));

            if (importantContact.isBookmarked()) {
                bookmarkedContacts.add(importantContact);
            } else {
                contacts.add(importantContact);
            }
        }

        Collections.sort(contacts, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));
        Collections.sort(bookmarkedContacts, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));
        bookmarkedContacts.addAll(contacts);
        return bookmarkedContacts;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_USER, user);
    }
}
