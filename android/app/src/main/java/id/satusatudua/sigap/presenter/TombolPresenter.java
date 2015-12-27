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

package id.satusatudua.sigap.presenter;

import android.os.Bundle;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoLocation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.Location;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.presenter.BenihPresenter;
import timber.log.Timber;

/**
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */

/**
 * Flow data yang terjadi ketika tombol merah itu ditekan akan dihandle disini
 */
public class TombolPresenter extends BenihPresenter<TombolPresenter.View> {

    public TombolPresenter(TombolPresenter.View view) {
        super(view);
    }

    /**
     * Pembuatan sebuah data kasus akan dihandle di method ini, dijalankan ketika tombol merah ditekan
     */
    public void createCase() {
        //tampilkan proses loading dulu, mudah-mudahan cepat, tergantung koneksi user :v
        view.showLoading();

        //ambil data user yang sedang gunain aplikasi dulu
        User currentUser = CacheManager.pluck().getCurrentUser();
        Location currentLocation = CacheManager.pluck().getUserLocation();

        //ambil instance API, kita bakal melakukan serangkaian input data secara bersamaan ke API
        Firebase api = FirebaseApi.pluck().getApi();

        //generate key untuk data kasus ini nantinya
        String caseKey = api.child("cases").push().getKey();

        //buat data kasus yang terjadi
        Map<String, Object> newCase = new HashMap<>();
        newCase.put("caseId", caseKey);
        newCase.put("userId", currentUser.getUserId());
        newCase.put("date", new Date().getTime());
        newCase.put("open", true);
        newCase.put("latitude", currentLocation.getLatitude());
        newCase.put("longitude", currentLocation.getLongitude());

        //buat pesan awal di grup chat nantinya
        Map<String, Object> message = new HashMap<>();
        message.put("date", new Date().getTime());
        message.put("userId", "Sigap");
        message.put("content", currentUser.getName() + " sedang dalam bahaya, segara bantu dia kawan!");

        //generate data yang nantinya akan kita kirim secara bersamaan ke API
        Map<String, Object> data = new HashMap<>();

        //masukan data kasus baru
        data.put("cases/" + caseKey, newCase);

        //ubah status user menjadi BAHAYA
        data.put("users/" + currentUser.getUserId() + "/status/", "BAHAYA");

        //simpan data bahwa user ini pernah bikin kasus ini
        data.put("userCases/" + currentUser.getUserId() + "/" + caseKey + "/caseId/", caseKey);

        //masukan pesan awal ke kumpulan data
        data.put("caseMessages/" + caseKey + "/initial", message);

        //dan akhirnya kirim data ke server
        sendData(api, caseKey, data, 1);
    }

    private void sendData(Firebase api, String caseKey, Map<String, Object> data, int tryCount) {
        Timber.d("Coba buat kasus percobaan ke " + tryCount);
        api.updateChildren(data, (firebaseError, firebase) -> {
            //kalo terjadi kesalahan
            if (firebaseError != null) {
                Timber.e(firebaseError.getMessage());
                //selama belum sampe 5 kali gagal, kita ulangi lagi aja :v
                if (tryCount < 5) {
                    sendData(api, caseKey, data, tryCount + 1);
                } else if (view != null) { // kalo udah 5 kali masih gagal, hanya Tuhan yang bisa bantu user :v
                    view.showError("Maaf kami gagal memproses pemintaan tolong anda, semoga beruntung :v");
                    view.dismissLoading();
                }
            } else {
                //simpan lagi lokasi kasus di tempat lain untuk keperluan query yg kompleks di kemudian hari
                //kalo gagal ya gpp :v, nanti kapan-kapan bakal di update
                createCaseLocation(caseKey);

                //akhirnya rangkaian proses selesai, loading pun dihilangkan
                if (view != null) {
                    view.onCaseCreated(caseKey);
                    view.dismissLoading();
                }
            }
        });
    }


    private void createCaseLocation(String caseId) {
        Location currentLocation = CacheManager.pluck().getUserLocation();
        FirebaseApi.pluck()
                .caseLocations()
                .setLocation(caseId,
                             new GeoLocation(currentLocation.getLatitude(),
                                             currentLocation.getLongitude()));
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void onCaseCreated(String caseId);
    }
}
