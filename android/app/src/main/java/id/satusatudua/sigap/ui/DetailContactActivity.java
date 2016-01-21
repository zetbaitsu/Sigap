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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.satusatudua.sigap.R;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.Review;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.presenter.BookmarkPresenter;
import id.satusatudua.sigap.presenter.DetailContactPresenter;
import id.zelory.benih.ui.BenihActivity;
import id.zelory.benih.util.BenihWorker;
import timber.log.Timber;

/**
 * Created on : January 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class DetailContactActivity extends BenihActivity implements DetailContactPresenter.View,
        BookmarkPresenter.View {
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

    private ImportantContact importantContact;
    private User currentUser;
    private int myRating;
    boolean rateAble = true;
    private DetailContactPresenter contactPresenter;
    private ProgressDialog progressDialog;
    private SimpleDateFormat dateFormat;
    private BookmarkPresenter bookmarkPresenter;

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

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        buttonEdit.setVisibility(importantContact.getUserId().equals(currentUser.getUserId()) ? View.VISIBLE : View.GONE);
        buttonBookmark.setImageResource(importantContact.isBookmarked() ? R.drawable.ic_bookmark : R.drawable.ic_bookmark_line);

        name.setText(importantContact.getName());
        rate.setText(String.format("%.1f", importantContact.getAvgRate()));
        totalUserRate.setText(importantContact.getTotalUserRate() + "");
        phone.setText(importantContact.getPhoneNumber());
        address.setText(importantContact.getAddress());
        createdAt.setText(dateFormat.format(importantContact.getCreatedAt()));

        yourName.setText(currentUser.getName());
        setMyRate();

        contactPresenter = new DetailContactPresenter(this, importantContact);

        if (importantContact.getUser() != null) {
            creator.setText(importantContact.getUser().getName());
        } else {
            contactPresenter.loadCreator();
        }
        contactPresenter.loadMyReview();
        contactPresenter.loadReviews();

        bookmarkPresenter = new BookmarkPresenter(this);
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
        rootRating.setVisibility(View.GONE);
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
        if (importantContact.isBookmarked()) {
            bookmarkPresenter.unBookmark(importantContact);
        } else {
            bookmarkPresenter.bookmark(importantContact);
        }
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
            contactPresenter.sendReview(myRating, title.getText().toString(), description.getText().toString());
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

    private void bindReviews(List<Review> reviews) {
        int size = reviews.size();
        rootReview.removeAllViews();
        for (int i = 0; i < size; i++) {
            bindReview(reviews.get(i));
        }
    }

    private void bindReview(Review review) {
        View reviewView = LayoutInflater.from(this).inflate(R.layout.item_review, rootReview, false);
        TextView title = (TextView) reviewView.findViewById(R.id.title);
        title.setText(review.getTitle());
        TextView rate = (TextView) reviewView.findViewById(R.id.rate);
        rate.setText(review.getRate() + "");
        TextView dateAndAuthor = (TextView) reviewView.findViewById(R.id.date_and_author);
        dateAndAuthor.setText("pada " + dateFormat.format(review.getDate()) + " oleh " + review.getUser().getName());
        TextView content = (TextView) reviewView.findViewById(R.id.content);
        content.setText(review.getDescription());
        rootReview.addView(reviewView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONTACT, importantContact);
    }

    @Override
    public void showCreator(User creator) {
        this.creator.setText(creator.getName());
        importantContact.setUser(creator);
    }

    @Override
    public void showMyReview(Review review) {
        importantContact.setMyRate(review.getRate());
        setMyRate();
    }

    @Override
    public void onReviewAdded(Review review) {
        rateAble = false;
        bindReview(review);
        importantContact.setTotalRate(importantContact.getTotalRate() + review.getRate());
        importantContact.setTotalUserRate(importantContact.getTotalUserRate() + 1);
        importantContact.setAvgRate(importantContact.getTotalRate() / importantContact.getTotalUserRate());
        rate.setText(String.format("%.1f", importantContact.getAvgRate()));
        totalUserRate.setText(importantContact.getTotalUserRate() + "");
    }

    @Override
    public void showReviews(List<Review> reviews) {
        bindReviews(reviews);

        BenihWorker.pluck().doInNewThread(() -> updateInfo(reviews))
                .subscribe(o -> {
                    rate.setText(String.format("%.1f", importantContact.getAvgRate()));
                    totalUserRate.setText(importantContact.getTotalUserRate() + "");
                }, throwable -> Timber.e(throwable.getMessage()));
    }

    private void updateInfo(List<Review> reviews) {
        int size = reviews.size();

        importantContact.setTotalUserRate(size);
        long totalRate = 0;
        for (int i = 0; i < size; i++) {
            totalRate += reviews.get(i).getRate();
        }
        importantContact.setTotalRate(totalRate);
        importantContact.setAvgRate(totalRate / size);
    }

    @Override
    public void showError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(rootReview, errorMessage, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.color.colorAccent);
        snackbar.show();
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Silahkan tunggu...");
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void onBookmarked(ImportantContact contact) {
        importantContact.setBookmarked(true);
        buttonBookmark.setImageResource(R.drawable.ic_bookmark);
    }

    @Override
    public void onUnBookmark(ImportantContact contact) {
        importantContact.setBookmarked(false);
        buttonBookmark.setImageResource(R.drawable.ic_bookmark_line);
    }
}
