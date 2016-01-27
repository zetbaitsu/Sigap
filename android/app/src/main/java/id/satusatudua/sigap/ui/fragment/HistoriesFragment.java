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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.ActivityHistory;
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.HistoriesPresenter;
import id.satusatudua.sigap.ui.DetailCaseActivity;
import id.satusatudua.sigap.ui.adapter.HistoryAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;

/**
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class HistoriesFragment extends BenihFragment implements HistoriesPresenter.View,
        SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    private HistoriesPresenter presenter;
    private HistoryAdapter adapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_histories;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new HistoryAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setUpAsList();
        adapter.setOnItemClickListener((view, position) -> {
            Case theCase = adapter.getData().get(position).getTheCase();
            User reporter = adapter.getData().get(position).getUser();
            startActivity(DetailCaseActivity.generateIntent(getActivity(), theCase, reporter));
        });

        presenter = new HistoriesPresenter(this);
        if (savedInstanceState == null) {
            new Handler().postDelayed(() -> presenter.loadHistories(), 800);
        } else {
            presenter.loadState(savedInstanceState);
        }
    }

    @Override
    public void showHistories(List<ActivityHistory> histories) {
        adapter.clear();
        adapter.add(histories);
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
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void dismissLoading() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        presenter.loadHistories();
    }
}
