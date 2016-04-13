package com.github.kolandroid.kol.connection;

import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.util.Logger;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Connection {
    private static final String AGENT_NAME = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"; //"AndroidKOL";
    private static final int TIMEOUT = 10000;
    private final ArrayList<String> formFields;
    private final String URLbase;
    private boolean redirect = true;

    public Connection(String url) {
        this.URLbase = url;
        formFields = new ArrayList<>();
    }

    public void addFormField(String element, String value) {
        Iterator<String> i = formFields.iterator();
        while (i.hasNext())
            if (i.next().startsWith(element + '='))
                i.remove();
        formFields.add(element + '=' + ((value == null) ? "" : value));
    }

    private String getBaseURL() {
        if (URLbase.contains("?")) {
            return URLbase.split("\\?")[0];
        } else {
            return URLbase;
        }
    }

    private String getArguments() {
        StringBuilder fieldValues = new StringBuilder("");

        // Note that URLbase might be account.php?, so we have to check this way
        if (URLbase.split("\\?").length > 1) {
            fieldValues.append(URLbase.split("\\?")[1]);
        }

        if (formFields.size() == 0)
            return fieldValues.toString();

        fieldValues.append(formFields.get(0));
        for (int i = 1; i < formFields.size(); i++)
            fieldValues.append('&').append(formFields.get(i));
        return fieldValues.toString();
    }

    public void disableRedirects() {
        redirect = false;
    }

    public ServerReply complete(String cookie) throws ConnectionException {
        try {
            HttpURLConnection connection = this.connect(cookie);
            return new ServerReply(connection);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    public void completeToFile(String cookie, FileOutputStream file, LoadingContext loading) throws ConnectionException {
        try {
            HttpURLConnection connection = this.connect(cookie);
            int length = connection.getContentLength();
            InputStream input = new BufferedInputStream(connection.getInputStream(), 8192);

            byte data[] = new byte[1024];
            int count;
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                loading.progress((int) ((total * 100) / length));
                file.write(data, 0, count);
            }

            file.flush();
            file.close();
            input.close();
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    private HttpURLConnection connect(String cookie) throws IOException {
        String base = getBaseURL();
        boolean doPost = base.contains("POST/");
        if (doPost) {
            base = base.replace("POST/", "");
        } else {
            String args = getArguments();
            if (args.length() > 0) {
                base += "?" + args;
            }
        }

        URL url = new URL(base);
        if (doPost) {
            Logger.log("Connection", "Making POST request to " + url + " [" + getArguments() + "]");
        } else if (!base.contains("newchatmessages.php")) { //filter out the endless new chat message requests
            Logger.log("Connection", "Making GET request to " + url + " [cookie: " + cookie + "]");
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (cookie != null)
            connection.setRequestProperty("Cookie", cookie);
        connection.setDoInput(true);
        connection.setRequestProperty("User-Agent", AGENT_NAME);
        connection.setReadTimeout(TIMEOUT);
        connection.setInstanceFollowRedirects(redirect);


        if (doPost) {
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");

            byte[] postData = getArguments().getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
        } else {
            connection.setUseCaches(true);
            connection.setDoOutput(false);
            /*
            for(Map.Entry<String, List<String>> e : connection.getRequestProperties().entrySet()) {
                for(String res : e.getValue()) {
                    Logger.log("Connection", e.getKey() + ":" + res);
                }
            }
            */
            connection.setRequestMethod("GET");
            connection.connect();
        }
        connection.connect();
        return connection;
    }

    public String getUrl() {
        return URLbase;
    }
}
