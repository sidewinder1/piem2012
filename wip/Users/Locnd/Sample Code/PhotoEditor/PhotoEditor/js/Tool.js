(function () {
    "use strict";
    
    window.Tools = {};
    
    // Current tool that is used to draw.
    window.Tools.Current = null;
    var _color = "black";
    var _strokeWidth = 3;
    var _strokeType = "round";
    
    window.Tools.Canvas = null;
    window.Tools.CanvasContext = null;

    var clickX = new Array();
    var clickY = new Array();
    var clickDrag = new Array();
    var paint = false;

    window.Tools.setEditorScreen = function(objDom) {

    };

    window.Tools.setDrawer = function(color, strokeWidth, strokeType) {
        _color = color;
        _strokeType = strokeType;
        _strokeWidth = strokeWidth;
    };

    // Current canvas that is used to draw on.
    window.Tools.setCanvas = function(canvas) {
        window.Tools.Canvas = canvas;
        if (window.Tools.Current === null) {
            window.Tools.Current = window.Tools.Brush;
        }
        
        canvas.onmousedown = function (e) {
            window.Tools.CanvasContext.strokeStyle = _color;
            window.Tools.CanvasContext.lineJoin = _strokeType;
            window.Tools.CanvasContext.lineWidth = _strokeWidth;

            var mouseX = e.pageX - this.offsetLeft;
            var mouseY = e.pageY - this.offsetTop - 129; // 129 px for ribbonbar.
            window.Tools.Current.start(mouseX, mouseY);
        };
        
        canvas.onmousemove = function (e) {
            var mouseX = e.pageX - this.offsetLeft;
            var mouseY = e.pageY - this.offsetTop - 129; // 129 px for ribbonbar.
            if (window.Tools.Current) {
                window.Tools.Current.moveTo(mouseX, mouseY);
            }
        };
        
        canvas.onmouseup = function (e) {
            if (window.Tools.Current) {
                window.Tools.Current.end();
            }
        };

        window.Tools.CanvasContext = canvas.getContext("2d");
    };

    window.Tools.Redraw = function ()
    {
        window.Tools.Canvas.width = window.Tools.Canvas.width; // Clears the canvas
      
        for (var i = 0; i < clickX.length; i++) {
            window.Tools.CanvasContext.beginPath();
            if (clickDrag[i] && i) {
                window.Tools.CanvasContext.moveTo(clickX[i - 1], clickY[i - 1]);
            } else {
                window.Tools.CanvasContext.moveTo(clickX[i] - 1, clickY[i]);
            }
            window.Tools.CanvasContext.lineTo(clickX[i], clickY[i]);
            window.Tools.CanvasContext.closePath();
            window.Tools.CanvasContext.stroke();
        }
    };

    var addClick = function(x, y, dragging) {
        clickX.push(x);
        clickY.push(y);
        clickDrag.push(dragging);
    };

    // Pen tool. Used to draw vectors.
    window.Tools.Pen = WinJS.Class.define(
    {
        start : function(x, y) {
            
        },
        
        moveTo : function(x, y){
            
        },
        
        end : function(x, y) {
            
        }        
    });
    
    // Brush tool. Used to draw strokes.
    window.Tools.Brush = WinJS.Class.define(
    {
        start: function (x, y) {
            paint = true;
            window.Tools.CanvasContext.beginPath();
            window.Tools.CanvasContext.moveTo(x, y);
            window.Tools.CanvasContext.stroke();
            //addClick(x, y);
            //window.Tools.Redraw();
        },

        moveTo: function (x, y) {
            if (paint) {
                window.Tools.CanvasContext.lineTo(x, y);
                window.Tools.CanvasContext.stroke();
                //addClick(x, y, true);
                //window.Tools.Redraw();
            }
        },

        end: function () {
            paint = false;
            window.Tools.CanvasContext.closePath();            
        }
    });
    
    // Eraser tool. Used to erase strokes.
    window.Tools.Eraser = WinJS.Class.define(
    {
        start: function (x, y) {

        },

        moveTo: function (x, y) {

        },

        end: function (x, y) {

        }
    });
    
    // Painter tool. Used to fill color on canvas.
    window.Tools.Painter = WinJS.Class.define(
    {
        start: function (x, y) {

        },

        moveTo: function (x, y) {

        },

        end: function (x, y) {

        }
    });
})();