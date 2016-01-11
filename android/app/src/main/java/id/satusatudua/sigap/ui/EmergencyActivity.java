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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.presenter.EmergencyPresenter;
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
public class EmergencyActivity extends BenihActivity implements EmergencyPresenter.View {

    @Bind(R.id.et_search) EditText searchField;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerViewContact;
    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator) SectionTitleIndicator indicator;

    private List<ImportantContact> contacts;
    private EmergencyPresenter presenter;
    private ProgressDialog progressDialog;
    private HelperAdapter helperAdapter;

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

        helperAdapter = new HelperAdapter(this);
        listHelper.setUpAsHorizontalList();
        listHelper.setAdapter(helperAdapter);
        helperAdapter.setOnItemClickListener((view, position) -> onItemHelperClicked(helperAdapter.getData().get(position)));

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });

        presenter = new EmergencyPresenter(this);
        if (savedInstanceState == null) {
            presenter.init();
        } else {
            presenter.loadState(savedInstanceState);
        }
    }

    private void onItemHelperClicked(CandidateHelper candidateHelper) {
        Timber.d(candidateHelper.toString());
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
                    StateManager.pluck().setState(StateManager.State.LOGGED);
                    dialog.dismiss();
                    onBackPressed();
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

    @Override
    public void onNewHelperAdded(CandidateHelper candidateHelper) {
        if (candidateHelper.getStatus() == CandidateHelper.Status.MENOLAK) {
            helperAdapter.remove(candidateHelper);
        } else {
            helperAdapter.addOrUpdate(candidateHelper);
        }
    }

    @Override
    public void onHelperStatusChanged(CandidateHelper candidateHelper) {
        if (candidateHelper.getStatus() == CandidateHelper.Status.MENOLAK) {
            helperAdapter.remove(candidateHelper);
        } else {
            helperAdapter.addOrUpdate(candidateHelper);
        }
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(recyclerViewContact, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
