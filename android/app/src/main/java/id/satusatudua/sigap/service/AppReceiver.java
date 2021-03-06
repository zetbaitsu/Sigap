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

package id.satusatudua.sigap.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.satusatudua.sigap.data.local.CacheManager;
import id.zelory.benih.util.BenihUtils;

/**
 * Created on : December 25, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CacheManager.pluck().getUserLocation() != null) {
            if (!BenihUtils.isMyServiceRunning(context, NearbyService.class)) {
                context.startService(new Intent(context, NearbyService.class));
            }

            if (!BenihUtils.isMyServiceRunning(context, EmergencyService.class)) {
                context.startService(new Intent(context, EmergencyService.class));
            }

            if (!BenihUtils.isMyServiceRunning(context, NotificationService.class)) {
                context.startService(new Intent(context, NotificationService.class));
            }
        }
    }
}
