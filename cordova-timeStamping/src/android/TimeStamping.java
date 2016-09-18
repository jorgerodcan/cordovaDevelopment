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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import android.util.Log;

import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.net.URL;


public class TimeStamping extends CordovaPlugin {

// Constructor
public TimeStamping() {
}

public static final String TAG = "op.sec.TimeStamping";
public static final String ACTION_CREATE_TIMESTAMP = "createTimeStamp";
final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init " + TAG);
}

public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        String filePath = null;

        // Parse arguments
        try{
                if(args.length() == 1) {
                        filePath = args.optString(0);

                        if(filePath == null) { Log.v(TAG, "ERROR. FilePath is null"); callbackContext.error("The file path cannot be null"); return true; }

                }else{
                        Log.v(TAG, "ARGUMENT ERROR");
                        callbackContext.error("The file path cannot be null");
                        return true;
                }

        }catch(Exception e) {
                Log.v(TAG, "ARGUMENT ERROR");
                callbackContext.error("ARGUMENT ERROR");
                return true;
        }

        // Processing of the action

        if (action.equals(ACTION_CREATE_TIMESTAMP)) {
                String timeStamp = this.calculateTimeStamp(filePath);
                if (timeStamp == null) {
                        callbackContext.error("FAILED");
                } else {
                        String signedTimeStamp = timestampDeliver(timeStamp);
                        callbackContext.success("Completed " + signedTimeStamp);
                }
        }else{
                callbackContext.error("Invalid action");
        }

        return true;
}

/**
   This function will calculate a hash of the file which is compliant with the
   requirements of the api of http://truetimestamp.org and send it to such
   server. The returned timestamp will be retrieved as a String
 **/
private String calculateTimeStamp(String filePath){

        String timeStamp = "ABCDE";
        byte [] byteShaSum = null;

        MessageDigest hashSum = null;
        try{
                hashSum = MessageDigest.getInstance("SHA-256");

                File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bstream = new BufferedInputStream(fis);

                byteShaSum = hashFile(hashSum, bstream, 16384);
        }catch (Exception e) {
                return null;
        }


        // sdcard/Download/tiffile.tif
        String hexTime = bytesToHex(byteShaSum);
        return hexTime;
}

private static byte [] hashFile(MessageDigest digest, BufferedInputStream in, int bufferSize) throws IOException {
        byte [] buffer = new byte[bufferSize];
        int sizeRead = -1;
        while ((sizeRead = in.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
        }
        in.close();

        byte [] hash = null;
        hash = new byte[digest.getDigestLength()];
        hash = digest.digest();
        return hash;
}

public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
}

public static String timestampDeliver(String hexSum){

        String urlParameters  = "hash="+hexSum+"&force_duplicate=1";
        byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request        = "http://truetimestamp.org/submit.php";
        URL url            = null;
        try{
            url = new URL( request );
        }catch (Exception e){
            return "MALFORMED URL";
        }

        HttpURLConnection conn= null;
        try{
        conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );

        DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
        wr.write( postData );
        }catch (Exception e) {
                return "ERROR IN TIMESTAP DELIVER PARSE";
        }

        StringBuilder sb = null;
        try{
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        sb = new StringBuilder();

                for (int c; (c = in.read()) >= 0; )
                        sb.append((char)c);
        }catch (Exception e) {
                return "ERROR BUILDING THE STRING";
        }

        String response = sb.toString();

        return response;
}

}
