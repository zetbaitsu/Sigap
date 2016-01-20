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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.presenter.ImportantContactPresenter;
import id.satusatudua.sigap.ui.AddContactActivity;
import id.satusatudua.sigap.ui.DetailContactActivity;
import id.satusatudua.sigap.ui.adapter.ContactAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.KeyboardUtil;
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
public class ImportantContactFragment extends BenihFragment implements
        ImportantContactPresenter.View, SwipeRefreshLayout.OnRefreshListener {
    private static final String KEY_SHOW_FAB = "extra_show_fab";

    @Bind(R.id.et_search) EditText searchField;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerViewContact;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fast_scroller) VerticalRecyclerViewFastScroller fastScroller;
    @Bind(R.id.fast_scroller_section_title_indicator) SectionTitleIndicator indicator;
    @Bind(R.id.fab) FloatingActionButton fab;

    private boolean showFab;
    private ContactAdapter adapter;
    private ImportantContactPresenter importantContactPresenter;

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

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new ContactAdapter(getActivity());
        recyclerViewContact.setUpAsList();
        recyclerViewContact.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> onItemContactClicked(adapter.getData().get(position)));
        fastScroller.setRecyclerView(recyclerViewContact);
        recyclerViewContact.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(indicator);

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            search();
            return true;
        });

        importantContactPresenter = new ImportantContactPresenter(this);
        if (savedInstanceState == null) {
            new Handler().postDelayed(() -> importantContactPresenter.loadContacts(), 800);
        } else {
            importantContactPresenter.loadState(savedInstanceState);
        }
    }

    @OnTextChanged(R.id.et_search)
    public void instantFilter(CharSequence keyword) {
        importantContactPresenter.filter(keyword.toString());
    }

    @OnClick(R.id.fab)
    public void addNewContact() {
        startActivity(new Intent(getActivity(), AddContactActivity.class));
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        if (showFab) {
            startActivity(DetailContactActivity.generateIntent(getActivity(), importantContact));
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + importantContact.getPhoneNumber().trim()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.icon_search)
    public void search() {
        importantContactPresenter.filter(searchField.getText().toString());
        KeyboardUtil.hideKeyboard(getActivity(), searchField);
    }

    @Override
    public void onNewContactAdded(ImportantContact importantContact) {
        adapter.addOrUpdate(importantContact);
    }

    @Override
    public void showFilteredContacts(List<ImportantContact> contacts) {
        adapter.clear();
        adapter.add(contacts);
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
    public void onRefresh() {
        importantContactPresenter.loadContacts();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        importantContactPresenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
