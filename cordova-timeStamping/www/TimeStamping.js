/**
 * @file TimeStamping plugin
 * @author Jorge R. Canseco
 * @copyright Jorge R. Canseco 2016. All rights reserved
 * @license MIT
 */

var exec = require('cordova/exec');

// Constant variables
var PLUGIN = 'TimeStamping';
var ACTION_CREATE_TIMESTAMP = 'createTimeStamp';

module.exports = {

    createTimeStamp: function(filePath, success, error) {
        if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)) {
          // At least the success and error functions must be defined.
          console.log("Function usage is createTimeStamp(pathToFile, successCallback, errorCallback)");
          return false;
        }
        var arguments = [filePath]
        return cordova.exec(success, error, PLUGIN, ACTION_CREATE_TIMESTAMP, arguments);
    }
};
