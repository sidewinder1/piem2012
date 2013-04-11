(function () {
    "use strict";
    window.ColorManager = {};
    
    window.ColorManager.GetColor = function () {
        return [{ color: "black" },
                { color: "white" },
                { color: "gray" },
                { color: "lightgray" },
                { color: "pink" },
                { color: "green" },
                { color: "red" },
                { color: "oliver" },
                { color: "yellow" },
                { color: "orange" }];
    };
    
    window.ColorManager.Colors = new WinJS.Binding.List(window.ColorManager.GetColor());

})();