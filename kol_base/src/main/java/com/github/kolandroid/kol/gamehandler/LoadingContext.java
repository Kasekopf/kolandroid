package com.github.kolandroid.kol.gamehandler;

/**
 * An interface back to a progress bar.
 */
public interface LoadingContext {
    LoadingContext NONE = new LoadingContext() {
        @Override
        public void start(String page) {
            // do nothing
        }

        @Override
        public void complete(String page) {
            // do nothing
        }

        @Override
        public void progress(int percent) {
            // do nothing
        }

        @Override
        public void error(String page) {
            // do nothing
        }
    };

    void start(String page);

    void complete(String page);

    void progress(int percent);

    void error(String page);
}
