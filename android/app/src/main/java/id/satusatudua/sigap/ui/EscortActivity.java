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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.presenter.EscortPresenter;
import id.satusatudua.sigap.ui.adapter.HelpingPagerAdapter;
import id.satusatudua.sigap.ui.fragment.EscortChatFragment;
import id.satusatudua.sigap.ui.fragment.ImportantContactFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EscortActivity extends BenihActivity implements EscortPresenter.View {
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    private ProgressDialog progressDialog;
    private Escort escort;
    private HelpingPagerAdapter helpingPagerAdapter;
    private EscortPresenter presenter;

    public static Intent generateIntent(Context context) {
        Intent intent = new Intent(context, EscortActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_escort;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        escort = CacheManager.pluck().getLastEscort();
        helpingPagerAdapter = new HelpingPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(helpingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        presenter = new EscortPresenter(this);
    }

    private List<BenihFragment> getFragments() {
        return Arrays.asList(EscortChatFragment.newInstance(escort), new ImportantContactFragment());
    }

    @OnClick(R.id.button_danger)
    public void danger() {
        EscortChatFragment chatFragment = (EscortChatFragment) helpingPagerAdapter.getItem(0);
        chatFragment.sendDangerMessage();
    }

    @OnClick(R.id.button_finish)
    public void finishEscort() {
        presenter.finishEscort(escort);
    }

    @Override
    public void onEscortFinished() {
        Intent intent = new Intent(this, TombolActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(String errorMessage) {
        try {
            Snackbar snackbar = Snackbar.make(viewPager, errorMessage, Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }
}
