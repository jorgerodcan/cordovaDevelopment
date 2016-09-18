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
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
***/

import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class MagicNumberChecker extends CordovaPlugin {

    // Constructor
    public MagicNumberChecker() {}

    public static final String TAG = "op.sec.magNumCheck";
    public static final String ACTION = "checkMagicNumber";
    HashMap<String, byte[]> map = new HashMap<String, byte[]>();

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        // Extensions assignment initialization
        map.put("ico", new byte[]{(byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00});
        map.put("z", new byte[]{(byte)0x1F, (byte)0x9D});
        map.put("bz2", new byte[]{(byte)0x42, (byte)0x5A, (byte)0x68});
        map.put("gif", new byte[]{(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38});
        map.put("tif", new byte[]{(byte)0x49, (byte)0x49, (byte)0x2A, (byte)0x00});
        map.put("tiff", new byte[]{(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38});
        map.put("jpg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF});
        map.put("jpeg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF});
        map.put("exe", new byte[]{(byte)0x4D, (byte)0x5A});
        map.put("zip", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("jar", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("odt", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("ods", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("odp", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("docx", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("xlsx", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("pptx", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("apk", new byte[]{(byte)0x50, (byte)0x4B, (byte)0x03, (byte)0x04});
        map.put("rar", new byte[]{(byte)0x52, (byte)0x61, (byte)0x72, (byte)0x21, (byte)0x1A, (byte)0x07});
        map.put("png", new byte[]{(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A});
        map.put("class", new byte[]{(byte)0xCA, (byte)0xFE, (byte)0xBA, (byte)0xBE});
        map.put("pdf", new byte[]{(byte)0x25, (byte)0x50, (byte)0x44, (byte)0x46});
        map.put("asf", new byte[]{(byte)0x30, (byte)0x26, (byte)0xB2, (byte)0x75});
        map.put("wma", new byte[]{(byte)0x30, (byte)0x26, (byte)0xB2, (byte)0x75});
        map.put("wmv", new byte[]{(byte)0x30, (byte)0x26, (byte)0xB2, (byte)0x75});
        map.put("ogg", new byte[]{(byte)0x4F, (byte)0x67, (byte)0x67, (byte)0x53});
        map.put("ogv", new byte[]{(byte)0x4F, (byte)0x67, (byte)0x67, (byte)0x53});
        map.put("oga", new byte[]{(byte)0x4F, (byte)0x67, (byte)0x67, (byte)0x53});
        map.put("wav", new byte[]{(byte)0x52, (byte)0x49, (byte)0x46, (byte)0x46});
        map.put("avi", new byte[]{(byte)0x52, (byte)0x49, (byte)0x46, (byte)0x46});
        map.put("mp3", new byte[]{(byte)0xFF, (byte)0xFB});
        map.put("__mp3__ID3v2", new byte[]{(byte)0x49, (byte)0x44, (byte)0x43}); // Exception, to be checked below
        map.put("bmp", new byte[]{(byte)0x42, (byte)0x4D});
        map.put("dib", new byte[]{(byte)0x42, (byte)0x4D});
        map.put("flac", new byte[]{(byte)0x66, (byte)0x4C, (byte)0x61, (byte)0x43});
        map.put("mid", new byte[]{(byte)0x4D, (byte)0x54, (byte)0x68, (byte)0x64});
        map.put("midi", new byte[]{(byte)0x4D, (byte)0x54, (byte)0x68, (byte)0x64});
        map.put("doc", new byte[]{(byte)0xD0, (byte)0xCF, (byte)0x11, (byte)0xE0});
        map.put("xls", new byte[]{(byte)0xD0, (byte)0xCF, (byte)0x11, (byte)0xE0});
        map.put("ppt", new byte[]{(byte)0xD0, (byte)0xCF, (byte)0x11, (byte)0xE0});
        map.put("msg", new byte[]{(byte)0xD0, (byte)0xCF, (byte)0x11, (byte)0xE0});
        map.put("dex", new byte[]{(byte)0x64, (byte)0x65, (byte)0x78, (byte)0x0A});
        map.put("tar", new byte[]{(byte)0x75, (byte)0x73, (byte)0x74, (byte)0x61});
        map.put("7z", new byte[]{(byte)0x37, (byte)0x7A, (byte)0xBC, (byte)0xAF});
        map.put("gz", new byte[]{(byte)0x1F, (byte)0x8B});
        Log.v(TAG, "Init magicNumberChecker");

    }

    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

      if (action.equals(ACTION)){

            String filePath = null;
            String extension = null;
            boolean success = false;

            // Parse arguments
            try{
              if(args.length() == 2){
                filePath = args.optString(0);

                if(args.isNull(1)){
                  extension = null;
              }else{
                extension = args.optString(1);
              }

                if(filePath == null){ Log.v(TAG, "ERROR. FilePath is null"); callbackContext.error("The file path cannot be null"); return true; }

              }else{
                Log.v(TAG, "ARGUMENT ERROR");
                callbackContext.error("The file path cannot be null");
                return true;
              }

          }catch(Exception e){
            Log.v(TAG, "ARGUMENT ERROR");
            callbackContext.error("ARGUMENT ERROR");
            return true;
          }

          // Processing

            success = verifyFile(filePath, extension);
            if (success){
              callbackContext.success();
            }else{
              callbackContext.error("Magic number mismatch");
            }



        }else{
            callbackContext.error("Invalid action");
        }

        return true;
    }


    /**
     * This function returns True if the extension of the file and its magic number matches if parameter
     * "extension" is null. If "extension" is not null, fileExtension will check the file magic number
     * according to the provided extension.
     *
     * This function will return true on succesful match, and false otherwise. This function will
     * return false if the file does not has an extension and "extension" parameter is null
     *
     * @param filePath: The path to the file to check
     * @param extension: The extension to check.
     * @return
     */
    private boolean verifyFile(String filePath, String extension){

        String ext = "";

        int i = filePath.lastIndexOf('.');
        if (i > 0)
            ext = filePath.substring(i+1);
            // Else no extension
        else {
            Log.e(TAG, "Error while getting extension");
            return false;
        }


        if (map.containsKey(ext)){
            //Log.v(TAG, "Found extension " + ext + " for " + filePath);
            byte[] mn = map.get(ext);

            try {

                FileInputStream inputStream = new FileInputStream(filePath);
                byte[] toCompare = new byte[mn.length];
                inputStream.read(toCompare);
                inputStream.close();

                for(int x=0; x<mn.length; x++){
                    if(mn[x] != toCompare[x]) {

                        // Special case for mp3
                        //Log.v(TAG, "Fail. Is the file of extension " + ext + " valid?");
                        if (ext.equals("mp3")) {

                            //Log.v(TAG, "Processing special file");
                            mn = map.get("__mp3__ID3v2");
                            inputStream = new FileInputStream(filePath);
                            toCompare = new byte[mn.length];
                            inputStream.read(toCompare);

                            for (int z=0; i<mn.length; i++){
                                if(mn[z] != toCompare[z]) {
                                    Log.v(TAG, "Values do not match for mp3 special");
                                    return false;
                                }
                                Log.v(TAG, "Values MATCH for " + filePath + " with extension " + ext);
                                return true;
                            }
                        }else {
                            Log.v(TAG, "Values do not match for " + filePath + " with extension " + ext);
                            return false;
                        }
                    }
                }

                Log.v(TAG, "Values MATCH for " + filePath + " with extension " + ext);
                return true;

            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                return false;
            } catch (IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                return false;
            }

        }else {
            Log.v(TAG, "Extension " + ext + " not found");
            return false;
        }
    }
}
