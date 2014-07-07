package com.starfish.kol.android.game.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.starfish.kol.android.R;
import com.starfish.kol.android.game.BaseGameFragment;
import com.starfish.kol.android.game.GameFragment;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.util.Regex;

@SuppressLint("ValidFragment")
public class WebFragment<E extends WebModel> extends BaseGameFragment<Void, E>{
	private WebView web;
	private final static Regex BODY_TAG = new Regex("<body[^>]*?>");
	
	public WebFragment() {
		super(R.layout.fragment_web_screen);
	}

	protected WebFragment(int layout) {
		super(layout);
	}
		
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void disableHardwareAcceleration(WebView web) {
		//https://code.google.com/p/android/issues/detail?id=17352
	    web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

    class JavaScriptInterface {
    	@android.webkit.JavascriptInterface
        public void debug(String text) {
        	Log.i("WebFragment Form", "Debug: " + text);
        }
    	
    	@android.webkit.JavascriptInterface
        public void processFormData(String formData) {
        	Log.i("WebFragment Form", "Res: " + formData);
    		getModel().makeRequest(formData);
        }
    }
    
	private class KoLWebViewClient extends WebViewClient
	{
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	if(url.startsWith("data:text/html"))
	    		return true;
	    	
	    	url = url.replace("reallyquitefake/", "");
	    	Log.i("WebFragment", "Request made to " + url);
	    	if(!getModel().makeRequest(url)) {
	    		Log.i("WebFragment", "Externel request: " + url);
	    		
	    		// Otherwise, the link is not for a kol page; launch an external activity
	            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	            startActivity(intent);
	            return true;
	    	}
	    	
	    	return true;
	    }
	}

	public void updateModel(E base) {
		if(web == null) {
			this.setArguments(GameFragment.getModelBundle(base));
		} else if(web != null) {
			String fixedHtml = BODY_TAG.replaceAll(base.getHTML(), "$0<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.5\">");
			web.loadData(fixedHtml, "text/html", null);
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreateSetup(View view, E base,
			Bundle savedInstanceState) {
		web = (WebView)view.findViewById(R.id.webview);
		
		//Fix the viewport size by inserting a viewport tag
		String fixedHtml = BODY_TAG.replaceAll(base.getHTML(), "$0<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.5\">");
		Log.i("WebFragment", "Loading content of size " + fixedHtml.length());
		
		
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new JavaScriptInterface(), "FORMOUT");
		//web.loadData( fixedHtml, "text/html", null);
        web.loadDataWithBaseURL(base.getURL(), fixedHtml, "text/html", null, null);
        
        Log.i("WebFragment", fixedHtml);
		web.invalidate();
		web.setWebViewClient(new KoLWebViewClient());
		/*
		if (Build.VERSION.SDK_INT >= 11){
			this.disableHardwareAcceleration(web);
		}
		*/
	}

	@Override
	public void onDestroyView() {
		web = null;
		super.onDestroyView();
	}
	@Override
	protected void recieveProgress(Void message) {
		//do nothing
	}
}
