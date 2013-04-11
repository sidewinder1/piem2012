(function () {
    "use strict";

    WinJS.UI.Pages.define("/pages/home/home.html", {
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            document.getElementById("openFileCmd").addEventListener("click", HomePageEvents._openFileCmd, false);
            
            document.getElementById("saveFileCmd").addEventListener("click", HomePageEvents._saveFile, false);
            
            var canvas = document.querySelector(".homepage #mainScreen #displayCanvas");
            // Set first canvas to draw.
            window.Tools.setCanvas(canvas);

            var colorList = element.querySelector(".homepage #ribbonBar #colorContainer #colorsDiv").winControl;

            colorList.itemDataSource = window.ColorManager.Colors.dataSource;
            colorList.oniteminvoked = HomePageEvents._selectColor.bind(this);
            
            // Create a checkerboard background.
            // set up a pattern, something really elaborate!
            var pattern = document.createElement('canvas');
            pattern.width = 40;
            pattern.height = 40;
            var pctx = pattern.getContext('2d');

            pctx.fillStyle = "rgb(199, 210, 199)";
            pctx.fillRect(0, 0, 20, 20);
            pctx.fillRect(20, 20, 20, 20);

            var can = document.getElementById('backgroundCanvas');
            var ctx = can.getContext('2d');
            var patternFill = ctx.createPattern(pattern, "repeat");
            ctx.fillStyle = patternFill;
            ctx.fillRect(0, 0, can.width, can.height);            
        },
    });
})();
