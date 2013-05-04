(function () {
    "use strict";

    WinJS.UI.Pages.define("/pages/home/home.html", {
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            // Binding events to button on appbar.
            document.getElementById("newCmd").addEventListener("click", HomePageEvents._newLayerCmd, false);
            document.getElementById("openFileCmd").addEventListener("click", HomePageEvents._openFileCmd, false);            
            document.getElementById("saveFileCmd").addEventListener("click", HomePageEvents._saveFile, false);
            document.getElementById("saveAsFileCmd").addEventListener("click", HomePageEvents._saveAsFile, false);
            
            // Add event listener to custom color button.
            var customColor = element.querySelector(".homepage #ribbonBar #colorContainer #customColor");
            customColor.addEventListener("click", HomePageEvents._showColorPicker, false);

            // Initialize data to display color picker control.
            var colorRefiner = element.querySelector("#colorPicker #colorRefiner");
            colorRefiner.addEventListener("click", HomePageEvents._colorRefinerClicked, false);

            var addColor = element.querySelector("#colorPicker #addColor");
            addColor.addEventListener("click", HomePageEvents._addColorClicked, false);

            var canvas = element.querySelector("#colorPicker #colorPanel");
            var context = canvas.getContext("2d");
            var img = new Image();
            img.src = "/images/color.png";
            img.onload = function () {
                canvas.height = this.height;
                canvas.width = this.width;
                colorRefiner.height = canvas.height;
                context.drawImage(this, 0, 0);
            };

            canvas.addEventListener("click", HomePageEvents._colorPanelClicked, false);

            var colorList = element.querySelector(".homepage #ribbonBar #colorContainer #colorsDiv").winControl;
            colorList.itemDataSource = window.ColorManager.Colors.dataSource;
            colorList.oniteminvoked = HomePageEvents._selectColor.bind(this);
            
            var layers = element.querySelector(".homepage #editorScreen #layersContainer").winControl;

            layers.itemDataSource = window.LayerManager.Layers.dataSource;
            layers.oniteminvoked = HomePageEvents._layerSelected.bind(this);
            
                    
        },
    });
})();
