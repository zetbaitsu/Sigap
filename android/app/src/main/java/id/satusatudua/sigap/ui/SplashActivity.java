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
import id.satusatudua.sigap.data.local.StateManager;
import id.zelory.benih.ui.BenihActivity;

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
    protected int getResourceLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {

        sendBroadcast(new Intent("id.satusatudua.sigap.ACTION_START"));

        switch (StateManager.pluck().getState()) {
            case NEW:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, WelcomeActivity.class)), 1800);
                break;
            case VERIFY_EMAIL:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, VerificationActivity.class)), 1800);
                break;
            case SET_PASSWORD:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, SetPasswordActivity.class)), 1800);
                break;
            case LOGGED:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, TombolActivity.class)), 1800);
                break;
            case LOGOUT:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, LoginActivity.class)), 1800);
                break;
            default:
                new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, WelcomeActivity.class)), 1800);
        }
    }
}
