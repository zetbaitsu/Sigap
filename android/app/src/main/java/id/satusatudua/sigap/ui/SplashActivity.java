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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.api.FirebaseApi;
import id.zelory.benih.BenihActivity;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class SplashActivity extends BenihActivity {
    @Override
    protected int getActivityView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        sendBroadcast(new Intent("id.satusatudua.sigap.ACTION_START"));

        if (FirebaseApi.pluck().getApi().getAuth() != null) {
            new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, MainActivity.class)), 1800);
        } else {
            new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, LoginActivity.class)), 1800);
        }
    }
}