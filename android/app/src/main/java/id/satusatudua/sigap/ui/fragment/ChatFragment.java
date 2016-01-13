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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.adapter.HelperAdapter;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihRecyclerView;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ChatFragment extends BenihFragment {

    @Bind(R.id.list_helper) BenihRecyclerView listHelper;
    @Bind(R.id.button_helpers) ImageView buttonHelpers;
    @Bind(R.id.divider) View divider;

    private HelperAdapter helperAdapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        helperAdapter = new HelperAdapter(getActivity());
        listHelper.setUpAsHorizontalList();
        listHelper.setAdapter(helperAdapter);
        helperAdapter.setOnItemClickListener((view, position) -> onItemHelperClicked(helperAdapter.getData().get(position)));
        helperAdapter.add(generateDummyData());
    }

    private void onItemHelperClicked(CandidateHelper candidateHelper) {
        Timber.d(candidateHelper.toString());
    }

    @OnClick(R.id.button_send)
    public void sendMessage() {

    }

    @OnClick(R.id.button_helpers)
    public void toggleHelpers() {
        if (listHelper.getVisibility() == View.VISIBLE) {
            listHelper.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            DrawableCompat.setTint(buttonHelpers.getDrawable(), R.color.divider);
        } else {
            listHelper.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(buttonHelpers.getDrawable(), R.color.colorPrimary);
        }
    }

    private List<CandidateHelper> generateDummyData() {
        List<CandidateHelper> helpers = new ArrayList<>();

        CandidateHelper helper = new CandidateHelper();
        helper.setCandidateId("1");
        helper.setStatus(CandidateHelper.Status.MENOLONG);
        User user = new User();
        user.setUserId("1");
        user.setName("Rya Meyvriska");
        helper.setCandidate(user);
        helpers.add(helper);

        return helpers;
    }
}
