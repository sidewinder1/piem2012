(function () {
    "use strict";

    WinJS.Namespace.define("IOHelper",
     {
         FileHelper: WinJS.Class.define(
                function () {
                }, {}, {
                    Write: function (obj, fileName, storageFolder) {
                        return new WinJS.Promise(function (c, e) {
                            try {
                                storageFolder.createFileAsync(fileName,
                                        Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {
                                            Windows.Storage.FileIO.writeTextAsync(file, JSON.stringify(obj), Windows.Storage.Streams.UnicodeEncoding.utf8);
                                            c();
                                        });
                            } catch (ex) {
                                e(ex);
                            }

                        });
                    },

                    WriteInLocal: function (obj, fileName) {
                        return new WinJS.Promise(function (c, e) {
                            try {
                                Windows.Storage.ApplicationData.current.localFolder.createFileAsync(fileName,
                                    Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {
                                        Windows.Storage.FileIO.writeTextAsync(file, JSON.stringify(obj), Windows.Storage.Streams.UnicodeEncoding.utf8);
                                        c();
                                    });
                            } catch (ex) {
                                e(ex);
                            }

                        });
                    },

                    Read: function (fileName, storageFolder) {
                        return new WinJS.Promise(function (c, e) {
                            try {
                                storageFolder.createFileAsync(fileName,
                                    Windows.Storage.CreationCollisionOption.openIfExists).then(function (file) {
                                        Windows.Storage.FileIO.readTextAsync(file).then(function (contents) {
                                            c(contents);
                                        });
                                    });
                            } catch (ex) {
                                e(ex);
                            }

                        });
                    },

                    ReadInLocal: function (fileName) {

                        return new WinJS.Promise(function (c, e) {
                            try {
                                Windows.Storage.ApplicationData.current.localFolder.createFileAsync(fileName,
                                Windows.Storage.CreationCollisionOption.openIfExists).then(function (file) {
                                    Windows.Storage.FileIO.readTextAsync(file).then(function (contents) {
                                        c(contents);
                                    });
                                });
                            } catch (ex) {
                                e(ex);
                            }
                        });
                    },

                    ReadObjInLocal: function (fileName) {
                        return new WinJS.Promise(function (c, e) {
                            try {
                                Windows.Storage.ApplicationData.current.localFolder.createFileAsync(fileName,
                                Windows.Storage.CreationCollisionOption.openIfExists).then(function (file) {
                                    Windows.Storage.FileIO.readTextAsync(file).then(function (contents) {
                                        c(JSON.parse(contents));
                                    });
                                });
                            } catch (ex) {
                                e(ex);
                            }
                        });
                    }
                })
     });
})();