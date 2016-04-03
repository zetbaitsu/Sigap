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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.ui.adapter.ProfilePagerAdapter;
import id.satusatudua.sigap.ui.fragment.HistoriesFragment;
import id.satusatudua.sigap.ui.fragment.MyContactFragment;
import id.satusatudua.sigap.ui.fragment.OtherHistoriesFragment;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.ui.fragment.BenihFragment;
import id.zelory.benih.ui.view.BenihImageView;
import id.zelory.benih.util.BenihScheduler;
import timber.log.Timber;

/**
 * Created on : January 18, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ProfileActivity extends BenihActivity {
    private static final String KEY_USER = "extra_user";

    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.gender) TextView gender;
    @Bind(R.id.phone) TextView phoneNumber;
    @Bind(R.id.email) TextView emailAddress;
    @Bind(R.id.button_edit) ImageView buttonEdit;
    @Bind(R.id.profile_picture) BenihImageView profilePicture;

    private User user;

    public static Intent generateIntent(Context context, User user) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveUser(savedInstanceState);

        ProfilePagerAdapter profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(profilePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (!user.equals(CacheManager.pluck().getCurrentUser())) {
            buttonEdit.setVisibility(View.GONE);
            emailAddress.setVisibility(View.GONE);
        }

        if (user.equals(CacheManager.pluck().getCurrentUser())) {
            CacheManager.pluck().listenCurrentUser()
                    .compose(BenihScheduler.pluck().applySchedulers(BenihScheduler.Type.IO))
                    .compose(bindToLifecycle())
                    .subscribe(user -> {
                        if (user.getImageUrl() != null) {
                            profilePicture.setRoundedImageUrl(user.getImageUrl());
                        }
                        collapsingToolbarLayout.setTitle(user.getName());
                        collapsingToolbarLayout.invalidate();
                        gender.setText(user.isMale() ? "Laki - laki" : "Perempuan");
                        phoneNumber.setText(user.getPhoneNumber());
                        emailAddress.setText(user.getEmail());
                    }, throwable -> Timber.e(throwable.getMessage()));
        } else {
            if (user.getImageUrl() != null) {
                profilePicture.setRoundedImageUrl(user.getImageUrl());
            }
            collapsingToolbarLayout.setTitle(user.getName());
            gender.setText(user.isMale() ? "Laki - laki" : "Perempuan");
            phoneNumber.setText(user.getPhoneNumber());
            emailAddress.setText(user.getEmail());
        }

    }

    private void resolveUser(Bundle savedInstanceState) {
        user = getIntent().getParcelableExtra(KEY_USER);

        if (user == null && savedInstanceState != null) {
            user = savedInstanceState.getParcelable(KEY_USER);
        }

        if (user == null) {
            Intent intent = new Intent(this, TombolActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.button_edit)
    public void editProfile() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }

    @OnClick(R.id.phone)
    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getPhoneNumber().trim()));
        startActivity(intent);
    }

    @OnClick(R.id.email)
    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + user.getEmail().trim()));
        startActivity(intent);
    }

    private List<BenihFragment> getFragments() {
        if (user.equals(CacheManager.pluck().getCurrentUser())) {
            return Arrays.asList(new HistoriesFragment(), MyContactFragment.newInstance(user));
        }
        return Arrays.asList(OtherHistoriesFragment.newInstance(user), MyContactFragment.newInstance(user));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_USER, user);
    }
}
