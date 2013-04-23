(function () {
    "use strict";

    window.LayerManager = {};
    window.LayerManager.Layers = new WinJS.Binding.List();
    window.LayerManager.Current = null;
    var currentData;
    var totalLayer = 0;
    window.LayerManager.Find = function (layerName) {
        var layerClass = "Loc" + layerName.replace("/", "").replace(" ", "");
        var canvas = document.querySelector(".homepage #editorScreen #mainScreen ." + layerClass);
        return canvas;
    };

    var gMouseX, gMouseY, gMouseT, gMouseL;

    document.onmousedown = function (e) {
        if (!window.LayerManager.Current) {
            return;
        }
        
        if (window.Tools.Current === null) {
            window.Tools.Current = window.Tools.Brush;
        }

        var mouseX = e.offsetX;
        var mouseY = e.offsetY; // 129 px for ribbonbar.

        if (e.clientY < 129) {
            return;
        }
        
        if (window.Tools.Current === window.Tools.Move) {
            gMouseX = e.clientX;
            gMouseY = e.clientY;
            mouseX = window.LayerManager.Current.style.marginLeft.replace("px", "");
            mouseY = window.LayerManager.Current.style.marginTop.replace("px", "");
            gMouseT = window.LayerManager.Current.style.marginTop.replace("px", "");
            gMouseL = window.LayerManager.Current.style.marginLeft.replace("px", "");
         
            
        } else if (window.Tools.Current === window.Tools.Transform) {
            mouseX = e.clientX;
            mouseY = e.clientY - 129;
        }

        window.Tools.Current.start(mouseX, mouseY);
    };

    document.onmousemove = function (e) {
        var mouseX = e.offsetX;
        var mouseY = e.offsetY; // 129 px for ribbonbar.

        if (window.Tools.Current === window.Tools.Move) {
            mouseX = parseInt(gMouseL) + (e.clientX - gMouseX);
            mouseY = parseInt(gMouseT) + (e.clientY - gMouseY);
        }
        else if (window.Tools.Current === window.Tools.Transform) {
            mouseX = e.clientX;
            mouseY = e.clientY - 129;
        }
        
        if (window.Tools.Current) {
            window.Tools.Current.moveTo(mouseX, mouseY);
        }
    };

    document.onmouseup = function () {
        if (window.Tools.Current) {
            window.Tools.Current.end();
        }
    };

    window.LayerManager.CreateLayer = function (layerName, width, height) {
        if (layerName === undefined || layerName === null) {
            layerName = "Layer " + (totalLayer + 1);
        }

        var layerClass = "Loc" + layerName.replace("/", "").replace(" ", "");

        var canvas = window.LayerManager.Find(layerName);

        if (canvas) {
            return;
        }

        totalLayer++;
        
        canvas = document.createElement("canvas");
        canvas.setAttribute("class", layerClass);
        canvas.style.zIndex = totalLayer;
        canvas.style.backgroundColor = "transparent";
        canvas.style.marginTop = "0px";
        canvas.style.marginLeft = "0px";
        // canvas.style.position = "fixed";

        // Make all layer item in list view to normal color.
        var items = document.querySelectorAll(".homepage #editorScreen #layersContainer #layerItem");
        for (var i = 0; i < items.length; i++) {
            items[i].style.backgroundColor = "#70D0B0";
        }

        document.querySelector(".homepage #editorScreen #mainScreen").appendChild(canvas);
        window.LayerManager.Layers.push(WinJS.Binding.as({ name: layerName, data: {}, index: totalLayer }));
        window.LayerManager.SelectLayer(layerName);

        var can = document.getElementById('backgroundCanvas');
        can.style.visibility = "visible";


        if (width != undefined && height != undefined) {
            canvas.style.width = width + "px";
            canvas.style.height = height + "px";
            canvas.width = width;
            canvas.height = height;
            can.width = width;
            can.height = height;
            can.style.width = canvas.style.width;
            can.style.height = canvas.style.height;
        }

        // Create a checkerboard background.
        // set up a pattern, something really elaborate!
        var pattern = document.createElement('canvas');
        pattern.width = 40;
        pattern.height = 40;
        var pctx = pattern.getContext('2d');

        pctx.fillStyle = "rgb(199, 210, 199)";
        pctx.fillRect(0, 0, 20, 20);
        pctx.fillRect(20, 20, 20, 20);

        var ctx = can.getContext('2d');
        var patternFill = ctx.createPattern(pattern, "repeat");
        ctx.fillStyle = patternFill;
        ctx.fillRect(0, 0, can.width, can.height);
    };

    window.LayerManager.SelectLayer = function (layerName) {
        var canvas = window.LayerManager.Find(layerName);

        if (canvas) {
            window.LayerManager.Current = canvas;
            window.Tools.SetTransformObj(canvas);

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