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
 * Created on : January 09, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class CandidateHelper implements Parcelable {
    private String candidateId;
    private Status status;
    private User candidate;

    public CandidateHelper() {

    }

    protected CandidateHelper(Parcel in) {
        candidateId = in.readString();
        status = Status.valueOf(in.readString());
        candidate = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<CandidateHelper> CREATOR = new Creator<CandidateHelper>() {
        @Override
        public CandidateHelper createFromParcel(Parcel in) {
            return new CandidateHelper(in);
        }

        @Override
        public CandidateHelper[] newArray(int size) {
            return new CandidateHelper[size];
        }
    };

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getCandidate() {
        return candidate;
    }

    public void setCandidate(User candidate) {
        this.candidate = candidate;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CandidateHelper && ((CandidateHelper) o).candidateId.equals(candidateId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(candidateId);
        dest.writeString(status.name());
        dest.writeParcelable(candidate, flags);
    }

    @Override
    public String toString() {
        return "CandidateHelper{" +
                "candidateId='" + candidateId + '\'' +
                ", status=" + status +
                ", candidate=" + candidate +
                '}';
    }

    public enum Status {
        MENUNGGU, MENOLONG, MENOLAK
    }
}
