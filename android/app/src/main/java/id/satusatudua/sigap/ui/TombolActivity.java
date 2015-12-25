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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.skyfishjy.library.RippleBackground;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : December 24, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class TombolActivity extends BenihActivity {

    @Bind(R.id.ripple) RippleBackground rippleBackground;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_tombol;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        rippleBackground.startRippleAnimation();
    }

    @OnClick(R.id.button_emergency)
    public void onEmergency() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setMessage("Kami telah mengirimkan permintaan tolong untuk anda kepada 5 orang yang telah anda percayai, dan 5 orang terdekat dari lokasi anda sekarang.")
                .setPositiveButton("OK", (dialog, which) -> {})
                .show()
                .getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @OnClick(R.id.button_main)
    public void startMain() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
