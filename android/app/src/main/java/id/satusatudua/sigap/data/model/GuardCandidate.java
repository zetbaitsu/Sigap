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
 * Created on : March 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class GuardCandidate extends UserTrusted implements Parcelable {
    private boolean selected;
    private GuardingStatus guardingStatus;

    public GuardCandidate() {
        guardingStatus = GuardingStatus.MENUNGGU;
    }

    protected GuardCandidate(Parcel in) {
        super(in);
        selected = in.readByte() != 0;
        guardingStatus = GuardingStatus.valueOf(in.readString());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public GuardingStatus getGuardingStatus() {
        return guardingStatus;
    }

    public void setGuardingStatus(GuardingStatus guardingStatus) {
        this.guardingStatus = guardingStatus;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(guardingStatus.name());
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<GuardCandidate> CREATOR = new Creator<GuardCandidate>() {
        @Override
        public GuardCandidate createFromParcel(Parcel in) {
            return new GuardCandidate(in);
        }

        @Override
        public GuardCandidate[] newArray(int size) {
            return new GuardCandidate[size];
        }
    };

    @Override
    public String toString() {
        return "GuardCandidate{" +
                "selected=" + selected +
                ", guardingStatus=" + guardingStatus +
                '}';
    }

    public enum GuardingStatus {
        MENUNGGU, MENOLONG, MENOLAK
    }
}
