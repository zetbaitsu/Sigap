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
import android.text.Html;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.ConfirmTrustedPresenter;
import id.zelory.benih.ui.BenihActivity;
import timber.log.Timber;

/**
 * Created on : January 13, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ConfirmTrustedOfActivity extends BenihActivity implements
        ConfirmTrustedPresenter.View {
    private static final String KEY_USER_ID = "extra_user_id";

    @Bind(R.id.description) TextView desc;
    private String userId;
    private ProgressDialog progressDialog;
    private ConfirmTrustedPresenter presenter;

    public static Intent generateIntent(Context context, String userId) {
        Intent intent = new Intent(context, ConfirmTrustedOfActivity.class);
        intent.putExtra(KEY_USER_ID, userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_confirm_trusted_of;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveUserId(savedInstanceState);

        presenter = new ConfirmTrustedPresenter(this);
        presenter.loadUser(userId);
    }

    private void resolveUserId(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra(KEY_USER_ID);

        if (userId == null && savedInstanceState != null) {
            userId = savedInstanceState.getString(KEY_USER_ID);
        }

        if (userId == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.button_accept)
    public void accept() {
        presenter.accept(userId);
    }

    @OnClick(R.id.button_decline)
    public void decline() {
        presenter.decline(userId);
    }

    @Override
    public void showUser(User user) {
        Timber.d("Show user: " + user);
        desc.setText(Html.fromHtml("Pengguna bernama <b>" + user.getName() + "</b> dengan email <b>" + user.getEmail() + "</b> memasukan anda kedalam daftar kontak yang ia percaya. Apakah anda bersedia untuk menerimanya?"));
    }

    @Override
    public void onAccepted(String userId) {
        finish();
    }

    @Override
    public void onDeclined(String userId) {
        finish();
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(desc, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
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
