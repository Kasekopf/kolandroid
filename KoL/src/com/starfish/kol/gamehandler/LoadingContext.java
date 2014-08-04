package com.starfish.kol.gamehandler;

/**
 * An interface back to a progress bar.
 */
public interface LoadingContext {
	public void start(String page);
	public void complete(String page);
	public void error(String page);
	
	public static LoadingContext NONE = new LoadingContext() {
		@Override
		public void start(String page) {
			// do nothing
		}

		@Override
		public void complete(String page) {
			// do nothing
		}

		@Override
		public void error(String page) {
			// do nothing
		}
	};
}
