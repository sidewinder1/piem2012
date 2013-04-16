﻿(function() {
    "use strict";

    window.LayerManager = {};
    window.LayerManager.Layers = new WinJS.Binding.List();
    window.LayerManager.Current = null;
    var currentData;

    window.LayerManager.Find = function(layerName) {
        var layerClass = "Loc" + layerName.replace("/", "").replace(" ", "");
        var canvas = document.querySelector(".homepage #editorScreen #mainScreen ." + layerClass);
        return canvas;
    };

    var _mouseX, _mouseY, _mouseT, _mouseL;
    
    document.onmousedown = function (e) {
        if (window.Tools.Current === null) {
            window.Tools.Current = window.Tools.Brush;
        }

        _mouseX = e.offsetX;
        _mouseY = e.offsetY; // 129 px for ribbonbar.
        if (window.Tools.Current === window.Tools.Move) {
            _mouseX = e.clientX;
            _mouseY = e.clientY;
            _mouseT = e.srcElement.style.top;
            _mouseL = e.srcElement.style.left;
        }
        
        //document.querySelector("#titleDiv").textContent = _mouseX + ", " + _mouseY;
        window.Tools.Current.start(_mouseX, _mouseY);
    };

    document.onmousemove = function (e) {
        var mouseX = e.offsetX;
        var mouseY = e.offsetY; // 129 px for ribbonbar.

        if (window.Tools.Current === window.Tools.Move) {
            mouseX = parseInt(_mouseL) + (e.clientX - _mouseX);
            mouseY = parseInt(_mouseT) + (e.clientY - _mouseY);
        }
        
        if (window.Tools.Current) {
            //document.querySelector("#titleDiv").textContent = mouseX + ", " + mouseY;
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
            layerName = "Layer " + (window.LayerManager.Layers.length + 1);
        }

        var layerClass = "Loc" + layerName.replace("/", "").replace(" ", "");

        var canvas = window.LayerManager.Find(layerName);
        
        if (canvas) {
            return;
        }

        canvas = document.createElement("canvas");
        canvas.setAttribute("class", layerClass);
        canvas.style.zIndex = (window.LayerManager.Layers.length + 1);
        canvas.style.backgroundColor = "transparent";
        canvas.style.msGridColumnAlign = "start";
        canvas.style.msGridRowAlign = "start";
        canvas.style.border = "1px solid #aaa";
        canvas.style.position = "absolute";

        document.querySelector(".homepage #editorScreen #mainScreen").appendChild(canvas);
        window.LayerManager.Layers.push(WinJS.Binding.as({ name: layerName, data: {}, index: (window.LayerManager.Layers.length + 1) }));
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