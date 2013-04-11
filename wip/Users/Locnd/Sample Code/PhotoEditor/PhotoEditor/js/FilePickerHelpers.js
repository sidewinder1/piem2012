(function () {
    "use strict";

    WinJS.Namespace.define("FilePickerHelpers", {        
        // Opens a file picker with appropriate settings and asynchronously returns the selected file
        // Throws an exception if the user clicks cancel (there is no file).
        OpenFileAsync: function () {
            // Attempt to ensure that the view is not snapped, otherwise the picker will not display.
            var viewState = Windows.UI.ViewManagement.ApplicationView.value;
            if (viewState === Windows.UI.ViewManagement.ApplicationViewState.snapped &&
                !Windows.UI.ViewManagement.ApplicationView.tryUnsnap()) {
                throw new Error("File picker cannot display in snapped view.");
            }

            WinJS.log && WinJS.log("Loading image from picker...", "sample", "status");
            var picker = new Windows.Storage.Pickers.FileOpenPicker();
            picker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
            FilePickerHelpers.fillDecoderExtensions(picker.fileTypeFilter);
            return picker.pickSingleFileAsync().then(function (file) {
                if (!file) {
                    throw new Error("User did not select a file.");
                }

                return file;
            });
        },

        // Opens a file picker with appropriate settings and asynchronously returns all of the
        // selected files. Throws an exception if the user clicks cancel (there is no file).
        OpenFilesAsync: function () {
            // Attempt to ensure that the view is not snapped, otherwise the picker will not display.
            var viewState = Windows.UI.ViewManagement.ApplicationView.value;
            if (viewState === Windows.UI.ViewManagement.ApplicationViewState.snapped &&
                !Windows.UI.ViewManagement.ApplicationView.tryUnsnap()) {
                throw new Error("File picker cannot display in snapped view.");
            }

            WinJS.log && WinJS.log("Loading images from picker...", "sample", "status");
            var picker = new Windows.Storage.Pickers.FileOpenPicker();
            picker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
            FilePickerHelpers.fillDecoderExtensions(picker.fileTypeFilter);
            return picker.pickMultipleFilesAsync().then(function (files) {
                if (files.size === 0) {
                    throw new Error("User did not select a file.");
                }

                return files;
            });
        },

        // Opens a file picker with appropriate settings and asynchronously returns a file
        // that the user has selected as the encode destination. Selects a few common
        // encoding formats.
        OpenFileToSaveAsync: function () {
            // Attempt to ensure that the view is not snapped, otherwise the picker will not display.
            var viewState = Windows.UI.ViewManagement.ApplicationView.value;
            if (viewState === Windows.UI.ViewManagement.ApplicationViewState.snapped &&
                !Windows.UI.ViewManagement.ApplicationView.tryUnsnap()) {
                throw new Error("File picker cannot display in snapped view.");
            }

            var picker = new Windows.Storage.Pickers.FileSavePicker();

            // Restrict the user to a fixed list of file formats that support encoding.
            picker.fileTypeChoices.insert("PNG file", [".png"]);
            picker.fileTypeChoices.insert("BMP file", [".bmp"]);
            picker.fileTypeChoices.insert("JPEG file", [".jpg"]);
            picker.defaultFileExtension = ".png";
            picker.suggestedFileName = "Output file";
            picker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;

            return picker.pickSaveFileAsync().then(function (file) {
                if (!file) {
                    throw new Error("User did not select a file.");
                }

                return file;
            });
        },

        // Gets the file extensions supported by all of the bitmap codecs installed on the system.
        // The "collection" argument is of type IVector and implements the append method. The
        // function does not return a value; instead, it populates the collection argument with
        // the file extensions.
        fillDecoderExtensions: function (collection) {
            var enumerator = Windows.Graphics.Imaging.BitmapDecoder.getDecoderInformationEnumerator();

            enumerator.forEach(function (decoderInfo) {
                // Each bitmap codec contains a list of file extensions it supports; get this list
                // and append every element in the list to "collection".
                decoderInfo.fileExtensions.forEach(function (fileExtension) {
                    collection.append(fileExtension);
                });
            });
        },     
    });
})();