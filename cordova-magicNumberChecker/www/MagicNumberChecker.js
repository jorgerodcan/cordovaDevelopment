/**
 * @file magicNumberChecker plugin
 * @author Jorge R. Canseco
 * @copyright Jorge R. Canseco 2016. All rights reserved
 * @license MIT
 */

var exec = require('cordova/exec');

// Constant variables
var PLUGIN = 'MagicNumberChecker';
var ACTION = 'checkMagicNumber';

 module.exports = {

  checkMagicNumber: function(filePath, fileExt, success, error) {
     if (arguments.length != 4) {
       // fileExt must be null if not needed, but MUST exist
       console.log("Function needs four arguments");
       return false;
     }

     if ((typeof success === 'undefined' || success === null) && (typeof fail === 'undefined' || fail === null)) {
       return console.log;
     }else{
       var arguments = [filePath, fileExt]
       return cordova.exec(success, error, PLUGIN, ACTION, arguments);
     }
 }
 };
