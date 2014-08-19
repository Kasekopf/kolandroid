package com.starfish.kol.android.controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.starfish.kol.android.R;
import com.starfish.kol.android.controller.UpdatableModelController;
import com.starfish.kol.android.screen.Screen;
import com.starfish.kol.android.screen.ScreenSelection;
import com.starfish.kol.model.models.WebModel;
import com.starfish.kol.util.Regex;

public class WebController extends UpdatableModelController<WebModel> {
	/**
	 * Autogenerated by eclipse.
	 */
	private static final long serialVersionUID = -8051419766943400254L;

	private final static Regex BODY_TAG = new Regex("<body[^>]*?>");

	private transient WebView web;

	public WebController(WebModel model) {
		super(model);
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

	public void updateModel(WebModel base) {
		super.updateModel(base);
		if (web != null) {
			String fixedHtml = BODY_TAG
					.replaceAll(base.getHTML(),
							"$0<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.5\">");
			web.loadData(fixedHtml, "text/html", null);
		}
	}

	@Override
	public int getView() {
		if (getModel().isSmall()) {
			return R.layout.dialog_web_screen;
		} else {
			return R.layout.fragment_web_screen;
		}
	}

	@Override
	public void chooseScreen(ScreenSelection choice) {
		if (getModel().isSmall()) {
			choice.displayDialog(this);
		} else {
			choice.displayPrimary(this);
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void connect(View view, WebModel model, final Screen host) {

		WebViewClient client = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("data:text/html"))
					return true;

				url = url.replace("reallyquitefake/", "");
				Log.i("WebFragment", "Request made to " + url);
				if (!getModel().makeRequest(url)) {
					Log.i("WebFragment", "Externel request: " + url);

					// Otherwise, the link is not for a kol page; launch an
					// external activity
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					host.getActivity().startActivity(intent);
				}

				return true;
			}
		};

		web = (WebView) view.findViewById(R.id.webview);
		// Fix the viewport size by inserting a viewport tag
		String fixedHtml = BODY_TAG
				.replaceAll(model.getHTML(),
						"$0<meta name=\"viewport\" content=\"width=device-width, initial-scale=0.5\">");
		Log.i("WebFragment", "Loading content of size " + fixedHtml.length());

		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setUseWideViewPort(true);
		web.getSettings().setJavaScriptEnabled(true);
		web.addJavascriptInterface(new JavaScriptInterface(), "FORMOUT");
		web.loadDataWithBaseURL(model.getURL(), fixedHtml, "text/html", null,
				null);

		web.invalidate();
		web.setWebViewClient(client);

	}

}
