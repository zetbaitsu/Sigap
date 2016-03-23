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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.GuardingPresenter;
import id.satusatudua.sigap.ui.adapter.GuardingPagerAdapter;
import id.satusatudua.sigap.ui.fragment.EscortChatFragment;
import id.satusatudua.sigap.ui.fragment.GuardingLocationFragment;
import id.satusatudua.sigap.ui.fragment.ImportantContactFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;
import timber.log.Timber;

/**
 * Created on : March 23, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class GuardingActivity extends BenihActivity implements GuardingPresenter.View {
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;

    private ProgressDialog progressDialog;
    private Escort escort;
    private User reporter;

    public static Intent generateIntent(Context context) {
        Intent intent = new Intent(context, GuardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_guarding;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        escort = CacheManager.pluck().getLastGuardingEscort();
        reporter = CacheManager.pluck().getLastEscortReporter();
        GuardingPagerAdapter guardingPagerAdapter = new GuardingPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(guardingPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (escort.isClosed()) {
            onEscortClosed(escort);
        } else {
            GuardingPresenter guardingPresenter = new GuardingPresenter(this, escort);
            guardingPresenter.refreshData();
        }
    }

    private List<BenihFragment> getFragments() {
        return Arrays.asList(GuardingLocationFragment.newInstance(reporter, escort),
                             EscortChatFragment.newInstance(reporter, escort),
                             new ImportantContactFragment());
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

    @Override
    public void onEscortClosed(Escort escort) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(reporter.getName().split(" ")[0] + " telah sampai.")
                .setMessage(reporter.getName().split(" ")[0] + " telah sampai pada tujuan dengan selamat, terimakasih atas partisipasi anda.")
                .setPositiveButton("OK", (dialog, which) -> {
                    showLoading();
                    User currentUser = CacheManager.pluck().getCurrentUser();
                    Map<String, Object> data = new HashMap<>();
                    data.put("users/" + currentUser.getUserId() + "/status/", "SIAP");
                    FirebaseApi.pluck().getApi().updateChildren(data, (firebaseError, firebase) -> {
                        if (firebaseError != null) {
                            Timber.e(firebaseError.getMessage());
                            showError("Gagal mengirimkan data konfirmasi!");
                            dismissLoading();
                        } else {
                            currentUser.setStatus(User.Status.SIAP);
                            CacheManager.pluck().cacheCurrentUser(currentUser);
                            StateManager.pluck().setState(StateManager.pluck().recoveryState());
                            Intent intent = new Intent(this, TombolActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            dismissLoading();
                        }
                    });
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.show();
    }
}
