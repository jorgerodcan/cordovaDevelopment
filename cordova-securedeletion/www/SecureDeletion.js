/**
 * @file secureDeletion
 * @author Jorge R. Canseco
 * @copyright Jorge R. Canseco 2016. All rights reserved
 * @license MIT
 */


//TODO
// Add functionality to transactionClose()

var exec = require('cordova/exec');

// Constant variables
var PLUGIN = 'SecureDeletion';
var ACTION_SECUREDELETE = 'SecureDelete';
var ACTION_CHECKFILEEXISTS = "CheckFile";

// Globals
var isTransactionOpen = false;
var fileList = null

module.exports = {

    startTransaction: function() {

        if (isTransactionOpen) {
            return false;
        }

        fileList = {};
        isTransactionOpen = true;
        return true

    },

    addFileToTransaction: function(filePath, __UserSuccess, error) {

        if ((typeof __UserSuccess === 'undefined' || __UserSuccess === null) || (typeof error === 'undefined' || error === null) || !(typeof filePath === 'string')) {
            return false;
        } else {
            if (!isTransactionOpen) {
                error("Transaction is not open.")
                return false;
            }

            var arguments = [filePath]
            var privateSuccess = function(__UserSuccess) {
                return function(filePath, _UserSuccess) {
                    //Add file to array
                    fileList[filePath] = true;
                    __UserSuccess(filePath);
                }
            }
            cordova.exec(privateSuccess(__UserSuccess), error, PLUGIN, ACTION_CHECKFILEEXISTS, arguments);
        }
        return true;
    },

    commitTransaction: function(success, error) {

        if ((typeof success === 'undefined' || success === null) || (typeof error === 'undefined' || error === null))
            return false;

        if (!isTransactionOpen) {
            return false;
        }

        var privateSuccess = function(__UserSuccess) {
            return function(returnedFiles, _UserSuccess) {
                clearTransaction();
                __UserSuccess(returnedFiles);
            }
        }
        cordova.exec(privateSuccess(success), error, PLUGIN, ACTION_SECUREDELETE, fileList);

        //fileList = null;
        //isTransactionOpen = false;
        //return true

    },

    getFileList: function() {

        var result = ""
        for (var key in fileList) {
            result += key + "\n"
        }
        return result;
    }

};
/*
 *   Private function
 */

function clearTransaction() {

    fileList = null;
    isTransactionOpen = false;
    return true
}
