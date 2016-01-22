package id.satusatudua.sigap.presenter;

import android.os.Bundle;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.satusatudua.sigap.data.api.FirebaseApi;
import id.satusatudua.sigap.data.local.CacheManager;
import id.satusatudua.sigap.data.model.ImportantContact;
import id.satusatudua.sigap.data.model.Review;
import id.satusatudua.sigap.data.model.User;
import id.satusatudua.sigap.util.RxFirebase;
import id.zelory.benih.presenter.BenihPresenter;
import rx.Observable;
import timber.log.Timber;

/**
 * Created on : January 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class DetailContactPresenter extends BenihPresenter<DetailContactPresenter.View> {

    private ImportantContact contact;
    private User currentUser;
    private List<Review> reviews;

    public DetailContactPresenter(View view, ImportantContact contact) {
        super(view);
        this.contact = contact;
        this.currentUser = CacheManager.pluck().getCurrentUser();
        reviews = new ArrayList<>();
    }

    public void loadCreator() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().users(contact.getUserId()))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .subscribe(user -> {
                    contact.setUser(user);
                    if (view != null) {
                        view.showCreator(user);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data pembuat kontak!");
                        view.dismissLoading();
                    }
                });
    }

    public void loadMyReview() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("reviews").child(contact.getContactId()).child(currentUser.getUserId()))
                .map(dataSnapshot -> {
                    Review review = dataSnapshot.getValue(Review.class);
                    review.setUserId(dataSnapshot.getKey());
                    return review;
                })
                .subscribe(review -> {
                    contact.setMyRate(review.getRate());
                    if (view != null) {
                        view.showMyReview(review);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.dismissLoading();
                    }
                });
    }

    public void sendReview(int rate, String title, String description) {
        view.showLoading();
        Firebase api = FirebaseApi.pluck().getApi();

        Map<String, Object> review = new HashMap<>();
        review.put("date", new Date().getTime());
        review.put("rate", rate);
        review.put("title", title);
        review.put("description", description);

        Map<String, Object> data = new HashMap<>();
        data.put("reviews/" + contact.getContactId() + "/" + currentUser.getUserId(), review);

        api.updateChildren(data, (firebaseError, firebase) -> {
            if (firebaseError == null) {
                if (view != null) {
                    Review review1 = new Review();
                    review1.setUser(currentUser);
                    review1.setTitle(title);
                    review1.setDescription(description);
                    review1.setDate(new Date((Long) review.get("date")));
                    review1.setUserId(currentUser.getUserId());
                    review1.setRate(rate);

                    contact.setMyRate(rate);

                    view.onReviewAdded(review1);
                    view.dismissLoading();
                }
            } else {
                Timber.e(firebaseError.getMessage());
                if (view != null) {
                    view.showError("Gagal mengirimkan data review anda!");
                    view.dismissLoading();
                }
            }
        });
    }

    public void loadReviews() {
        view.showLoading();
        RxFirebase.observeOnce(FirebaseApi.pluck().getApi().child("reviews").child(contact.getContactId()))
                .flatMap(dataSnapshot -> Observable.from(dataSnapshot.getChildren()))
                .map(dataSnapshot -> {
                    Review review = dataSnapshot.getValue(Review.class);
                    review.setUserId(dataSnapshot.getKey());
                    return review;
                })
                .doOnNext(review -> {
                    int x = reviews.indexOf(review);
                    if (x >= 0) {
                        reviews.set(x, review);
                    } else {
                        reviews.add(review);
                    }
                })
                .flatMap(review -> RxFirebase.observeOnce(FirebaseApi.pluck().users(review.getUserId())))
                .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                .doOnNext(user -> {
                    int size = reviews.size();
                    for (int i = 0; i < size; i++) {
                        if (reviews.get(i).getUserId().equals(user.getUserId())) {
                            reviews.get(i).setUser(user);
                        }
                    }
                })
                .toList()
                .subscribe(users -> {
                    if (view != null) {
                        view.showReviews(reviews);
                        view.dismissLoading();
                    }
                }, throwable -> {
                    Timber.e(throwable.getMessage());
                    if (view != null) {
                        view.showError("Gagal memuat data ulasan!");
                        view.dismissLoading();
                    }
                });
    }

    @Override
    public void saveState(Bundle bundle) {

    }

    @Override
    public void loadState(Bundle bundle) {

    }

    public interface View extends BenihPresenter.View {
        void showCreator(User creator);

        void showMyReview(Review review);

        void onReviewAdded(Review review);

        void showReviews(List<Review> reviews);
    }
}
