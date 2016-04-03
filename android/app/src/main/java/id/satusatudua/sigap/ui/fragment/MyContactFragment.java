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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.MyContactPresenter;
import id.satusatudua.sigap.ui.DetailContactActivity;
import id.satusatudua.sigap.ui.TombolActivity;
import id.satusatudua.sigap.ui.adapter.MyContactAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;

/**
 * Created on : January 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class MyContactFragment extends BenihFragment implements
        SwipeRefreshLayout.OnRefreshListener, MyContactPresenter.View {
    private static final String KEY_USER = "extra_user";

    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    private User user;
    private MyContactAdapter myContactAdapter;
    private MyContactPresenter presenter;

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

        presenter = new MyContactPresenter(this);
        new Handler().postDelayed(() -> presenter.loadMyContacts(user), 800);
    }

    private void onItemContactClicked(ImportantContact importantContact) {
        startActivity(DetailContactActivity.generateIntent(getActivity(), importantContact));
    }

    private void resolveUser(Bundle savedInstanceState) {
        user = getArguments().getParcelable(KEY_USER);

        if (user == null && savedInstanceState != null) {
            user = savedInstanceState.getParcelable(KEY_USER);
        }

        if (user == null) {
            Intent intent = new Intent(getActivity(), TombolActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onRefresh() {
        presenter.loadMyContacts(user);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_USER, user);
    }

    @Override
    public void showContacts(List<ImportantContact> contacts) {
        myContactAdapter.clear();
        myContactAdapter.add(contacts);
    }

    @Override
    public void updateContact(ImportantContact contact) {
        myContactAdapter.addOrUpdate(contact);
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoading() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void dismissLoading() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
