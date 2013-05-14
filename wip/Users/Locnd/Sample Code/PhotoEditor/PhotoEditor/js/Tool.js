﻿(function () {
    "use strict";

    window.Tools = {};

    // Current tool that is used to draw.
    window.Tools.Current = null;
    var _strokeWidth = 1;
    var _strokeType = "round";
    var _currentTransformObj;
    window.Tools.FillShape = false;
    
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

    window.Tools.ResetAllState = function () {
        window.Tools.UnsetTransformObj();
        if (window.LayerManager.Current) {
            window.LayerManager.Current.style.cursor = "default";
        }
    };

    window.Tools.SetTransformObj = function (dom) {
        if (window.Tools.Current != window.Tools.Transform || dom === null) {
            return;
        }

        window.Tools.UnsetTransformObj();

        _currentTransformObj = dom;
        var border = document.querySelector(".homepage #editorScreen #mainScreen #borderHandler");
        border.style.visibility = "visible";
        border.style.marginLeft = dom.style.marginLeft;
        border.style.marginTop = dom.style.marginTop;
        border.style.height = dom.style.height;
        border.style.width = dom.style.width;
    };

    window.Tools.UnsetTransformObj = function () {
        if (!_currentTransformObj) {
            return;
        }

        var border = document.querySelector(".homepage #editorScreen #mainScreen #borderHandler");
        border.style.visibility = "collapse";
        _currentTransformObj = null;
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

    // Pen tool. Used to draw vectors.
    window.Tools.ColorPicker = WinJS.Class.define(
    {
        start: function (x, y) {

        },

        moveTo: function (x, y) {

        },

        end: function (x, y) {

        }
    });

    var lastData;
    // Pen tool. Used to draw vectors.
    window.Tools.RectangleDraw = WinJS.Class.define(
    {
        start: function (x, y) {
            paint = true;
            window.Tools.CanvasContext.strokeStyle = window.ColorManager.Color1;
            window.Tools.CanvasContext.lineJoin = _strokeType;
            window.Tools.CanvasContext.fillStyle = window.ColorManager.Color2;
            window.Tools.CanvasContext.lineWidth = _strokeWidth;
            lastPoint = { x: x, y: y };
            lastData = window.Tools.CanvasContext.getImageData(0, 0, window.LayerManager.Current.width, window.LayerManager.Current.height);
        },

        moveTo: function (x, y) {
            if (!paint) {
                return;
            }
            if (lastData) {
                window.Tools.CanvasContext.putImageData(lastData, 0, 0);
            }
            window.Tools.CanvasContext.beginPath();
            window.Tools.CanvasContext.rect(lastPoint.x, lastPoint.y, x - lastPoint.x, y - lastPoint.y);
            window.Tools.CanvasContext.closePath();
            if (window.Tools.FillShape) {
                window.Tools.CanvasContext.fill();
            }
            
            window.Tools.CanvasContext.lineWidth = _strokeWidth;
            window.Tools.CanvasContext.strokeStyle = window.ColorManager.Color1;
            window.Tools.CanvasContext.stroke();
        },

        end: function (x, y) {
            if (!paint) {
                return;
            }
            paint = false;
        }
    });

    // Pen tool. Used to draw vectors.
    window.Tools.LineDraw = WinJS.Class.define(
    {
        start: function (x, y) {
            paint = true;
            window.Tools.CanvasContext.strokeStyle = window.ColorManager.Color1;
            window.Tools.CanvasContext.lineJoin = _strokeType;
            window.Tools.CanvasContext.lineWidth = _strokeWidth;
            lastPoint = { x: x, y: y };           
            lastData = window.Tools.CanvasContext.getImageData(0, 0, window.LayerManager.Current.width, window.LayerManager.Current.height);
        },

        moveTo: function (x, y) {
            if (!paint) {
                return;
            }
            if (lastData) {
                window.Tools.CanvasContext.putImageData(lastData, 0, 0);
            }
            
            window.Tools.CanvasContext.beginPath();
            window.Tools.CanvasContext.moveTo(lastPoint.x, lastPoint.y);
            window.Tools.CanvasContext.lineTo(x, y);
            window.Tools.CanvasContext.closePath();
            
            window.Tools.CanvasContext.stroke();
        },

        end: function (x, y) {
            if (!paint) {
                return;
            }
            paint = false;
        }
    });
    
    // Pen tool. Used to draw vectors.
    window.Tools.CircleDraw = WinJS.Class.define(
    {
        start: function (x, y) {
            paint = true;
            window.Tools.CanvasContext.strokeStyle = window.ColorManager.Color1;
            window.Tools.CanvasContext.lineJoin = _strokeType;
            window.Tools.CanvasContext.fillStyle = window.ColorManager.Color2;

            window.Tools.CanvasContext.lineWidth = _strokeWidth;
            lastPoint = { x: x, y: y };
            lastData = window.Tools.CanvasContext.getImageData(0, 0, window.LayerManager.Current.width, window.LayerManager.Current.height);
        },

        moveTo: function (x, y) {
            if (!paint) {
                return;
            }
            
            if (lastData) {
                window.Tools.CanvasContext.putImageData(lastData, 0, 0);
            }

            drawEllipse(Math.min(x, lastPoint.x), Math.min(y, lastPoint.y), Math.abs(x - lastPoint.x), Math.abs(y - lastPoint.y));
        },

        end: function (x, y) {
            if (!paint) {
                return;
            }
            paint = false;
        }
    });
    
    var hDirect = "c", vDirect = "c", oldSize;
    var gMouseX, gMouseY, gMouseT, gMouseL;
    // Move tool. Used to move canvas.
    window.Tools.Move = WinJS.Class.define(
    {        
        start: function (x, y) {
            paint = true;
           
            //window.LayerManager.Current.style.marginLeft = x + "px";
            //window.LayerManager.Current.style.marginTop = y + "px";
        },

        moveTo: function (x, y) {
            if (paint) {
                var mouseX = parseInt(gMouseL) + (x - gMouseX);
                var mouseY = parseInt(gMouseT) + (y - gMouseY);
                window.LayerManager.Current.style.marginLeft = mouseX + "px";
                window.LayerManager.Current.style.marginTop = mouseY + "px";
            }
        },

        end: function () {
            paint = false;
        }
    });
    
    // Transform tool. Used to resize a object.
    window.Tools.Transform = WinJS.Class.define(
    {
        start: function (x, y) {
            lastPoint = {};
            oldSize = { w: Number(_currentTransformObj.style.width.replace("px", "")), h: Number(_currentTransformObj.style.height.replace("px", "")) };
            var e = event;
            switch (e.srcElement.getAttribute("class")) {
                case "corner tlCorner":
                    hDirect = "l";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", ""));
                    vDirect = "t";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", ""));
                    break;
                default:
                    vDirect = "c";
                    hDirect = "c";
                    break;
                case "corner trCorner":
                    hDirect = "r";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", "")) + oldSize.w;
                    vDirect = "t";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", ""));
                    break;
                case "corner tCorner":
                    vDirect = "t";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", ""));
                    hDirect = "c";
                    break;
                case "corner lCorner":
                    hDirect = "l";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", ""));
                    vDirect = "c";
                    break;
                case "corner rCorner":
                    hDirect = "r";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", "")) + oldSize.w;
                    vDirect = "c";
                    break;
                case "corner blCorner":
                    hDirect = "l";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", ""));
                    vDirect = "b";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", "")) + oldSize.h;
                    break;
                case "corner brCorner":
                    hDirect = "r";
                    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", "")) + oldSize.w;
                    vDirect = "b";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", "")) + oldSize.h;
                    break;
                case "corner bCorner":
                    vDirect = "b";
                    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", "")) + oldSize.h;
                    hDirect = "c";
                    break;
            }
            
            //if (Math.abs(x - 5 - Number(_currentTransformObj.style.marginLeft.replace("px", ""))) < 6) {
            //    hDirect = "l";
            //    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", ""));
            //}
            //else if (Math.abs(x - 5 - (Number(_currentTransformObj.style.marginLeft.replace("px", "")) + oldSize.w)) < 6) {
            //    hDirect = "r";
            //    lastPoint.x = Number(_currentTransformObj.style.marginLeft.replace("px", "")) + oldSize.w;
            //} else {
            //    hDirect = "c";
            //}

            //if (Math.abs(y - 5 - Number(_currentTransformObj.style.marginTop.replace("px", ""))) < 6) {
            //    vDirect = "t";
            //    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", ""));
            //}
            //else if (Math.abs(y - 5 - (Number(_currentTransformObj.style.marginTop.replace("px", "")) + oldSize.h)) < 6) {
            //    vDirect = "b";
            //    lastPoint.y = Number(_currentTransformObj.style.marginTop.replace("px", "")) + oldSize.h;
            //}
            //else {
            //    vDirect = "c";
            //}
            
            if (vDirect === "c" && hDirect === "c") {
                gMouseX = x;
                gMouseY = y;
                gMouseT = window.LayerManager.Current.style.marginTop.replace("px", "");
                gMouseL = window.LayerManager.Current.style.marginLeft.replace("px", "");
            }

            paint = true;
        },

        moveTo: function (x, y) {
            if (paint) {                
                if (vDirect === "c" && hDirect === "c") {
                    var mouseX = parseInt(gMouseL) + (x - gMouseX);
                    var mouseY = parseInt(gMouseT) + (y - gMouseY);
                    window.LayerManager.Current.style.marginLeft = mouseX + "px";
                    window.LayerManager.Current.style.marginTop = mouseY + "px";
                }
                else {
                    if (hDirect === "l") {
                        _currentTransformObj.style.marginLeft = x + "px";
                        _currentTransformObj.style.width = (lastPoint.x - x + oldSize.w) + "px";
                    }
                    else if (hDirect === "r") {
                        _currentTransformObj.style.width = (x - lastPoint.x + oldSize.w) + "px";
                    }

                    if (vDirect === "t") {
                        _currentTransformObj.style.marginTop = y + "px";
                        _currentTransformObj.style.height = (lastPoint.y - y + oldSize.h) + "px";
                    } else if (vDirect === "b") {
                        _currentTransformObj.style.height = (y - lastPoint.y + oldSize.h) + "px";
                    }
                }
                
                var border = document.querySelector(".homepage #editorScreen #mainScreen #borderHandler");
                border.style.marginLeft = _currentTransformObj.style.marginLeft;
                border.style.marginTop = _currentTransformObj.style.marginTop;
                border.style.width = _currentTransformObj.style.width;
                border.style.height = _currentTransformObj.style.height;
            }
        },

        end: function () {
            if (!paint) {
                return;
            }

            // copy image to stretch or reduce image.
            var img = new Image();
            img.src = window.LayerManager.Current.toDataURL("image/png");

            _currentTransformObj.width = Number(_currentTransformObj.style.width.replace("px", ""));
            _currentTransformObj.height = Number(_currentTransformObj.style.height.replace("px", ""));

            var w = _currentTransformObj.width, h = _currentTransformObj.height;
            img.onload = function() {
                window.Tools.CanvasContext = window.LayerManager.Current.getContext('2d');
                window.Tools.CanvasContext.drawImage(img, 0, 0, w, h);
            };
           
            paint = false;
        }
    });

    // Brush tool. Used to draw strokes.
    window.Tools.Brush = WinJS.Class.define(
    {
        start: function (x, y) {
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
    
    function drawEllipse(x, y, w, h) {
        var kappa = .5522848,
            ox = (w / 2) * kappa, // control point offset horizontal
            oy = (h / 2) * kappa, // control point offset vertical
            xe = x + w,           // x-end
            ye = y + h,           // y-end
            xm = x + w / 2,       // x-middle
            ym = y + h / 2;       // y-middle

        window.Tools.CanvasContext.beginPath();
        window.Tools.CanvasContext.moveTo(x, ym);
        window.Tools.CanvasContext.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
        window.Tools.CanvasContext.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
        window.Tools.CanvasContext.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
        window.Tools.CanvasContext.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
        window.Tools.CanvasContext.closePath();
        window.Tools.CanvasContext.stroke();
        
        if (window.Tools.FillShape) {
            window.Tools.CanvasContext.fill();
        }
    }
})();