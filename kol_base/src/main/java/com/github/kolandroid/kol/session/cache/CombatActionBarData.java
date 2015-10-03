package com.github.kolandroid.kol.session.cache;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.util.Logger;

public class CombatActionBarData {
    public CombatActionBarData(ServerReply data) {
        Logger.log("CombatActionBarData", "Loaded: " + data.html);

    }

    public static CombatActionBarData create(ServerReply data) {
        return new CombatActionBarData(data);
    }
}
