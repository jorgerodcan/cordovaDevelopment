/***
   Copyright (c) 2016 Jorge Rodríguez Canseco

   Permission is hereby granted, free of charge, to any person obtaining a copy of
   this software and associated documentation files (the "Software"), to deal in the
   Software without restriction, including without limitation the rights to use,
   copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
   Software, and to permit persons to whom the Software is furnished to do so,
   subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
   INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
   PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
   HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
   HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
   SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ***/

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLConnection;
import java.io.ByteArrayOutputStream;
import  java.net.InetSocketAddress;


import info.guardianproject.netcipher.proxy.OrbotHelper;

public class Torify extends CordovaPlugin {

// Constructor
public Torify() {
}

public static final String TAG = "op.sec.Torify";
public static final String ACTION_START_TOR = "startTor";
public static final String ACTION_IS_TOR_RUNNING = "isTorRunning";
public static final String ACTION_FETCH_URL = "fetchUrl";
public final static int START_TOR_RESULT = 0x9234;
private final static String PROXY_HOST = "127.0.0.1";
private int mProxyHttp = 8118; // default for Orbot/Tor
private int mProxySocks = 9050; // default for Orbot/Tor

public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init Torify");
}

DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                        Intent orbotIntent = OrbotHelper.getOrbotInstallIntent(cordova.getActivity().getApplicationContext());
                        orbotIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        cordova.getActivity().getApplicationContext().startActivity(orbotIntent);
                        break;

                case DialogInterface.BUTTON_NEGATIVE:
                        return;
                }
        }
};

public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {


        if (action.equals(ACTION_START_TOR)) {
                this.startTor();
                callbackContext.success("Completed");
        }else if (action.equals(ACTION_IS_TOR_RUNNING)) {
                Log.v(TAG, "Action is tor running");
                boolean isRunning = this.getTorStatus();

                if (isRunning) {
                        Log.v(TAG, "Tor is running check macthes");
                        callbackContext.success("Tor is running");
                }else {
                        Log.v(TAG, "Tor not running matches");
                        callbackContext.error("Tor is not running");
                }
        } else if (action.equals(ACTION_FETCH_URL)){
            String url = args.optString(0);
            try{
              String webcode = checkHTTP(url, Proxy.Type.HTTP, PROXY_HOST, mProxyHttp);
              callbackContext.success(webcode);
            } catch (Exception e) {
              callbackContext.error("EXCEPTION\n" + e.getMessage());
            }
        }else{
                callbackContext.error("Invalid action");
        }

        return true;
}

/**
   This function will start the Tor proxy client if it is not already. In case
   Orbot is not installed, this will request the user to install it.
 **/
private boolean startTor(){
        if (!OrbotHelper.isOrbotInstalled(cordova.getActivity())) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());

                                        builder.setMessage("OrBot is not installed in your system. You need to download and install" +
                                                           " OrBot in your phone to access the Tor network.\n\nDo you want to go to OrBot download page now?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                }

                        });
        } else {

                if(!OrbotHelper.isOrbotRunning(cordova.getActivity())) {
                        OrbotHelper.requestShowOrbotStart(cordova.getActivity());
                }
                int a = 1;
        }

        return true;
}

/**
    This function returns the current status of the Tor connection (Orbot)
 **/
public String checkHTTP(String recurl, Proxy.Type pType, String proxyHost, int proxyPort)
throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException,
KeyStoreException, CertificateException, IOException {


        HttpURLConnection con =null;
        URL url = new URL(recurl);
        Proxy proxy = null;
        //Proxy proxy=new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getDefaultHost(),android.net.Proxy.getDefaultPort()));

        //con = (HttpURLConnection) url.openConnection(proxy);
        if (pType == null) {
                // do nothing

        } else if (pType == Proxy.Type.SOCKS) {
                Log.v(TAG, "Using SOCKS proxy");
                proxy=new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost,proxyPort));

        } else if (pType == Proxy.Type.HTTP) {
                Log.v(TAG, "Using HTTP proxy");
                proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,proxyPort));

        }

        if (proxy == null) {
                con = (HttpURLConnection) url.openConnection();
        } else {
                Log.v(TAG, "Opening connection to " + recurl);
                con = (HttpURLConnection) url.openConnection(proxy);
                Log.v(TAG, "Connection to " + recurl + " opened");
        }

        String toReturn = "";
        toReturn += con.getResponseMessage();
        toReturn += "\n\n";
        InputStream in = null;


        try {
                in = new BufferedInputStream(con.getInputStream());
                Log.v(TAG, "Reading input stream");
                Log.v(TAG, String.valueOf(con.getContentLength()));

                toReturn += readStream(in);

        } catch (Exception e) {
          Log.e(TAG, "EXCEPTION : " + e.getMessage());

        }finally {
                con.disconnect();
        }

        return toReturn;

}

private String readStream(InputStream is) {
        try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                        bo.write(i);
                        i = is.read();
                }
                return bo.toString();
        } catch (IOException e) {
                Log.v(TAG, "EXCEPTION: " + e.getMessage());
                return "";
        }
}

private boolean getTorStatus() {
        return OrbotHelper.isOrbotRunning(cordova.getActivity().getApplicationContext());
}

}
