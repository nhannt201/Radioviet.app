package vn.sun.studio.radio.apps;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError er) {
        handler.proceed();
        // Ignore SSL certificate errors
    }


}
