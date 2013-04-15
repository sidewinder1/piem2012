(function() {
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
        canvas.style.border = "1px solid #aaa";
        canvas.draggable = "true";
        
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
        
        canvas.onmousedown = function (e) {
            if (window.Tools.Current === null) {
                window.Tools.Current = window.Tools.Brush;
            }

            var mouseX = e.offsetX;
            var mouseY = e.offsetY; // 129 px for ribbonbar.
            if (window.Tools.Current === window.Tools.Move) {
                return;
                mouseX = e.x - mouseX;
                mouseY = e.y - mouseY;
            }

            window.Tools.Current.start(mouseX, mouseY);
        };

        canvas.onmousemove = function (e) {
            var mouseX = e.offsetX;
            var mouseY = e.offsetY; // 129 px for ribbonbar.

            if (window.Tools.Current === window.Tools.Move) {
                return;
                mouseX = e.x - mouseX;
                mouseY = e.y - mouseY;
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

    
        canvas.onDragStart = function(ev) {
            ev.dataTransfer.effectAllowed = 'move';
            ev.dataTransfer.setData("Text", ev.target.getAttribute('id'));
            ev.dataTransfer.setDragImage(ev.target, 0, 0);
            return true;
        };

        canvas.ondragEnter = function(ev) {
            event.preventDefault();
            return true;
        };

        canvas.ondragOver = function(ev) {
            return false;
        };
        
        canvas.ondragDrop = function(ev) {
            var src = ev.dataTransfer.getData("Text");
            ev.target.appendChild(document.getElementById(src));
            ev.stopPropagation();
            return false;
        };
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