(function () {
    "use strict";

    var Imaging = Windows.Graphics.Imaging;
    WinJS.Namespace.define("HomePageEvents",
        {
            currentImage: {},

            _processOnClicked: function () {
                CanvasProcessing.runFilter(HomePageEvents.currentImage);
            },

            _selectColor: function (args) {
                var item = window.ColorManager.Colors.getAt(args.detail.itemIndex);
                window.ColorManager.Color1 = item.color;
                var color1Dom = document.querySelector(".homepage #ribbonBar #colorContainer #color1");
                color1Dom.style.backgroundColor = item.color;
            },

            _saveFile: function () {
                WinJS.log && WinJS.log("Saving to a new file...", "sample", "status");

                // Keep data in-scope across multiple asynchronous methods.
                var encoderId;
                var filename;
                var stream;

                FilePickerHelpers.OpenFileToSaveAsync().then(function (file) {
                    filename = file.name;

                    switch (file.fileType) {
                        case ".jpg":
                            encoderId = Imaging.BitmapEncoder.jpegEncoderId;
                            break;
                        case ".bmp":
                            encoderId = Imaging.BitmapEncoder.bmpEncoderId;
                            break;
                        case ".png":
                        default:
                            encoderId = Imaging.BitmapEncoder.pngEncoderId;
                            break;
                    }

                    return file.openAsync(Windows.Storage.FileAccessMode.readWrite);
                }).then(function (_stream) {
                    stream = _stream;

                    // BitmapEncoder expects an empty output stream; the user may have selected a
                    // pre-existing file.
                    stream.size = 0;
                    return Imaging.BitmapEncoder.createAsync(encoderId, stream);
                }).then(function (encoder) {
                    var width = window.LayerManager.Current.width;
                    var height = window.LayerManager.Current.height;

                    //// get the image data object
                    //var image = window.Tools.CanvasContext.getImageData(0, 0, 500, 200);
                    //// get the image data values
                    //var length = image.data.length;
                    //// set every fourth value to 50
                    //for (var i = 3; i < length; i += 4) {
                    //    if (image.data[i-2] > 0) image.data[i] = 0;
                    //}

                    //// after the manipulation, reset the data
                    ////image.data = imageData;
                    //// and put the imagedata back to the canvas
                    //window.Tools.CanvasContext.putImageData(image, 0, 0);

                    var outputPixelData = window.Tools.CanvasContext.getImageData(0, 0, width, height);

                    encoder.setPixelData(
                        Imaging.BitmapPixelFormat.rgba8,
                        Imaging.BitmapAlphaMode.straight,
                        width,
                        height,
                        96, // Horizontal DPI
                        96, // Vertical DPI
                        outputPixelData.data
                        );

                    return encoder.flushAsync();
                }).then(function () {
                    WinJS.log && WinJS.log("Saved new file: " + filename, "sample", "status");

                }).then(null, function (error) {
                    WinJS.log && WinJS.log("Failed to save file: " + error.message, "sample", "error");
                }).done(function () {
                    stream && stream.close();
                });
            },

            _openFileCmd: function () {
                var openPicker = new Windows.Storage.Pickers.FileOpenPicker();
                openPicker.viewMode = Windows.Storage.Pickers.PickerViewMode.thumbnail;
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
                // Users expect to have a filtered view of their folders depending on the scenario.
                // For example, when choosing a documents folder, restrict the filetypes to documents for your application.
                openPicker.fileTypeFilter.replaceAll([".png", ".jpg", ".jpeg"]);

                // Open the picker for the user to pick a file
                openPicker.pickSingleFileAsync().then(function (file) {
                    if (file) {
                        // Application now has read/write access to the picked file
                        WinJS.log && WinJS.log("Picked photo: " + file.name, "sample", "status");
                        var title = document.querySelector(".homepage #ribbonBar #titleDiv");
                        title.innerHTML = file.name + " - Photo Editor";

                        HomePageEvents.currentImage = new Image();
                        
                        HomePageEvents.currentImage.src = URL.createObjectURL(file);
                        window.LayerManager.CreateLayer();
                        var context = window.LayerManager.Current.getContext("2d");
                        HomePageEvents.currentImage.onload = function () {
                            window.LayerManager.Current.width = HomePageEvents.currentImage.width;
                            window.LayerManager.Current.height = HomePageEvents.currentImage.height;
                            context.drawImage(HomePageEvents.currentImage, 0, 0, HomePageEvents.currentImage.width, HomePageEvents.currentImage.height);
                        };
                    } else {
                        // The picker was dismissed with no selected file
                        WinJS.log && WinJS.log("Operation cancelled.", "sample", "status");
                    }
                });
            }
        });
})();