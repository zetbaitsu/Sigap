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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.GuardCandidate;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.presenter.RequestEscortPresenter;
import id.satusatudua.sigap.ui.adapter.GuardCandidateAdapter;
import id.satusatudua.sigap.util.MapUtils;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.view.BenihRecyclerView;
import id.zelory.benih.util.BenihScheduler;

/**
 * Created on : March 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class RequestEscortActivity extends BenihActivity implements RequestEscortPresenter.View {
    private static final int REQUEST_PICK_LOCATION = 25;
    @Bind(R.id.location) TextView locationText;
    @Bind(R.id.recycler_view) BenihRecyclerView recyclerView;

    private Location location = new Location();
    private String address;
    private ProgressDialog progressDialog;
    private GuardCandidateAdapter adapter;
    private RequestEscortPresenter presenter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_request_escort;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        recyclerView.setUpAsList();
        adapter = new GuardCandidateAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            adapter.getData().get(position).setSelected(!adapter.getData().get(position).isSelected());
            adapter.notifyItemChanged(position);
        });

        presenter = new RequestEscortPresenter(this);
        presenter.loadGuardCandidate();
    }

    @OnClick(R.id.button_submit)
    public void submit() {
        List<GuardCandidate> guardCandidates = getSelectedCandidate();
        if (address == null || "".equals(address)) {
            showError("Mohon pilih lokasi terlebih dahulu!");
        } else if (guardCandidates.isEmpty()) {
            showError("Mohon pilih pengawal terlebih dahulu!");
        } else {
            presenter.sendRequest(location, address, guardCandidates);
        }
    }

    private List<GuardCandidate> getSelectedCandidate() {
        List<GuardCandidate> guardCandidates = new ArrayList<>();
        for (GuardCandidate guardCandidate : adapter.getData()) {
            if (guardCandidate.isSelected()) {
                guardCandidates.add(guardCandidate);
            }
        }
        return guardCandidates;
    }

    @OnClick(R.id.button_pick_location)
    public void pickLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), REQUEST_PICK_LOCATION);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            showError("Failed to access locationText!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_LOCATION && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to pick location!");
                return;
            }
            Place place = PlacePicker.getPlace(data, this);
            String address = place.getAddress().toString();
            if (address.trim().isEmpty()) {
                showLoading();
                MapUtils.getAddress(place.getLatLng().latitude, place.getLatLng().longitude)
                        .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.NEW_THREAD))
                        .compose(bindToLifecycle())
                        .subscribe(s -> {
                            this.address = place.getName() + " - " + s;
                            location.setLatitude(place.getLatLng().latitude);
                            location.setLongitude(place.getLatLng().longitude);
                            locationText.setText(this.address);
                            dismissLoading();
                        }, throwable -> {
                            throwable.printStackTrace();
                            showError("Failed to get address!");
                            dismissLoading();
                        });
            } else {
                this.address = place.getName() + " - " + address;
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                locationText.setText(this.address);
            }
        }
    }

    @Override
    public void onGuardCandidateAdded(GuardCandidate guardCandidate) {
        adapter.add(guardCandidate);
    }

    @Override
    public void onRequestSend() {
        startActivity(EscortActivity.generateIntent(this));
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
