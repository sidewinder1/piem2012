(function () {
    "use strict";
    WinJS.Namespace.define("HomePageEvents",
        {
            currentImage: {},
            
            _processOnClicked: function () {
                CanvasProcessing.runFilter("displayCanvas", HomePageEvents.currentImage);
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
                        var canvas = document.querySelector(".homepage #mainScreen #displayCanvas");

                        HomePageEvents.currentImage.src = URL.createObjectURL(file);

                        var context = canvas.getContext("2d");
                        HomePageEvents.currentImage.onload = function () {
                            canvas.width = HomePageEvents.currentImage.width;
                            canvas.height = HomePageEvents.currentImage.height;
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