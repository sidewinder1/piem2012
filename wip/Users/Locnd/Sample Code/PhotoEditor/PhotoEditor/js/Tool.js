(function () {
    "use strict";

    window.Tools = {};

    // Current tool that is used to draw.
    window.Tools.Current = null;
    var _strokeWidth = 3;
    var _strokeType = "round";

    window.Tools.CanvasContext = null;

    var clickX = new Array();
    var clickY = new Array();
    var clickDrag = new Array();
    var paint = false;

    window.Tools.setDrawer = function (color, strokeWidth, strokeType) {
        if (color.length > 1) {
            window.ColorManager.Color1 = color;
        }

        _strokeType = strokeType;
        _strokeWidth = strokeWidth;
    };

    window.Tools.Redraw = function () {
        window.LayerManager.Current.width = window.Tools.Canvas.width; // Clears the canvas
        // window.LayerManager.Current.clearRect
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

    var getIndex = function (x, y, w, a) {
        return (x + y * w) * 4 + a;
    };

    var getPointsInLine = function (x1, y1, x2, y2) {
        // With 2 points, we will create a line with : y = mx + b;
        var m = (y2 - y1) / (x2 - x1);
        var b = y1 - m * x1;

        var points = new Array();
        points.push({ x: x1, y: y1 });
        points.push({ x: x2, y: y2 });
        var adder;
        if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
            // Create points follow x.
            if (x1 < x2) {
                adder = 1;
            }
            else {
                adder = -1;
            }

            for (var j = 0; j < Math.abs(x1 - x2) ; j++) {
                var pointX = x1 + (j * adder);
                points.push({ x: pointX, y: Math.round(m * pointX + b) });
            }
        }
        else {
            // Create points follow y.
            if (y1 < y2) {
                adder = 1;
            }
            else {
                adder = -1;
            }

            for (var i = 0; i < Math.abs(y1 - y2) ; i++) {
                var pointY = y1 + (i * adder);
                points.push({ x: Math.round((pointY - b) / m), y: pointY });
            }
        }

        return points;
    };

    var lastPoint = {};
    window.Tools.ErasePoint = function (x, y) {
        // get the image data object
        var image = window.Tools.CanvasContext.getImageData(0, 0, window.LayerManager.Current.width, window.LayerManager.Current.height);
        // get the image data values
        var width = window.LayerManager.Current.width;
        var points = getPointsInLine(x, y, lastPoint.x, lastPoint.y);

        for (var j = 0; j < points.length; j++) {
            var startX = points[j].x - Math.floor(_strokeWidth / 2);
            var startY = points[j].y - Math.floor(_strokeWidth / 2);

            for (var i = 0; i < _strokeWidth * _strokeWidth; i++) {
                image.data[getIndex(startX + Math.floor(i / _strokeWidth), startY + (i % _strokeWidth), width, 3)] = 0;
            }
        }

        lastPoint = { x: x, y: y };
        // after the manipulation, reset the data
        //image.data = imageData;
        // and put the imagedata back to the canvas
        window.Tools.CanvasContext.putImageData(image, 0, 0);
    };
 
    // Pen tool. Used to draw vectors.
    window.Tools.Pen = WinJS.Class.define(
    {
        start: function (x, y) {

        },

        moveTo: function (x, y) {

        },

        end: function (x, y) {

        }
    });

    // Brush tool. Used to draw strokes.
    window.Tools.Brush = WinJS.Class.define(
    {
        start: function (x, y) {
            if (!window.Tools.CanvasContext) {
                return;
            }

            window.Tools.CanvasContext.strokeStyle = window.ColorManager.Color1;
            window.Tools.CanvasContext.lineJoin = _strokeType;
            window.Tools.CanvasContext.lineWidth = _strokeWidth;

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
            if (!paint) {
                return;
            }
            
            paint = false;
            window.Tools.CanvasContext.closePath();
        }
    });

    // Move tool. Used to move canvas.
    window.Tools.Move = WinJS.Class.define(
    {
        start: function (x, y) {
            paint = true;
            window.LayerManager.Current.style.left = x + "px";
            window.LayerManager.Current.style.top = y + "px";
        },

        moveTo: function (x, y) {
            if (paint) {
                window.LayerManager.Current.style.left = x + "px";
                window.LayerManager.Current.style.top = y + "px";
            }
        },

        end: function () {
            paint = false;
        }
    });

    // Eraser tool. Used to erase strokes.
    window.Tools.Eraser = WinJS.Class.define(
    {
        start: function (x, y) {
            lastPoint = { x: x, y: y };
            paint = true;
            window.Tools.ErasePoint(x, y);
        },

        moveTo: function (x, y) {
            if (paint) {
                window.Tools.ErasePoint(x, y);
            }
        },

        end: function () {
            paint = false;
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