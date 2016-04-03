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
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.CandidateHelper;
import id.satusatudua.sigap.presenter.EmergencyPresenter;
import id.satusatudua.sigap.ui.adapter.HelpingPagerAdapter;
import id.satusatudua.sigap.ui.fragment.ChatFragment;
import id.satusatudua.sigap.ui.fragment.ImportantContactFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;

/**
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class EmergencyActivity extends BenihActivity implements EmergencyPresenter.View {

    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    private EmergencyPresenter presenter;
    private ProgressDialog progressDialog;
    private HelpingPagerAdapter helpingPagerAdapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_emergency;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        helpingPagerAdapter = new HelpingPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(helpingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        presenter = new EmergencyPresenter(this);
        if (savedInstanceState == null) {
            presenter.init();
        } else {
            presenter.loadState(savedInstanceState);
        }
    }

    private List<BenihFragment> getFragments() {
        return Arrays.asList(ChatFragment.newInstance(CacheManager.pluck().getLastCase(),
                                                      CacheManager.pluck().getCurrentUser()),
                             new ImportantContactFragment());
    }

    @OnClick(R.id.button_i_am_save)
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

    @Override
    public void onNewHelperAdded(CandidateHelper candidateHelper) {
        try {
            ChatFragment chatFragment = (ChatFragment) helpingPagerAdapter.getItem(0);
            chatFragment.onHelperStatusChanged(candidateHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHelperStatusChanged(CandidateHelper candidateHelper) {

    }

    @Override
    public void showEmergencyLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissEmergencyLoading() {
        progressDialog.dismiss();
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

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        presenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
