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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.local.StateManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.data.model.UserTrusted;
import id.satusatudua.sigap.event.RemoveTrustedUserClick;
import id.satusatudua.sigap.presenter.TrustedUserPresenter;
import id.satusatudua.sigap.ui.adapter.TrustedUserAdapter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihBus;
import id.zelory.benih.util.KeyboardUtil;
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
    @Bind(R.id.tv_count_contact) TextView trustedCounter;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;
    @Bind(R.id.toolbar) Toolbar toolbar;

    private TrustedUserPresenter presenter;
    private ProgressDialog progressDialog;
    private TrustedUserAdapter adapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_trusted_user;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        BenihBus.pluck().receive().subscribe(o -> {
            if (o instanceof RemoveTrustedUserClick) {
                presenter.removeTrustedUser(((RemoveTrustedUserClick) o).getUserTrusted());
            }
        }, throwable -> Timber.e(throwable.getMessage()));

        setSupportActionBar(toolbar);

        recyclerView.setUpAsList();
        adapter = new TrustedUserAdapter(this);
        recyclerView.setAdapter(adapter);

        presenter = new TrustedUserPresenter(this);
        presenter.loadTrustedUser();
    }

    @OnClick(R.id.button_add_contact)
    public void addTrustedUser() {
        String email = this.email.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email.setError("Mohon masukan alamat email yang valid!");
        } else if (email.equalsIgnoreCase(CacheManager.pluck().getCurrentUser().getEmail())) {
            this.email.setError("Mohon masukan alamat email selain email anda sendiri!");
        } else {
            presenter.searchUser(email);
            KeyboardUtil.hideKeyboard(this, this.email);
            this.email.setText("");
        }
    }

    @OnClick(R.id.button_save)
    public void save() {
        if (adapter.getData().size() < 3) {
            showError("Anda harus memasukan minimal 3 kontak!");
        } else if (notAllAccepted()) {
            showError("Akun yang mengkonfirmasi belum sampai 3!");
        } else {
            StateManager.pluck().setState(StateManager.State.LOGGED);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private boolean notAllAccepted() {
        int count = 0;
        int size = adapter.getData().size();
        for (int i = 0; i < size; i++) {
            if (adapter.getData().get(i).getStatus() == UserTrusted.Status.DITERIMA) {
                count++;
            }
        }
        return count >= 3;
    }

    @Override
    public void onFoundUser(User user) {
        presenter.addTrustedUser(user);
    }

    @Override
    public void onNotFoundUser(String email) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Email Tidak Terdaftar")
                .setMessage("Email yang anda masukan tidak terdaftar, undang teman anda untuk bergabung ke Sigap?")
                .setPositiveButton("YA, Undang Dia", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Saya Mengundang Anda Bergabung Ke Sigap");
                    intent.putExtra(Intent.EXTRA_TEXT, "Halo...!\nSaya membutuhkan anda untuk menjadi kontak yang saya percayai di Sigap, mari bergabung bersama saya di Sigap untuk mewujudkan kehidupan yang lebih Siap dan Tanggap terhadap segala ancaman bahaya. Untuk bergabung ke Sigap silahkan download aplikasinya di url ini [URL].\nTerimakasih banyak atas perhatiannya.\n\nSalam,\n"
                            + CacheManager.pluck().getCurrentUser().getName());
                    startActivity(intent);
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
    public void onTrustedUserAdded(UserTrusted userTrusted) {
        adapter.addOrUpdate(userTrusted);
        trustedCounter.setText("(" + adapter.getData().size() + ")");
    }

    @Override
    public void onTrustedUserRemoved(UserTrusted userTrusted) {
        adapter.remove(userTrusted);
        trustedCounter.setText("(" + adapter.getData().size() + ")");
    }

    @Override
    public void onTrustedUserChanged(UserTrusted userTrusted) {
        adapter.addOrUpdate(userTrusted);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG);
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
