/**
 * @file magicNumberChecker plugin
 * @author Jorge R. Canseco
 * @copyright Jorge R. Canseco 2016. All rights reserved
 * @license MIT
 */

var exec = require('cordova/exec');

// Constant variables
var PLUGIN = 'Torify';
var ACTION_START_TOR = 'startTor';
var ACTION_IS_TOR_RUNNING = "isTorRunning";
var ACTION_FETCH_URL = "fetchUrl";

module.exports = {

    startTor: function(port, success, error) {
        if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)) {
          // At least the success and error functions must be defined.
          console.log("Function usage is startTor([port], successCallback, errorCallback)");
          return false;
        }
        var arguments = [port]
        return cordova.exec(success, error, PLUGIN, ACTION_START_TOR, arguments);
    },

    isTorRunning: function(success,error) {
      if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)) {
        // At least the success and error functions must be defined.
        console.log("Function usage is isTorRunning(successCallback, errorCallback)");
        return false;
      }
      return cordova.exec(success, error, PLUGIN, ACTION_IS_TOR_RUNNING, []);

    },

    fetchUrl: function(url,success,error) {
      if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)) {
        // At least the success and error functions must be defined.
        console.log("Function usage is isTorRunning(successCallback, errorCallback)");
        return false;
      }
      var arguments = [url]
      return cordova.exec(success, error, PLUGIN, ACTION_FETCH_URL, arguments);

    }
};
