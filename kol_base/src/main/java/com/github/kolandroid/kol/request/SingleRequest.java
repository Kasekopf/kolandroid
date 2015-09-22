package com.github.kolandroid.kol.request;

import com.github.kolandroid.kol.connection.Connection;


/**
 * A request made to the KoL servers which does NOT follow any redirects.
 */
public class SingleRequest extends Request {
    //The names of values to include with the request
    private final String[] formNames;
    //The values to include with the request
    private final String[] formValues;

    /**
     * Create a new request to the specified url.
     *
     * @param url The url to request.
     */
    public SingleRequest(String url) {
        this(url, new String[0], new String[0]);
    }

    /**
     * Create a new request to the specified url, with arguments.
     * @param url   The url to request.
     * @param names The names of any values to attach
     * @param values The values to attach
     */
    public SingleRequest(String url, String[] names, String[] values) {
        super(url);

        this.formNames = names;
        this.formValues = values;
    }

    /**
     * Get a connection which will not follow redirects.
     * @param server The server to use for the request.
     * @return The connection to use for the request.
     */
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
