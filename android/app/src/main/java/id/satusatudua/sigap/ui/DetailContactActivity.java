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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.User;
import id.zelory.benih.ui.BenihActivity;

/**
 * Created on : January 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class DetailContactActivity extends BenihActivity {
    private static final String KEY_CONTACT = "extra_contact";

    @Bind(R.id.button_edit) ImageView buttonEdit;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.button_bookmark) ImageView buttonBookmark;
    @Bind(R.id.rate) TextView rate;
    @Bind(R.id.total_user_rate) TextView totalUserRate;
    @Bind(R.id.phone) EditText phone;
    @Bind(R.id.address) EditText address;
    @Bind(R.id.creator) EditText creator;
    @Bind(R.id.created_at) EditText createdAt;
    @Bind(R.id.image_profile) ImageView imageProfile;
    @Bind(R.id.your_name) TextView yourName;
    @Bind(R.id.rate_1) ImageView rate1;
    @Bind(R.id.rate_2) ImageView rate2;
    @Bind(R.id.rate_3) ImageView rate3;
    @Bind(R.id.rate_4) ImageView rate4;
    @Bind(R.id.rate_5) ImageView rate5;
    @Bind(R.id.root_rating) LinearLayout rootRating;
    @Bind(R.id.title) EditText title;
    @Bind(R.id.description) EditText description;
    @Bind(R.id.root_review) LinearLayout rootReview;
    @Bind(R.id.root_more) LinearLayout rootMore;

    private ImportantContact importantContact;
    private User currentUser;
    private int myRating;
    boolean rateAble = true;

    public static Intent generateIntent(Context context, ImportantContact contact) {
        Intent intent = new Intent(context, DetailContactActivity.class);
        intent.putExtra(KEY_CONTACT, contact);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_detail_contact;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        resolveContact(savedInstanceState);
        currentUser = CacheManager.pluck().getCurrentUser();

        buttonEdit.setVisibility(importantContact.getUserId().equals(currentUser.getUserId()) ? View.VISIBLE : View.GONE);

        name.setText(importantContact.getName());
        rate.setText(importantContact.getAvgRate() + "");
        totalUserRate.setText(importantContact.getTotalUserRate() + "");
        phone.setText(importantContact.getPhoneNumber());
        address.setText(importantContact.getAddress());
        createdAt.setText(new SimpleDateFormat("dd/MM/yyyy").format(importantContact.getCreatedAt()));

        yourName.setText(currentUser.getName());
        setMyRate();

        bindReviews(Arrays.asList(1, 2, 3));
    }

    private void setMyRate() {
        switch (importantContact.getMyRate()) {
            case 1:
                rate1();
                rateAble = false;
                break;
            case 2:
                rate2();
                rateAble = false;
                break;
            case 3:
                rate3();
                rateAble = false;
                break;
            case 4:
                rate4();
                rateAble = false;
                break;
            case 5:
                rate5();
                rateAble = false;
                break;
        }
    }

    private void resolveContact(Bundle savedInstanceState) {
        importantContact = getIntent().getParcelableExtra(KEY_CONTACT);

        if (importantContact == null && savedInstanceState != null) {
            importantContact = savedInstanceState.getParcelable(KEY_CONTACT);
        }

        if (importantContact == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.button_edit)
    public void editContact() {

    }

    @OnClick(R.id.button_bookmark)
    public void bookmark() {
        importantContact.setBookmarked(!importantContact.isBookmarked());
        buttonBookmark.setImageResource(importantContact.isBookmarked() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_line);
    }

    @OnClick(R.id.rate_1)
    public void rate1() {
        if (rateAble) {
            myRating = 1;
            rate1.setImageResource(R.drawable.ic_star_filled);
            rate2.setImageResource(R.drawable.ic_star_line);
            rate3.setImageResource(R.drawable.ic_star_line);
            rate4.setImageResource(R.drawable.ic_star_line);

            rootRating.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rate_2)
    public void rate2() {
        if (rateAble) {
            myRating = 2;
            rate1.setImageResource(R.drawable.ic_star_filled);
            rate2.setImageResource(R.drawable.ic_star_filled);
            rate3.setImageResource(R.drawable.ic_star_line);
            rate4.setImageResource(R.drawable.ic_star_line);
            rate5.setImageResource(R.drawable.ic_star_line);

            rootRating.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rate_3)
    public void rate3() {
        if (rateAble) {
            myRating = 3;
            rate1.setImageResource(R.drawable.ic_star_filled);
            rate2.setImageResource(R.drawable.ic_star_filled);
            rate3.setImageResource(R.drawable.ic_star_filled);
            rate4.setImageResource(R.drawable.ic_star_line);
            rate5.setImageResource(R.drawable.ic_star_line);

            rootRating.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rate_4)
    public void rate4() {
        if (rateAble) {
            myRating = 4;
            rate1.setImageResource(R.drawable.ic_star_filled);
            rate2.setImageResource(R.drawable.ic_star_filled);
            rate3.setImageResource(R.drawable.ic_star_filled);
            rate4.setImageResource(R.drawable.ic_star_filled);
            rate5.setImageResource(R.drawable.ic_star_line);

            rootRating.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rate_5)
    public void rate5() {
        if (rateAble) {
            myRating = 5;
            rate1.setImageResource(R.drawable.ic_star_filled);
            rate2.setImageResource(R.drawable.ic_star_filled);
            rate3.setImageResource(R.drawable.ic_star_filled);
            rate4.setImageResource(R.drawable.ic_star_filled);
            rate5.setImageResource(R.drawable.ic_star_filled);

            rootRating.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.button_send)
    public void sendRating() {
        if (title.getText().toString().isEmpty()) {
            title.setError("Mohon isi form ini!");
        } else if (description.getText().toString().isEmpty()) {
            description.setError("Mohon isi form ini!");
        } else {
            rootRating.setVisibility(View.GONE);
            rateAble = false;
        }
    }

    @OnClick(R.id.button_cancel)
    public void cancelRating() {
        myRating = 0;
        rate1.setImageResource(R.drawable.ic_star_line);
        rate2.setImageResource(R.drawable.ic_star_line);
        rate3.setImageResource(R.drawable.ic_star_line);
        rate4.setImageResource(R.drawable.ic_star_line);
        rate5.setImageResource(R.drawable.ic_star_line);
        rootRating.setVisibility(View.GONE);
    }

    private void bindReviews(List<Object> reviews) {
        int size = reviews.size();
        rootReview.removeAllViews();
        for (int i = 0; i < size; i++) {
            View reviewView = LayoutInflater.from(this).inflate(R.layout.item_review, rootReview, false);
            TextView title = (TextView) reviewView.findViewById(R.id.title);
            title.setText("Judul ke " + i);
            TextView rate = (TextView) reviewView.findViewById(R.id.rate);
            rate.setText("5.0");
            TextView dateAndAuthor = (TextView) reviewView.findViewById(R.id.date_and_author);
            dateAndAuthor.setText("pada 15/06/2015 oleh " + currentUser.getName());
            TextView content = (TextView) reviewView.findViewById(R.id.content);
            content.setText("Nah ini review untuk yang ke " + i);
            rootReview.addView(reviewView);
        }

        rootMore.setVisibility(size >= 3 ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.read_more)
    public void readMoreReview() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONTACT, importantContact);
    }
}
