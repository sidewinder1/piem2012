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
                        
            var colorList = element.querySelector(".homepage #ribbonBar #colorContainer #colorsDiv").winControl;
            colorList.itemDataSource = window.ColorManager.Colors.dataSource;
            colorList.oniteminvoked = HomePageEvents._selectColor.bind(this);
            
            var layers = element.querySelector(".homepage #editorScreen #layersContainer").winControl;

            layers.itemDataSource = window.LayerManager.Layers.dataSource;
            layers.oniteminvoked = HomePageEvents._layerSelected.bind(this);
            
                    
        },
    });
})();
