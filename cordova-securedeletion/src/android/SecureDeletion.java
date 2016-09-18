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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class SecureDeletion extends CordovaPlugin {

    // Constructor
    public SecureDeletion() {
    }

    public static final String TAG = "op.sec.securedeletion";
    public static final String ACTION_CHECK_FILE = "CheckFile";
    public static final String ACTION_SECUREDELETE = "SecureDelete";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_CHECK_FILE)) {
            // Test if the argument is properly settled
            if ((args.length() != 1) || (args.isNull(0))) {
                callbackContext.error("Argument error");
                return true;
            }

            try {
                String filePath = args.getString(0);
                addFiles(filePath, callbackContext);

            } catch (Exception e) {
                callbackContext.error("Plugin exception:\n" + e.getMessage());
            }

            return true;

        }else if (action.equals(ACTION_SECUREDELETE)){

            if(args == null){
              callbackContext.error("Argument error");
              return false;
            }

            // Setting arguments
            String arguments[];
            try{
              arguments = new String[args.length()];
              for(int i = 0; i<args.length(); i++)
                  arguments[i] = args.getString(i);

            }catch(Exception e){
              callbackContext.error("Error while parsing the arguments");
              return false;
            }

            this.secureDeletion(callbackContext, arguments);

        }else{
            callbackContext.error("Invalid action");
        }

        return true;
    }


    /*
        Not able to delete directories.
     */
    private boolean addFiles(String filePath, CallbackContext callbackContext){
        File file = new File(filePath);

        if(file.isDirectory()){
            callbackContext.error("The file is a directory.");
            return false;
        }else{
            if(this.checkFile(filePath, callbackContext)){
              callbackContext.success(filePath);
              return true;
            }
            callbackContext.error("Error adding the file");
            return false;
        }
    }

    private boolean checkFile(String filePath, CallbackContext callbackContext){

        File file = new File(filePath);
        if (file.exists()) {
            if (file.canWrite()){
                return true;
            }else{

                callbackContext.error("File " + filePath + " exists but cannot be overwritten");
                return false;
            }
        } else {
            callbackContext.error("File does not exist");
            return false;
        }
    }

    private boolean secureDeletion(CallbackContext callbackContext, String fileList[]){

      //Standard API deletion of files
      for(int i = 0; i<fileList.length; i++){
        if(checkFile(fileList[i], callbackContext)){
          File file = new File(fileList[i]);
          file.delete();
        }else{
          return false;
        }
      }

      //Purging free space
      /*
      FileOutputStream s = new FileOutputStream(filename)
      Channel c = s.getChannel()
      while(xyz)
          c.write(buffer)
      c.force(true)
      s.getFD().sync()
      c.close()
      */
      callbackContext.success();
      return true;
    }
}
