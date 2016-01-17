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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.ui.adapter.ContactAdapter;
import id.satusatudua.sigap.util.PasswordUtils;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihUtils;
import id.zelory.benih.util.BenihWorker;
import id.zelory.benih.util.KeyboardUtil;
import timber.log.Timber;
import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ImportantContactFragment extends BenihFragment {
    private static final String KEY_SHOW_FAB = "extra_show_fab";

    @Bind(R.id.et_search) EditText searchField;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerViewContact;
    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator) SectionTitleIndicator indicator;
    @Bind(R.id.fab) FloatingActionButton fab;

    private List<ImportantContact> contacts;
    private boolean showFab;

    public static ImportantContactFragment newInstance(boolean showFab) {
        ImportantContactFragment fragment = new ImportantContactFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SHOW_FAB, showFab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_important_contact;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            showFab = getArguments().getBoolean(KEY_SHOW_FAB);
        }
        fab.setVisibility(showFab ? View.VISIBLE : View.GONE);

        ContactAdapter adapter = new ContactAdapter(getActivity());
        recyclerViewContact.setUpAsList();
        recyclerViewContact.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> onItemContactClicked(adapter.getData().get(position)));
        fastScroller.setRecyclerView(recyclerViewContact);
        recyclerViewContact.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);

        /*BenihWorker.pluck()
                .doInComputation(() -> contacts = generateDummyData())
                .subscribe(o -> {
                    adapter.add(contacts);
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                });*/

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });
    }

    @OnClick(R.id.fab)
    public void addNewContact() {
        Timber.d("addNewContact clicked");
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        Timber.d(importantContact.toString());
    }

    @OnClick(R.id.icon_search)
    public void search() {
        KeyboardUtil.hideKeyboard(getActivity(), searchField);
    }

    private List<ImportantContact> generateDummyData() {
        List<ImportantContact> contacts = new ArrayList<>();
        List<ImportantContact> bookmarkedContacts = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
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
}
