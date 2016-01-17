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
 * Created on : January 17, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class ActivityHistory implements Parcelable {
    private Case theCase;
    private User user;
    private boolean fromMe;

    public ActivityHistory() {

    }

    protected ActivityHistory(Parcel in) {
        theCase = in.readParcelable(Case.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        fromMe = in.readByte() != 0;
    }

    public static final Creator<ActivityHistory> CREATOR = new Creator<ActivityHistory>() {
        @Override
        public ActivityHistory createFromParcel(Parcel in) {
            return new ActivityHistory(in);
        }

        @Override
        public ActivityHistory[] newArray(int size) {
            return new ActivityHistory[size];
        }
    };

    public Case getTheCase() {
        return theCase;
    }

    public void setTheCase(Case theCase) {
        this.theCase = theCase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ActivityHistory && ((ActivityHistory) o).theCase.equals(theCase);
    }

    @Override
    public String toString() {
        return "ActivityHistory{" +
                "theCase=" + theCase +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(theCase, flags);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (fromMe ? 1 : 0));
    }
}
