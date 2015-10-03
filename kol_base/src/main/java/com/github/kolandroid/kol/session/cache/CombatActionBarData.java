package com.github.kolandroid.kol.session.cache;

import com.github.kolandroid.kol.connection.ServerReply;

public class CombatActionBarData {
    public CombatActionBarData(ServerReply data) {

    }

    public static CombatActionBarData create(ServerReply data) {
        return new CombatActionBarData(data);
    }
}
