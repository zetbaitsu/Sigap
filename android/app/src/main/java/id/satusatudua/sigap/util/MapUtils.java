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

package id.satusatudua.sigap.util;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import id.satusatudua.sigap.SigapApp;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 10, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class MapUtils {

    static {
        Timber.tag(MapUtils.class.getSimpleName());
    }

    public static String getImageUrl(double latitude, double longitude) {
        return "http://maps.google.com/maps/api/staticmap?center="
                + latitude + ","
                + longitude
                + "&zoom=17&size=512x300&sensor=false";
    }

    public static Observable<String> getAddress(double latitude, double longitude) {

        return Observable.create(subscriber -> {

            Geocoder geocoder = new Geocoder(SigapApp.pluck().getApplicationContext(), Locale.getDefault());
            List<Address> addressList;
            String result = "";
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append(", ");
                    }
                    sb.append(address.getLocality()).append(", ");
                    sb.append(address.getCountryName()).append(".");
                    result = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Timber.e(e.getMessage());
            }

            if (!subscriber.isUnsubscribed()) {

                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }
}
