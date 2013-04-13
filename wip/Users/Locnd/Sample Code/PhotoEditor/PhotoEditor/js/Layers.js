(function() {
    "use strict";

    window.LayerManager = {};
    window.LayerManager.Layers = new WinJS.Binding.List();
    window.LayerManager.Current = null;
    var currentData;
    window.LayerManager.CreateLayer = function (layerName) {
        if (layerName === undefined || layerName === null) {
            layerName = "Layer" + (window.LayerManager.Layers.length + 1);
        }
        
        var layerClass = layerName.replace("/", "");
        var canvas = document.querySelector(".homepage #editorScreen #mainScreen ." + layerClass);
        
        if (canvas) {
            return;
        }

        canvas = document.createElement("canvas");
        canvas.setAttribute("class", layerClass);
        canvas.style.zIndex = (window.LayerManager.Layers.length + 1);
        canvas.style.backgroundColor = "transparent";
        
        document.querySelector(".homepage #editorScreen #mainScreen").appendChild(canvas);
        
        window.LayerManager.Layers.push(WinJS.Binding.as({ name: layerName, data: {}, index: (window.LayerManager.Layers.length + 1) }));

        window.LayerManager.SelectLayer(layerName);
    };

    window.LayerManager.SelectLayer = function (layerName) {
        var layerClass = layerName.replace("/", "");
        var canvas = document.querySelector(".homepage #editorScreen #mainScreen ." + layerClass);
        
        if (canvas) {
            window.LayerManager.Current = canvas;

            canvas.onmousedown = function (e) {
                if (window.Tools.Current === null) {
                    window.Tools.Current = window.Tools.Brush;
                }

                var mouseX = e.offsetX;
                var mouseY = e.offsetY; // 129 px for ribbonbar.
                window.Tools.Current.start(mouseX, mouseY);
            };

            canvas.onmousemove = function (e) {
                var mouseX = e.offsetX;
                var mouseY = e.offsetY; // 129 px for ribbonbar.
                if (window.Tools.Current) {
                    window.Tools.Current.moveTo(mouseX, mouseY);
                }
            };

            document.onmouseup = function () {
                if (window.Tools.Current) {
                    window.Tools.Current.end();
                }
            };

            window.Tools.CanvasContext = canvas.getContext("2d");
            
            for (var i = 0; i < window.LayerManager.Layers.length; i++) {
                if (window.LayerManager.Layers.getAt(i).name == layerName) {
                    currentData = window.LayerManager.Layers.getAt(i);
                    break;
                }
            }
        }
    };
})();