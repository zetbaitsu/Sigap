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
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.presenter.TrustedUserPresenter;
import id.zelory.benih.ui.BenihActivity;
import timber.log.Timber;

/**
 * Created on : January 11, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class AddTrustedUserActivity extends BenihActivity implements TrustedUserPresenter.View {

    @Bind(R.id.et_add_email) EditText email;
    private TrustedUserPresenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_trusted_user;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        presenter = new TrustedUserPresenter(this);
    }

    @OnClick(R.id.button_add_contact)
    public void addTrustedUser() {
        String email = this.email.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Mohon masukan alamat email yang valid!");
        } else {
            presenter.searchUser(email);
        }
    }

    @Override
    public void onFoundUser(User user) {
        Timber.d("Found user: " + user);
        presenter.addTrustedUser(user);
    }

    @Override
    public void onNotFoundUser(String email) {
        Timber.d("Not found user: " + email);
    }

    @Override
    public void onTrustedUserAdded(UserTrusted userTrusted) {
        Timber.d("Added trusted user: " + userTrusted);
    }

    @Override
    public void onTrustedUserRemoved(UserTrusted userTrusted) {
        Timber.d("Removed trusted user: " + userTrusted);
    }

    @Override
    public void onTrustedUserChanged(UserTrusted userTrusted) {
        Timber.d("Changed trusted user: " + userTrusted);
    }

    @Override
    public void showError(String errorMessage) {
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
