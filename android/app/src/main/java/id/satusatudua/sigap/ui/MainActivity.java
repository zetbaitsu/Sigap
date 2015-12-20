package id.satusatudua.sigap.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.Bind;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.UserPresenter;
import id.satusatudua.sigap.ui.adapter.UserAdapter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihRecyclerView;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */

public class MainActivity extends BenihActivity implements UserPresenter.View {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;

    private UserPresenter userPresenter;
    private ProgressDialog progressDialog;
    private UserAdapter userAdapter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);

        userAdapter = new UserAdapter(this);
        userAdapter.setOnItemClickListener((view, position) -> {
            Snackbar snackbar = Snackbar.make(recyclerView, "Item clicked: " + userAdapter.getData()
                    .get(position).getName(), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.color.colorPrimary);
            snackbar.show();
        });

        userAdapter.setOnLongItemClickListener((view, position) ->
                                                       userPresenter.loadUser(userAdapter.getData().get(position).getUid()));

        recyclerView.setUpAsList();
        recyclerView.setAdapter(userAdapter);

        setupController(savedInstanceState);
        //locationController = new LocationPresenter(this);
    }

    private void setupController(Bundle savedInstanceState) {
        if (userPresenter == null) {
            userPresenter = new UserPresenter(this);
        }

        if (savedInstanceState != null) {
            userPresenter.loadState(savedInstanceState);
        } else {
            userPresenter.loadUsers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                FirebaseApi.pluck().getApi().unauth();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showUsers(List<User> users) {
        userAdapter.clear();
        userAdapter.add(users);
    }

    @Override
    public void showUser(User user) {
        Snackbar snackbar = Snackbar.make(recyclerView, "Show specific user: " + user, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorPrimary);
        snackbar.show();
    }

    @Override
    public void onUserAdded(User user) {
        userAdapter.add(user);
    }

    @Override
    public void onUserChanged(User user) {
        userAdapter.addOrUpdate(user);
    }

    @Override
    public void onUserRemoved(User user) {
        userAdapter.remove(user);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar.make(recyclerView, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        userPresenter.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userPresenter = null;
        progressDialog = null;
        userAdapter.clear();
        userAdapter = null;
    }
}
