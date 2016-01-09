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

package id.satusatudua.sigap.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.adapter.ContactAdapter;
import id.satusatudua.sigap.ui.adapter.HelperAdapter;
import id.satusatudua.sigap.util.PasswordUtils;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihUtils;
import id.zelory.benih.util.BenihWorker;
import id.zelory.benih.util.KeyboardUtil;
import timber.log.Timber;
import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EmergencyActivity extends BenihActivity {
    private final static String KEY_CASE_ID = "extra_case_id";

    @Bind(R.id.et_search) EditText searchField;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerViewContact;
    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator) SectionTitleIndicator indicator;
    private List<ImportantContact> contacts;

    public static Intent generateIntent(Context context, String caseId) {
        Intent intent = new Intent(context, EmergencyActivity.class);
        intent.putExtra(KEY_CASE_ID, caseId);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_emergency;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        ContactAdapter adapter = new ContactAdapter(this);
        recyclerViewContact.setUpAsList();
        recyclerViewContact.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> onItemContactClicked(adapter.getData().get(position)));
        fastScroller.setRecyclerView(recyclerViewContact);
        recyclerViewContact.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);

        BenihWorker.pluck()
                .doInComputation(() -> contacts = generateDummyData())
                .subscribe(o -> {
                    adapter.add(contacts);
                }, throwable -> {
                    Timber.d(throwable.getMessage());
                });

        HelperAdapter helperAdapter = new HelperAdapter(this);
        listHelper.setUpAsHorizontalList();
        listHelper.setAdapter(helperAdapter);
        helperAdapter.setOnItemClickListener((view, position) -> onItemHelperClicked(helperAdapter.getData().get(position)));
        helperAdapter.add(generateDummyHelpers());

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });
    }

    private void onItemHelperClicked(User helper) {
        Timber.d(helper.toString());
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        Timber.d(importantContact.toString());
    }

    @OnClick(R.id.i_am_save)
    public void iAmSave() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Klarifikasi")
                .setMessage("Apakah kamu yakin kamu telah selamat?")
                .setPositiveButton("YA, Saya Selamat", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("TIDAK", (dialog, which1) -> {
                    dialog.dismiss();
                })
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.primary_text));
        alertDialog.show();
    }

    @OnClick(R.id.icon_search)
    public void search() {
        KeyboardUtil.hideKeyboard(this, searchField);
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

    private List<User> generateDummyHelpers() {
        List<User> helpers = new ArrayList<>();

        User user = new User();
        user.setUserId("1");
        user.setName("Zetra");
        helpers.add(user);

        user = new User();
        user.setUserId("2");
        user.setName("Rya Meyvriska");
        helpers.add(user);

        user = new User();
        user.setUserId("3");
        user.setName("Arif Sholehuddin");
        helpers.add(user);

        user = new User();
        user.setUserId("4");
        user.setName("Fafilia Masrofin");
        helpers.add(user);

        user = new User();
        user.setUserId("5");
        user.setName("Anindia Cyntia");
        helpers.add(user);

        return helpers;
    }
}
