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
import android.util.Log;
import java.util.Arrays;
import java.io.File;

import android.hardware.*;
import info.guardianproject.f5android.*;

public class F5Stego extends CordovaPlugin {

// Constructor
public F5Stego() {
}

public static final String TAG = "op.sec.F5Stego";
public static final String ACTION_ENCODE_IMAGE = "encode";
public static final String ACTION_DECODE_IMAGE = "decode";

StringBuilder builder = new StringBuilder();

float [] history = new float[2];
String [] direction = {"NONE","NONE"};

public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.v(TAG, "Init " + TAG);
}

public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        /*
         ++++++++++++++++ ENCODING INTO IMAGE ++++++++++++++++
           Arguments String dump_dir, String inFileName, String outFileName, String secret_message
         */

        if (action.equals(ACTION_ENCODE_IMAGE)) {

                // Parse arguments. Several cases

                // At least the file name must be specified
                if (args.length() != 4) {
                        callbackContext.error("Argument error. Structure is [dump_dir, inFileName, outFileName, secret_message], args length are\n\n" + String.valueOf(args.length()));
                        return true;
                }

                // Structure of arguments is [filename, embedName, password, quality]
                // Parsing process
                String dumpDir = args.getString(0);
                String inFileName = args.getString(1);
                String outFileName = args.getString(2);
                String secretMsg = args.getString(3);


                // Check input filename extension
                if ((!inFileName.endsWith(".jpg")) && (!inFileName.endsWith(".tif")) && (!inFileName.endsWith(".gif")) &&
                    (!inFileName.endsWith(".bmp")))
                {
                        callbackContext.error("Argument error. Container file is of unrecognized extension. Valid extensions are .jpg, .tif, .gif, .bmp\nName is " + inFileName);
                        return true;
                }

                // Check output filename extension
                if ((outFileName.endsWith(".tif")) || (outFileName.endsWith(".gif")) || (outFileName.endsWith(".bmp"))) {
                        outFileName = outFileName.substring(0, outFileName.lastIndexOf("."));
                }

                if (!outFileName.endsWith(".jpg")) {
                        outFileName = outFileName.concat(".jpg");
                }

                //TODO Check for existence of paths


                Embed e = new Embed(cordova.getActivity(), dumpDir, inFileName, outFileName, secretMsg);
                callbackContext.success("Completed encode test");

        }else if (action.equals(ACTION_DECODE_IMAGE)) {

                if (args.length() != 1) {
                        callbackContext.error("Argument error. Structure is [dump_dir, inFileName, outFileName, secret_message], args length are\n\n" + String.valueOf(args.length()));
                        return true;
                }

                String sourcePath = args.getString(0);


                Extract e = new Extract(cordova.getActivity(), sourcePath);
                String message = null;

                while(message == null){
                  message = e.getResults();
                }

                callbackContext.success(message);

        } else {
                callbackContext.error("Invalid action");
        }

        return true;
}

}
