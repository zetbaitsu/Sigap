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
import id.satusatudua.sigap.data.model.Case;
import id.satusatudua.sigap.data.model.CaseReview;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.DetailCasePresenter;
import id.satusatudua.sigap.ui.MainActivity;
import id.satusatudua.sigap.ui.adapter.CaseReviewAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;

/**
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class DetailCaseFragment extends BenihFragment implements
        SwipeRefreshLayout.OnRefreshListener, DetailCasePresenter.View {
    private static final String KEY_CASE = "extra_case";
    private static final String KEY_REPORTER = "extra_reporter";

    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;

    private Case theCase;
    private User reporter;
    private CaseReviewAdapter adapter;
    private DetailCasePresenter presenter;

    public static DetailCaseFragment newInstance(Case theCase, User reporter) {
        DetailCaseFragment fragment = new DetailCaseFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_CASE, theCase);
        bundle.putParcelable(KEY_REPORTER, reporter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_detail_case;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveTheCase(savedInstanceState);
        resolveReporter(savedInstanceState);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new CaseReviewAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setUpAsList();

        presenter = new DetailCasePresenter(this, theCase, reporter);
        new Handler().postDelayed(() -> presenter.loadReviews(), 800);
    }

    private void resolveTheCase(Bundle savedInstanceState) {
        theCase = getArguments().getParcelable(KEY_CASE);

        if (theCase == null && savedInstanceState != null) {
            theCase = savedInstanceState.getParcelable(KEY_CASE);
        }

        if (theCase == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void resolveReporter(Bundle savedInstanceState) {
        reporter = getArguments().getParcelable(KEY_REPORTER);

        if (reporter == null && savedInstanceState != null) {
            reporter = savedInstanceState.getParcelable(KEY_REPORTER);
        }

        if (reporter == null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        presenter.loadReviews();
    }

    @Override
    public void showReviews(List<CaseReview> reviews) {
        adapter.clear();
        adapter.add(reviews);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG);
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
}
