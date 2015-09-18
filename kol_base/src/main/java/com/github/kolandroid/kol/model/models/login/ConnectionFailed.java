package com.github.kolandroid.kol.model.models.login;

public enum ConnectionFailed {
    NO_ACCESS("Unable to access KoL servers."),
    STRANGE_ACCESS("Unknown page received from login url.");

    private final String message;

    ConnectionFailed(String message) {
        this.message = message;
    }

    public String getReason() {
        return message;
    }
}
