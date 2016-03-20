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

import java.util.Date;
import java.util.List;

/**
 * Created on : March 20, 2016
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class Escort implements Parcelable {
    private String escortId;
    private String destination;
    private Date date;
    private double latitude;
    private double longitude;
    private boolean closed;
    private List<GuardCandidate> guardCandidates;

    public Escort() {

    }

    protected Escort(Parcel in) {
        escortId = in.readString();
        destination = in.readString();
        date = new Date(in.readLong());
        latitude = in.readDouble();
        longitude = in.readDouble();
        closed = in.readByte() != 0;
        guardCandidates = in.createTypedArrayList(GuardCandidate.CREATOR);
    }

    public static final Creator<Escort> CREATOR = new Creator<Escort>() {
        @Override
        public Escort createFromParcel(Parcel in) {
            return new Escort(in);
        }

        @Override
        public Escort[] newArray(int size) {
            return new Escort[size];
        }
    };

    public String getEscortId() {
        return escortId;
    }

    public void setEscortId(String escortId) {
        this.escortId = escortId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public List<GuardCandidate> getGuardCandidates() {
        return guardCandidates;
    }

    public void setGuardCandidates(List<GuardCandidate> guardCandidates) {
        this.guardCandidates = guardCandidates;
    }

    @Override
    public String toString() {
        return "Escort{" +
                "escortId='" + escortId + '\'' +
                ", destination='" + destination + '\'' +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", guardCandidates=" + guardCandidates +
                '}';
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(escortId);
        dest.writeString(destination);
        dest.writeLong(date.getTime());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (closed ? 1 : 0));
        dest.writeTypedList(guardCandidates);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Escort && ((Escort) o).escortId.equals(escortId);
    }
}
