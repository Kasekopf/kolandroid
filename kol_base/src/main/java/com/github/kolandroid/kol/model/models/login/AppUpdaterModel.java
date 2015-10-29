package com.github.kolandroid.kol.model.models.login;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.gamehandler.DataContext;
import com.github.kolandroid.kol.gamehandler.LoadingContext;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.ExternalFileRequest;
import com.github.kolandroid.kol.request.ResponseHandler;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Logger;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.Serializable;

public class AppUpdaterModel extends Model {
    private static final String BASE_URL = "https://kingdomofloathingandroid.blob.core.windows.net/";
    public static final String VERSION_URL = BASE_URL + "version/version.json";

    private static final Gson PARSER = new Gson();

    private final VersionInfo version;

    public AppUpdaterModel(Session s, ServerReply reply) {
        super(s);

        this.version = PARSER.fromJson(reply.html, AppUpdaterModel.VersionInfo.class);
    }

    public boolean updateDetected(DataContext data) {
        if (data.getVersion("app") == 0 || version.app == 0) {
            Logger.log("AppUpdaterModel", "Unable to match version information (" + data.getVersion("app") + ", " + version.app + ")");
            return false;
        }

        return data.getVersion("app") < version.app;
    }

    public void downloadApp(FileOutputStream file, LoadingContext loading) {
        String appUrl = BASE_URL + "app/" + version.app + ".apk";

        ExternalFileRequest fileDownload = new ExternalFileRequest(appUrl, file);
        fileDownload.makeAsync(getSession(), loading, ResponseHandler.NONE);
    }

    public static class VersionInfo implements Serializable {
        public int app;
    }
}
