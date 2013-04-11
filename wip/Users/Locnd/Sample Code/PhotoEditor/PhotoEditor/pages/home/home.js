﻿(function () {
    "use strict";

    WinJS.UI.Pages.define("/pages/home/home.html", {
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            document.getElementById("openFileCmd").addEventListener("click", HomePageEvents._openFileCmd, false);
            

            var canvas = document.querySelector(".homepage #mainScreen #displayCanvas");
            // Set first canvas to draw.
            window.Tools.setCanvas(canvas);
        },
    });
})();
