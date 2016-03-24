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

package id.satusatudua.sigap.data.api;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.satusatudua.sigap.BuildConfig;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.util.BenihScheduler;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created on : January 22, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public enum CloudImage {
    HARVEST;
    public final static String TRANSFORM = "h_160,w_160,c_limit";
    public final static String MESSAGE_TRANSFROM = "/h_320,w_240,c_limit";

    private Cloudinary cloudinary;

    CloudImage() {
        getCloudinary();
        Timber.tag(CloudImage.class.getSimpleName());
    }

    public static CloudImage pluck() {
        return HARVEST;
    }

    private Observable<Cloudinary> getCloudinary() {
        return RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("cloudImage"))
                .map(dataSnapshot -> {
                    Map<String, String> config = new HashMap<>();
                    config.put("cloud_name", dataSnapshot.child("name").getValue().toString());
                    config.put("api_key", dataSnapshot.child("key").getValue().toString());
                    config.put("api_secret", dataSnapshot.child("secret").getValue().toString());
                    cloudinary = new Cloudinary(config);
                    cloudinary.url().transformation(new Transformation().width(160).height(160).crop("limit"));
                    return cloudinary;
                });
    }

    public Observable<String> upload(Object file, String fileName, boolean isCompressed) {
        if (cloudinary == null) {
            return getCloudinary().flatMap(cloudinary -> uploadToCloud(file, fileName, isCompressed));
        } else {
            return uploadToCloud(file, fileName, isCompressed);
        }
    }

    private Observable<String> uploadToCloud(Object file, String fileName, boolean isCompressed) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if (fileName != null) {
                        Map data = cloudinary.uploader().upload(file, ObjectUtils.asMap("resource_type", "raw",
                                                                                        "public_id", fileName));
                        String url = data.get("url").toString();
                        subscriber.onNext(url);
                        subscriber.onCompleted();
                    } else {
                        Map data = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                        String url = data.get("url").toString();
                        url = url.substring(url.lastIndexOf('/'));
                        if (isCompressed) {
                            url = BuildConfig.CLOUDINARY_PATH + TRANSFORM + url;
                        } else {
                            url = BuildConfig.CLOUDINARY_PATH + url;
                        }
                        subscriber.onNext(url);
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    subscriber.onCompleted();
                }
            }
        }).compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO));
    }
}
