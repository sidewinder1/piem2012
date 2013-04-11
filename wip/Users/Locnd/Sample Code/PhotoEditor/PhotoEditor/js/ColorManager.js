(function () {
    "use strict";
    window.ColorManager = {};
    window.ColorManager.Color1 ="black";
    window.ColorManager.Color2 = "white";
    
    window.ColorManager.GetColor = function () {
        return [{ color: "black" },
                { color: "white" },
                { color: "gray" },
                { color: "lightgray" },
                { color: "pink" },
                { color: "green" },
                { color: "red" },
                { color: "olive" },
                { color: "yellow" },
                { color: "orange" }];
    };
    
    window.ColorManager.Colors = new WinJS.Binding.List(window.ColorManager.GetColor());

})();