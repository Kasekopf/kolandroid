package com.github.kolandroid.kol.session.data;

import java.io.Serializable;

/**
 * A wrapper class to store the current password hash.
 */
public class PwdData implements Serializable {
    // The current password hash
    private final String pwd;

    public PwdData(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof PwdData) {
            return pwd.equals(((PwdData) obj).pwd);
        }
        return false;
    }

    @Override
    public String toString() {
        return "$pwd[" + pwd + "]";
    }
}
