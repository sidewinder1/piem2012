(function() {
    "use strict";

    window.LayerManager = {};
    window.LayerManager.Layers = new WinJS.Binding.List();

    window.LayerManager.CreateLayer = function (layerName) {
        var layerClass = layerName.replace("/", "");
        var temDom = document.querySelector("." + layerClass);
        
        if (temDom) {
            return;
        }

        var canvas = document.createElement("canvas");
        canvas.setAttribute("class", layerClass);
        canvas.style.zIndex = (window.LayerManager.Layers.length - 1);
        document.querySelector(".homepage #editorScreen #mainScreen").appendChild(canvas);
        window.LayerManager.Layers.push(WinJS.Binding.as({ name: layerName, data: {}, index : (window.LayerManager.Layers.length - 1) }));
    };

})();