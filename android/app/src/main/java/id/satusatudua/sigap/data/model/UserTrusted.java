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
 * Created on : December 26, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class UserTrusted implements Parcelable {
    private String userTrustedId;
    private Status status;
    private User user;

    public UserTrusted() {

    }

    protected UserTrusted(Parcel in) {
        userTrustedId = in.readString();
        status = Status.valueOf(in.readString());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<UserTrusted> CREATOR = new Creator<UserTrusted>() {
        @Override
        public UserTrusted createFromParcel(Parcel in) {
            return new UserTrusted(in);
        }

        @Override
        public UserTrusted[] newArray(int size) {
            return new UserTrusted[size];
        }
    };

    public String getUserTrustedId() {
        return userTrustedId;
    }

    public void setUserTrustedId(String userTrustedId) {
        this.userTrustedId = userTrustedId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserTrusted && ((UserTrusted) o).userTrustedId.equals(userTrustedId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userTrustedId);
        dest.writeString(status.name());
        dest.writeParcelable(user, flags);
    }

    @Override
    public String toString() {
        return "UserTrusted{" +
                "userTrustedId='" + userTrustedId + '\'' +
                ", status=" + status +
                ", user=" + user +
                '}';
    }

    public enum Status {
        MENUNGGU, DITERIMA, DITOLAK
    }
}
