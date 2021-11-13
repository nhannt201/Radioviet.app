package vn.sun.studio.radio.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private WebView simpleWebView;

   // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*Add in Oncreate() funtion after setContentView()*/
// initiate a web view
        simpleWebView=(WebView) findViewById(R.id.webview);
        final WebSettings settings =  simpleWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        //settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          simpleWebView.clearCache(false);
           settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
       // settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19");
        HttpURLConnection http = null;
        simpleWebView.setWebViewClient(new CustomWebViewClient() {
            // autoplay when finished loading via javascript injection
            public void onPageFinished(WebView view, String url) {
                simpleWebView.setVisibility(View.VISIBLE);
                simpleWebView.loadUrl("javascript:(function() { document.querySelectorAll('audio')[0].play(); })()");
            }


        });
        simpleWebView.setFocusable(true);
        simpleWebView.setFocusableInTouchMode(true);
        simpleWebView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
             //   Toast.makeText(MainActivity.this, "hahah", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        simpleWebView.setWebChromeClient(new WebChromeClient());
        //Check connect INternett
        if (isConnected()){
         //   Toast.makeText(MainActivity.this, "Network connection is available", Toast.LENGTH_SHORT).show();


           /* if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }*/
// specify the url of the web page in loadUrl function


            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                trustAllHosts();
                simpleWebView.loadUrl("https://test.phong123.com/?s=0");
            } else {
                simpleWebView.loadUrl("http://test.phong123.com/?s=0");
            }
            // simpleWebView.getSettings().setJavaScriptEnabled(true);

        } else {
            Toast.makeText(MainActivity.this, "Cần có Internet để khởi động ứng dụng. Vui lòng thử lại sao!", Toast.LENGTH_SHORT).show();
          //  simpleWebView.setBackground(getResources().getDrawable(R.mipmap.no_connect));
            final TextView thongbao = findViewById(R.id.thongbao);
            //imgBg.setBackground(getResources().getDrawable(R.mipmap.no_connect));
            thongbao.setVisibility(View.VISIBLE);
           // simpleWebView.setVisibility(View.INVISIBLE);
        }

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        simpleWebView.onPause();
    }

    @Override
    protected void onResume() {
        simpleWebView.onResume();
        super.onResume();
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 25) {
            return;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

}