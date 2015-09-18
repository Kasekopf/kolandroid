package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;

public class SingleRequest extends Request {
    private final String[] formNames;
    private final String[] formVals;

    public SingleRequest(String url) {
        this(url, new String[0], new String[0]);
    }

    public SingleRequest(String url, String[] names, String[] vals) {
        super(url);

        this.formNames = names;
        this.formVals = vals;
    }

    @Override
    protected Connection getConnection(String server) {
        Connection connection = super.getConnection(server);

        for (int i = 0; i < formNames.length; i++) {
            connection.addFormField(formNames[i], formVals[i]);
        }
        connection.disableRedirects();
        return connection;
    }
}
