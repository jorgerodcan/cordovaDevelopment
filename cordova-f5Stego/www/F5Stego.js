/**
 * @file F5Stego plugin
 * @author Jorge R. Canseco
 * @copyright Jorge R. Canseco 2016. All rights reserved
 * @license MIT
 */

var exec = require('cordova/exec');

// Constant variables
var PLUGIN = 'F5Stego';
var ACTION_ENCODE_IMAGE = 'encode';
var ACTION_DECODE_IMAGE = 'decode';

module.exports = {

    encode: function(dumpDir, inFileName, outFileName, secretMessage, success, error) {


        if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)
            || (typeof dumpDir === 'undefined' || dumpDir === null)
            || (typeof inFileName === 'undefined' || inFileName === null)
            || (typeof outFileName === 'undefined' || outFileName === null)
            || (typeof secretMessage === 'undefined' || secretMessage === null)) {
          // At least the success, error, dumpDir, embedname and outFileName must be specified
          console.log("Function usage is encode(dumpDir, inFileName, outFileName, secret_message, successCallback, errorCallback)");
          return false;
        }

        argdumpDir = dumpDir;
        arginFileName = inFileName;
        argoutFileName = outFileName;
        argsecretMessage = secretMessage;

        var arguments = [argdumpDir, arginFileName, argoutFileName, argsecretMessage]

        return cordova.exec(success, error, PLUGIN, ACTION_ENCODE_IMAGE, arguments);
    },


    decode: function(inputFileName, success, error){

      if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null)
          || (typeof inputFileName === 'undefined' || inputFileName === null)) {

        // At least the success, error, dumpDir, embedname and outFileName must be specified
        console.log("Function usage is encode(dumpDir, inFileName, outFileName, secret_message, successCallback, errorCallback)");
        return false;
      }

      var inF = inputFileName;
      var arguments = [inF];

      return cordova.exec(success,error,PLUGIN, ACTION_DECODE_IMAGE, arguments);

    }
};
