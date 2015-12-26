package id.satusatudua.sigap.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created on : November 22, 2015
 * Author     : zetbaitsu
 * Name       : Zetra
 * Email      : zetra@mail.ugm.ac.id
 * GitHub     : https://github.com/zetbaitsu
 * LinkedIn   : https://id.linkedin.com/in/zetbaitsu
 */
public class User implements Parcelable {
    private String userId;
    private String email;
    private String name;
    private boolean male;
    private Date birthDate;
    private boolean fromApps;
    private Status status;

    public User() {

    }

    protected User(Parcel in) {
        userId = in.readString();
        email = in.readString();
        name = in.readString();
        male = in.readByte() != 0;
        birthDate = new Date(in.readLong());
        fromApps = in.readByte() != 0;
        status = Status.valueOf(in.readString());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isFromApps() {
        return fromApps;
    }

    public void setFromApps(boolean fromApps) {
        this.fromApps = fromApps;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User && ((User) o).userId.equals(userId);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(name);
        dest.writeByte((byte) (male ? 1 : 0));
        dest.writeLong(birthDate.getTime());
        dest.writeByte((byte) (fromApps ? 1 : 0));
        dest.writeString(status.name());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", male=" + male +
                ", birthDate=" + birthDate +
                ", fromApps=" + fromApps +
                ", status=" + status +
                '}';
    }

    public enum Status {
        SIAP, DIKAWAL, MENGAWAL, BAHAYA, MENOLONG
    }
}
