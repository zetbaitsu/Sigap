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

package id.satusatudua.sigap.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on : January 21, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class CaseReview implements Parcelable {
    private String userId;
    private User user;
    private String status;
    private String review;

    public CaseReview() {

    }

    protected CaseReview(Parcel in) {
        userId = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        status = in.readString();
        review = in.readString();
    }

    public static final Creator<CaseReview> CREATOR = new Creator<CaseReview>() {
        @Override
        public CaseReview createFromParcel(Parcel in) {
            return new CaseReview(in);
        }

        @Override
        public CaseReview[] newArray(int size) {
            return new CaseReview[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeParcelable(user, flags);
        dest.writeString(status);
        dest.writeString(review);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CaseReview && ((CaseReview) o).userId.equals(userId);
    }

    @Override
    public String toString() {
        return "CaseReview{" +
                "userId='" + userId + '\'' +
                ", user=" + user +
                ", status='" + status + '\'' +
                ", review='" + review + '\'' +
                '}';
    }
}
