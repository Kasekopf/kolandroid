package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;

public class SingleRequest extends Request {
    private final String[] formNames;
    private final String[] formValues;

    public SingleRequest(String url) {
        this(url, new String[0], new String[0]);
    }

    public SingleRequest(String url, String[] names, String[] values) {
        super(url);

        this.formNames = names;
        this.formValues = values;
    }

    @Override
    protected Connection getConnection(String server) {
        Connection connection = super.getConnection(server);

        for (int i = 0; i < formNames.length; i++) {
            connection.addFormField(formNames[i], formValues[i]);
        }
        connection.disableRedirects();
        return connection;
    }
}
