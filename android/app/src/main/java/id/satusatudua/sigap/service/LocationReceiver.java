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

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.SigapApp;
import id.zelory.benih.util.BenihUtils;

/**
 * Created on : November 23, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!BenihUtils.isMyServiceRunning(context, LocationService.class)) {
            context.startService(new Intent(context, LocationService.class));
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean anyLocationProv = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        anyLocationProv |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!anyLocationProv) {
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle("Sigap")
                    .setContentText("Fitur lokasi tidak aktif.")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setStyle(new android.support
                            .v4.app.NotificationCompat
                            .BigTextStyle()
                                      .bigText("Aktifkan fitur lokasi agar Sigap bisa berjalan dengan baik."))
                    .build();

            NotificationManagerCompat.from(SigapApp.pluck().getApplicationContext()).notify(25061993, notification);
        } else {
            NotificationManagerCompat.from(SigapApp.pluck().getApplicationContext()).cancel(25061993);
        }
    }
}
