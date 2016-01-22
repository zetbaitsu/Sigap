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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.presenter.EmergencyPresenter;
import id.satusatudua.sigap.presenter.ImportantContactPresenter;
import id.satusatudua.sigap.ui.adapter.ContactAdapter;
import id.satusatudua.sigap.ui.adapter.HelperAdapter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.KeyboardUtil;
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
public class EmergencyActivity extends BenihActivity implements EmergencyPresenter.View,
        ImportantContactPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.et_search) EditText searchField;
    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerViewContact;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator) SectionTitleIndicator indicator;

    private EmergencyPresenter presenter;
    private ProgressDialog progressDialog;
    private ContactAdapter adapter;
    private HelperAdapter helperAdapter;
    private ImportantContactPresenter importantContactPresenter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_emergency;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new ContactAdapter(this);
        recyclerViewContact.setUpAsList();
        recyclerViewContact.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> onItemContactClicked(adapter.getData().get(position)));
        fastScroller.setRecyclerView(recyclerViewContact);
        recyclerViewContact.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);

        helperAdapter = new HelperAdapter(this);
        helperAdapter.add(transformReporter());
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

        importantContactPresenter = new ImportantContactPresenter(this);
        if (savedInstanceState == null) {
            new Handler().postDelayed(() -> importantContactPresenter.loadContacts(), 800);
        } else {
            importantContactPresenter.loadState(savedInstanceState);
        }
    }

    private CandidateHelper transformReporter() {
        CandidateHelper reporter = new CandidateHelper();
        reporter.setCandidate(CacheManager.pluck().getCurrentUser());
        reporter.setCandidateId(CacheManager.pluck().getCurrentUser().getUserId());
        reporter.setStatus(CandidateHelper.Status.MENOLONG);
        return reporter;
    }

    private void onItemHelperClicked(CandidateHelper candidateHelper) {
        startActivity(ProfileActivity.generateIntent(this, candidateHelper.getCandidate()));
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + importantContact.getPhoneNumber().trim()));
        startActivity(intent);
    }

    @OnClick(R.id.i_am_save)
    public void iAmSave() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Klarifikasi")
                .setMessage("Apakah kamu yakin kamu telah selamat?")
                .setPositiveButton("YA, Saya Selamat", (dialog, which) -> {
                    startActivity(CloseCaseActivity.generateIntent(this, presenter.getTheCase(), presenter.getHelpers()));
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

    @OnTextChanged(R.id.et_search)
    public void instantFilter(CharSequence keyword) {
        importantContactPresenter.filter(keyword.toString());
    }

    @OnClick(R.id.icon_search)
    public void search() {
        importantContactPresenter.filter(searchField.getText().toString());
        KeyboardUtil.hideKeyboard(this, searchField);
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
    public void showEmergencyLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan tunggu...");
        progressDialog.show();
    }

    @Override
    public void dismissEmergencyLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(recyclerViewContact, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void dismissLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        importantContactPresenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showContacts(List<ImportantContact> contacts) {
        adapter.clear();
        adapter.add(contacts);
        recyclerViewContact.scrollToPosition(0);
    }

    @Override
    public void showFilteredContacts(List<ImportantContact> contacts) {
        adapter.clear();
        adapter.add(contacts);
        recyclerViewContact.scrollToPosition(0);
    }

    @Override
    public void onRefresh() {
        importantContactPresenter.loadContacts();
    }
}
