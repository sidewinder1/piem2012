(function () {
    "use strict";

    var imaging = Windows.Graphics.Imaging;
    WinJS.Namespace.define("HomePageEvents",
        {
            currentImage: {},

            _processOnClicked: function () {
                CanvasProcessing.runFilter(HomePageEvents.currentImage);
            }, 
            
            _newLayerCmd: function () {
                var popup = document.querySelector("#newLayerFlyout");
                popup.winControl.show(this, "top");
            },

            _visibilityLayer: function (dom) {
                var title = dom.previousSibling.previousSibling;
                var canvas = window.LayerManager.Find(title.textContent);
                if (canvas.style.visibility == "hidden") {
                    canvas.style.visibility = "visible";
                } else {
                    canvas.style.visibility = "hidden";
                }
            },
            
            _deleteLayer: function (dom) {
                var title = dom.previousSibling.previousSibling;
                var canvas = window.LayerManager.Find(title.textContent);
                
                document.querySelector(".homepage #editorScreen #mainScreen").removeChild(canvas);
                for (var i = 0; i < window.LayerManager.Layers.length; i++) {
                    var item = window.LayerManager.Layers.getAt(i);
                    if (item.name == title.textContent) {
                        window.LayerManager.Layers.pop(item);
                        return;
                    }
                }              
            },

            _newLayerClicked: function () {
                var name = document.querySelector("#newLayerFlyout #layerName");
                var width = document.querySelector("#newLayerFlyout #layerWidth");
                var height = document.querySelector("#newLayerFlyout #layerHeight");
                
                if (name.value === "" || name.value === undefined) {
                    name.focus();
                    return;
                }
                if (width.value === "" || width.value === undefined) {
                    width.focus();
                    return;
                }
                if (height.value === "" || height.value === undefined) {
                    height.focus();
                    return;
                }

                window.LayerManager.CreateLayer(name.value, width.value, height.value);
                var popup = document.querySelector("#newLayerFlyout");
                popup.winControl.hide();
            },

            _selectColor: function (args) {
                var item = window.ColorManager.Colors.getAt(args.detail.itemIndex);
                window.ColorManager.Color1 = item.color;
                var color1Dom = document.querySelector(".homepage #ribbonBar #colorContainer #color1");
                color1Dom.style.backgroundColor = item.color;
            },
            
            _layerSelected: function (args) {
                var item = window.LayerManager.Layers.getAt(args.detail.itemIndex);

                // Make all layer item in list view to normal color.
                var items = document.querySelectorAll(".homepage #editorScreen #layersContainer #layerItem");
                for (var i = 0; i < items.length; i++) {
                    items[i].style.backgroundColor = "#70D0B0";
                }

                var currentLayerItem = args.srcElement.querySelector("#layerItem");
                currentLayerItem.style.backgroundColor = "#79E8C8";
                window.LayerManager.SelectLayer(item.name);
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
                            encoderId = imaging.BitmapEncoder.jpegEncoderId;
                            break;
                        case ".bmp":
                            encoderId = imaging.BitmapEncoder.bmpEncoderId;
                            break;
                        case ".png":
                        default:
                            encoderId = imaging.BitmapEncoder.pngEncoderId;
                            break;
                    }

                    return file.openAsync(Windows.Storage.FileAccessMode.readWrite);
                }).then(function (_stream) {
                    stream = _stream;

                    // BitmapEncoder expects an empty output stream; the user may have selected a
                    // pre-existing file.
                    stream.size = 0;
                    return imaging.BitmapEncoder.createAsync(encoderId, stream);
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
                        imaging.BitmapPixelFormat.rgba8,
                        imaging.BitmapAlphaMode.straight,
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
                        HomePageEvents.currentImage.onload = function () {
                            window.LayerManager.CreateLayer(null, HomePageEvents.currentImage.width, HomePageEvents.currentImage.height);
                            var context = window.LayerManager.Current.getContext("2d");
                        
                            //window.LayerManager.Current.width = HomePageEvents.currentImage.width;
                            //window.LayerManager.Current.style.width = HomePageEvents.currentImage.width + "px";
                            //window.LayerManager.Current.style.height = HomePageEvents.currentImage.height + "px";
                            //window.LayerManager.Current.height = HomePageEvents.currentImage.height;
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