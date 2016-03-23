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

package id.satusatudua.sigap.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.model.Escort;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.TrackingUserPresenter;
import id.zelory.benih.ui.fragment.BenihFragment;

/**
 * Created on : March 23, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class GuardingLocationFragment extends BenihFragment implements OnMapReadyCallback,
        TrackingUserPresenter.View {
    private static final String KEY_ESCORT = "extra_escort";
    private static final String KEY_REPORTER = "extra_reporter";

    private GoogleMap map;
    private Escort escort;
    private User reporter;
    private TrackingUserPresenter trackingUserPresenter;
    private Marker currentReporterMarker;
    private Bitmap userImage;

    public static GuardingLocationFragment newInstance(User reporter, Escort escort) {
        GuardingLocationFragment fragment = new GuardingLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_ESCORT, escort);
        bundle.putParcelable(KEY_REPORTER, reporter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_guarding_location;
    }

    @Override
    protected void onViewReady(@Nullable Bundle savedInstanceState) {
        resolveData(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        trackingUserPresenter = new TrackingUserPresenter(this);
    }

    private void resolveData(Bundle savedInstanceState) {
        escort = getArguments().getParcelable(KEY_ESCORT);

        if (escort == null && savedInstanceState != null) {
            escort = savedInstanceState.getParcelable(KEY_ESCORT);
        }

        if (escort == null) {
            getActivity().finish();
            return;
        }

        reporter = getArguments().getParcelable(KEY_REPORTER);

        if (reporter == null && savedInstanceState != null) {
            reporter = savedInstanceState.getParcelable(KEY_REPORTER);
        }

        if (reporter == null) {
            getActivity().finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        LatLng destination = new LatLng(escort.getLatitude(), escort.getLongitude());
        map.addMarker(new MarkerOptions().position(destination).title(escort.getDestination()));

        trackingUserPresenter.loadAndTrackUserLocation(reporter);
    }

    @Override
    public void onUserMoved(LatLng latLng) {
        if (currentReporterMarker == null) {
            currentReporterMarker = map.addMarker(new MarkerOptions().position(latLng).title(reporter.getName()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            currentReporterMarker.remove();
            currentReporterMarker = map.addMarker(new MarkerOptions().position(latLng).title(reporter.getName()));
        }

        if (userImage == null) {
            loadImage();
        } else {
            currentReporterMarker.setIcon(BitmapDescriptorFactory.fromBitmap(userImage));
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    private void loadImage() {
        Glide.with(getActivity())
                .load(reporter.getImageUrl())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(128, 128) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        userImage = getCircleBitmap(resource);
                        if (currentReporterMarker != null) {
                            currentReporterMarker.setIcon(BitmapDescriptorFactory.fromBitmap(userImage));
                        }
                    }
                });
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                                                  bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

}
