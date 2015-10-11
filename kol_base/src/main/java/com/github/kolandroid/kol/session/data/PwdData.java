package com.github.kolandroid.kol.session.data;

import com.github.kolandroid.kol.session.FixedCacheLine;
import com.github.kolandroid.kol.session.SessionCache;
import com.github.kolandroid.kol.util.Callback;

import java.io.Serializable;

/**
 * A wrapper class to store the current password hash.
 */
public class PwdData implements Serializable {
    // The current password hash
    private final String pwd;

    /**
     * Create a new wrapper for a password hash.
     *
     * @param pwd The password hash to wrap.
     */
    public PwdData(String pwd) {
        this.pwd = pwd;
    }

    /**
     * Get the stored password hash.
     * @return The stored password hash
     */
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

    /**
     * The cache line for a single PwdData
     */
    public static class Cache extends FixedCacheLine<PwdData> {
        @Override
        protected void recompute(SessionCache cache, final Callback<PwdData> complete, final Callback<Void> failure) {
            cache.access(CharacterStatusData.class, new Callback<CharacterStatusData>() {
                @Override
                public void execute(CharacterStatusData item) {
                    String hash = item.getPwdHash();
                    if (hash == null || hash.isEmpty())
                        failure.execute(null);
                    else
                        complete.execute(new PwdData(hash));
                }
            }, failure);
        }

        @Override
        protected Class[] dependencies() {
            return new Class[]{CharacterStatusData.class};
        }
    }
}
