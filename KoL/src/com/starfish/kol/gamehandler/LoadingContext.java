package com.starfish.kol.gamehandler;

/**
 * An interface back to a progress bar.
 */
public interface LoadingContext {
	public void reportProgress(String page, int current, int total);
	public void complete(String page, boolean error);
	
	public static LoadingContext NONE = new LoadingContext() {
		@Override
		public void reportProgress(String page, int current, int total) {
			//do nothing
		}

		@Override
		public void complete(String page, boolean error) {
			//do nothing
		}
	};
}
