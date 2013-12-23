(function () {
    if (!window.drawer) {
        window.drawer = {};
    }

    window.drawer.mainCanvas = null;
    window.drawer.drawContext = null;
    window.drawer.canvasSize = 700;
    window.drawer.pathData = [];

    var painting = false, maxHeight = 0;

    // Initialize some data for this tool as: get main canvas, set mouse events to this canvas.
    $(window).load(function () {
        $("#imageSource").change(function () {
            var input = this;
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('#fillSource').attr('src', e.target.result);
                }

                reader.readAsDataURL(input.files[0]);
            }
        });

        window.drawer.mainCanvas = $("#canvasPanel")[0];
        window.drawer.drawContext = window.drawer.mainCanvas.getContext("2d");
        // Initilize canvas setting here.
        window.drawer.mainCanvas.height = window.drawer.canvasSize;
        window.drawer.mainCanvas.width = window.drawer.canvasSize;

        $("#slider").slider({
            max: 1, min: 0, step: 0.1, value: 1, change: function () {

            }
        });

        // Check undefined.
        if (!window.drawer.mainCanvas) { return; }


        // ------- I N I T I A L I Z E   T E S T ================
        var parameter,
		nextParameter,
		bSpline = { x: 0, y: 0 },
		nextPos = { x: 0, y: 0 },
		THRESHOLD_DIST = 0.1,
		STEP_COUNT = 52,
        SMALLEST_DISTANCE = 9,
		lastDrawPoint = { x: -3, y: -3 };

        var getBsplinePoint = function (t) {
            var spline = { x: 0, y: 0 },
                perpendicularVector = { x: 0, y: 0 },
                p0 = controllPoint2,
                p1 = controllPoint1,
                p2 = start,
                p3 = end,
                S = 1.0 / 6.0,
                t2 = t * t,
                t3 = t2 * t;

            var m = Array
                (
                    Array(-1, 3, -3, 1),
                    Array(3, -6, 3, 0),
                    Array(-3, 0, 3, 0),
                    Array(1, 4, 1, 0)
                );

            var A = (p0.x * m[0][0] + p1.x * m[0][1] + p2.x * m[0][2] + p3.x * m[0][3]),
                B = (p0.x * m[1][0] + p1.x * m[1][1] + p2.x * m[1][2] + p3.x * m[1][3]),
                C = (p0.x * m[2][0] + p1.x * m[2][1] + p2.x * m[2][2] + p3.x * m[2][3]),
                D = (p0.x * m[3][0] + p1.x * m[3][1] + p2.x * m[3][2] + p3.x * m[3][3]),
                E = (p0.y * m[0][0] + p1.y * m[0][1] + p2.y * m[0][2] + p3.y * m[0][3]),
                F = (p0.y * m[1][0] + p1.y * m[1][1] + p2.y * m[1][2] + p3.y * m[1][3]),
                G = (p0.y * m[2][0] + p1.y * m[2][1] + p2.y * m[2][2] + p3.y * m[2][3]),
                H = (p0.y * m[3][0] + p1.y * m[3][1] + p2.y * m[3][2] + p3.y * m[3][3]);
            spline.x = S * (A * t3 + B * t2 + C * t + D);
            spline.y = S * (E * t3 + F * t2 + G * t + H);
            perpendicularVector.x = S * (3 * A * t2 + 2 * B * t + C);
            perpendicularVector.y = S * (3 * E * t2 + 2 * F * t + G);

            var h = Math.sqrt(perpendicularVector.x * perpendicularVector.x + perpendicularVector.y * perpendicularVector.y);
            if (h > maxHeight) { maxHeight = h; }
            return {
                x: spline.x,
                y: spline.y,
                h: h
            };
        };
        sumDistance = 0;
        controllPoint2 = { x: 0, y: 0 };
        controllPoint1 = { x: 0, y: 0 };
        start = { x: 0, y: 0 };
        end = { x: 0, y: 0 };
        // T E S T ================


        // Handle mouse down event for main canvas.
        document.body.onmousedown = function (evt) {
            if (evt.target.tagName != "CANVAS") { return; }
            // Initilize stroke values: color, width,...
            window.drawer.drawContext.strokeStyle = "#000000";
            window.drawer.drawContext.lineJoin = 2;
            window.drawer.drawContext.lineWidth = 1;

            // T E S T ------------------
            start.x = evt.offsetX;
            start.y = evt.offsetY;

            lastDrawPoint.x = evt.offsetX;
            lastDrawPoint.y = evt.offsetY;
            // T E S T ------------------


            // Initialize pathData for stroke.
            // Clear old data.
            window.drawer.pathData = [];
            // window.drawer.pathData.push({ x: evt.offsetX, y: evt.offsetY });
            window.drawer.pushPoint(evt.offsetX, evt.offsetY);

            painting = true;
            window.drawer.drawContext.beginPath();
            window.drawer.drawContext.moveTo(evt.offsetX, evt.offsetY);
            window.drawer.drawContext.stroke();
        };

        // Handle mouse move event for main canvas.
        document.body.onmousemove = function (evt) {
            var distance = window.drawer.distance(lastDrawPoint.x, lastDrawPoint.y, evt.offsetX, evt.offsetY);
            if (painting && distance > SMALLEST_DISTANCE) {
                // T E S T-----------------------------
                end.x = evt.offsetX; end.y = evt.offsetY;

                if (controllPoint2.x && controllPoint2.y) {
                    for (var i = 0; i < STEP_COUNT - 1; i++) {
                        parameter = i / STEP_COUNT;
                        nextParameter = (i + 1) / STEP_COUNT;
                        bSpline = getBsplinePoint(nextParameter);
                        nextPos = bSpline;
                        bSpline = getBsplinePoint(parameter);
                        sumDistance += Math.sqrt((nextPos.x - bSpline.x) * (nextPos.x - bSpline.x) + (nextPos.y - bSpline.y) * (nextPos.y - bSpline.y));
                        if (sumDistance > THRESHOLD_DIST) {
                            window.drawer.pushPoint(bSpline.x, bSpline.y, bSpline.h);
                            // window.drawer.pathData.push({ x: bSpline.x, y: bSpline.y, h: bSpline.h });
                            window.drawer.drawContext.lineTo(bSpline.x, bSpline.y);
                            window.drawer.drawContext.stroke();
                            sumDistance -= THRESHOLD_DIST;
                        }
                    }
                }
                //window.drawer.drawContext.fillStyle = "blue";
                //window.drawer.drawContext.rect(evt.offsetX, evt.offsetY, 5, 5);
                //window.drawer.drawContext.fill();

                controllPoint2 = { x: controllPoint1.x, y: controllPoint1.y };
                controllPoint1 = { x: start.x, y: start.y };
                start = { x: end.x, y: end.y };
                // T E S T-----------------------------
                lastDrawPoint.x = evt.offsetX;
                lastDrawPoint.y = evt.offsetY;
            }
        };

        document.body.onmouseup = function (evt) {
            if (!painting) {
                return;
            }

            // T E S T ================
            sumDistance = 0;
            controllPoint2 = { x: 0, y: 0 };
            controllPoint1 = { x: 0, y: 0 };
            start = { x: 0, y: 0 };
            end = { x: 0, y: 0 };
            // T E S T ================



            // Push data to pathData.
            window.drawer.pushPoint(evt.offsetX, evt.offsetY);
            // window.drawer.pathData.push({ x: evt.offsetX, y: evt.offsetY });
            window.drawer.drawContext.lineTo(evt.offsetX, evt.offsetY);
            window.drawer.drawContext.stroke();

            // Close stroke.
            painting = false;
            window.drawer.drawContext.closePath();

            window.drawer.formImageToPath();
            //window.drawer.testDrawImage();
        };
    });

    var Epsilon = 0.000000000000000000000000000000001;

    // This function create a image from source image and transform it to form to current path.
    window.drawer.formImageToPath = function () {
        var SMALLEST_WIDTH = 5,
        img = $("#fillSource")[0];
        var alpha = $("#slider").slider("option", "value");

        // Create new canvas.
        var newCanvas = $('<canvas/>', { 'width': window.drawer.canvasSize, 'height': window.drawer.canvasSize, 'class': 'canvasPanel' }).appendTo(document.body);
        var drawContext = newCanvas[0].getContext("2d");
        drawContext.imageSmoothingEnabled = false;
        newCanvas[0].height = window.drawer.canvasSize;
        newCanvas[0].width = window.drawer.canvasSize;

        newCanvas.css("-moz-opacity", alpha);/* For older FF versions */
        newCanvas.css("-khtml-opacity", alpha);
        newCanvas.css("opacity", alpha);
        newCanvas.css("-moz-opacity", alpha);

        // Clear canvas content.
        window.drawer.drawContext.clearRect(0, 0, window.drawer.canvasSize, window.drawer.canvasSize);
        var Magic_Degree = 0;
        var numSlices = window.drawer.pathData.length;
        var h = img.height,
            w = img.width, degree = 0;
        var THRESHOLD_DIS = 0.01
        sliceWidth = w / numSlices;

        drawContext.save();

        degree = window.drawer.getDegree(window.drawer.pathData[0].x, window.drawer.pathData[0].y,
                window.drawer.pathData[6].x, window.drawer.pathData[6].y);
        var JUMP_STEPS = 1;

        // iterate over all slices      
        for (var n = 0; n < numSlices - JUMP_STEPS; n += JUMP_STEPS) {
            drawContext.save();

            var id1 = n, id2 = n + 1;
            if (id2 >= numSlices) {
                id2 = numSlices - 1;
                id1 = Math.min(0, numSlices - 1);
            }

            // Get degree of current line with Ox.
            //var currentDegree = window.drawer.getDegree(window.drawer.pathData[id1].x, window.drawer.pathData[id1].y,
            //    window.drawer.pathData[id2].x, window.drawer.pathData[id2].y);
            var currentLine = window.drawer.getLinearEquation(window.drawer.pathData[n - 1], window.drawer.pathData[n], window.drawer.pathData[n + 1]);
            var currentDegree = currentLine.angle;

            currentLine.draw(window.drawer.drawContext, "blue", window.drawer.pathData[n].y - 50, window.drawer.pathData[n].y + 50);
            console.log("<<<%d-height:%d>>>Current degree: %d (%d, %d) ", n, h, Math.round(currentDegree / Math.PI * 180), window.drawer.pathData[n].x, window.drawer.pathData[n].y);

            var adjustment = 1.1;
            var nextLine = window.drawer.getLinearEquation(window.drawer.pathData[n], window.drawer.pathData[n + 1], window.drawer.pathData[n + 2]);
            nextLine.draw(window.drawer.drawContext, "green", window.drawer.pathData[n + 1].y - 50, window.drawer.pathData[n + 1].y + 50);
            var sheight = window.drawer.pathData[n].h ? Math.min(h, ((window.drawer.pathData[n].h) / maxHeight * h + h * adjustment) / (1 + adjustment)) : h;
            var perLine1 = new LinearEquation(-1 / currentDegree.a, window.drawer.pathData[n].y + window.drawer.pathData[n].x * 1 / currentDegree.a);

            var deltaAngle = currentDegree - degree;

            // Translate to correct position.
            drawContext.translate(window.drawer.pathData[n].x, window.drawer.pathData[n].y);

            drawContext.rotate(currentDegree);

            // Calculate small adjustment.           
            console.log("Changed degree from %d to %d ", Math.round(degree / Math.PI * 180), Math.round(currentDegree / Math.PI * 180));
            degree = currentDegree;

            drawContext.globalCompositeOperation = "destination-out";
            // OLD METHOD.
            drawContext.drawImage(img, n * sliceWidth, 0,
               Math.max(sliceWidth, 1), img.height,
               0, -sheight / 2,
               Math.max(sliceWidth, SMALLEST_WIDTH), sheight);
            drawContext.globalCompositeOperation = "source-over";
            // OLD METHOD.
            drawContext.drawImage(img, n * sliceWidth, 0,
               Math.max(sliceWidth, 1), img.height,
               0, -sheight / 2,
               Math.max(sliceWidth, SMALLEST_WIDTH), sheight);

            console.log("Max  %d to %d ", Math.round(degree / Math.PI * 180), Math.round(currentDegree / Math.PI * 180));

            drawContext.restore();
        }

        drawContext.restore();
        maxHeight = 0;
    };

    var isXIncrease = 0, isYIncrease = 0;
    window.drawer.pushPoint = function (x, y, h) {
        if (window.drawer.pathData.length > 2) {
            var xSubtract = window.drawer.pathData[window.drawer.pathData.length - 1].x -
                window.drawer.pathData[window.drawer.pathData.length - 2].x;
            var ySubtract = window.drawer.pathData[window.drawer.pathData.length - 1].y -
                window.drawer.pathData[window.drawer.pathData.length - 2].y;

            if (Math.abs(isXIncrease - xSubtract) > 0.0001 || Math.abs(isYIncrease - ySubtract) > 0.0001) {
                isXIncrease = xSubtract;
                isYIncrease = ySubtract;
                var degree = window.drawer.getDegree(
                    window.drawer.pathData[window.drawer.pathData.length - 1].x,
                    window.drawer.pathData[window.drawer.pathData.length - 1].y,
                    window.drawer.pathData[window.drawer.pathData.length - 3].x,
                    window.drawer.pathData[window.drawer.pathData.length - 3].y,
                    window.drawer.pathData[window.drawer.pathData.length - 2].x,
                    window.drawer.pathData[window.drawer.pathData.length - 2].y)
                console.log("a: %d", degree);
                if (degree < 0.05) {
                    return;
                }
            }
        }

        window.drawer.pathData.push({ x: x, y: y, h: h });
    }

    window.drawer.getTopBottom = function (currentLine, currentPoint, sheight, nextLine) {
        window.drawer.drawLine(window.drawer.drawContext, "orange", currentPoint.x, currentPoint.y);
        var top = 1, bottom = 1;
        // Find x: 

        // We have: x1*x1*(1 + a*a) + x1*(-2*c      +2*a*b      -2*a*d)     + c*c     + b*b      +2*b*d          +d*d - R*R = 0
        // and current line: y1 = a*x1 + b.

        var aa = 1 + currentLine.a * currentLine.a,
            bb = 2 * (-currentPoint.x + currentLine.a * currentLine.b - currentLine.a * currentPoint.y),
            cc = currentPoint.x * currentPoint.x + currentLine.b * currentLine.b + currentPoint.y * currentPoint.y -
			2 * currentLine.b * currentPoint.y - sheight * sheight / 4;
        var delta = bb * bb - 4 * aa * cc;

        if (delta >= 0 && currentLine.a != 0) {
            var x1 = (-bb - Math.sqrt(delta)) / (2 * aa);
            var x2 = (-bb + Math.sqrt(delta)) / (2 * aa);
            var topPoint = { x: x1, y: x1 * currentLine.a + currentLine.b },
                bottomPoint = { x: x2, y: x2 * currentLine.a + currentLine.b };
            // We have linear equation of 2 lines that are perpendicular with currentLine.
            var perTopLine = new LinearEquation(-1 / currentLine.a, topPoint.y + 1 / currentLine.a * topPoint.x);
            var perBottomLine = new LinearEquation(-1 / currentLine.a, bottomPoint.y + 1 / currentLine.a * bottomPoint.x);
            var topRPoint = perTopLine.findIntersectPoint(nextLine);
            var bottomRPoint = perBottomLine.findIntersectPoint(nextLine);

            var curNxtIntersect = currentLine.findIntersectPoint(nextLine);

            top = window.drawer.distance(topPoint.x, topPoint.y, topRPoint.x, topRPoint.y);
            bottom = window.drawer.distance(bottomPoint.x, bottomPoint.y, bottomRPoint.x, bottomRPoint.y);

            window.drawer.drawLine(window.drawer.drawContext, "green", topPoint.x, topPoint.y, topRPoint.x, topRPoint.y);
            window.drawer.drawLine(window.drawer.drawContext, "orqange", bottomPoint.x, bottomPoint.y, bottomRPoint.x, bottomRPoint.y);
        }
        var max = Math.max(top, bottom);
        var min = Math.min(top, bottom);
        var isReducing = nextLine.angle > currentLine.angle || currentLine.angle - nextLine.angle > Math.PI / 2;
        return { top: isReducing ? max : min, bottom: isReducing ? min : max };
    }

    window.drawer.copy = function (srcCanvas, desCanvas, x, y, w, h) {
        var image = new Image();
        img.src = srcCanvas.toDataURL("image/png");

        img.style.zIndex = i;
        img.width = Number(srcCanvas.style.width.replace("px", ""));
        img.height = Number(srcCanvas.style.height.replace("px", ""));

        img.onload = function () {
            var savedContext = desCanvas.getContext('2d');
            savedContext.drawImage(this, x, y,
            this.width, this.height);
        };
    };

     window.drawer.testDrawImage = function () {
        img = $("#fillSource")[0];
        var alpha = $("#slider").slider("option", "value");

        // Create new canvas.
        var newCanvas = $('<canvas/>', { 'width': window.drawer.canvasSize, 'height': window.drawer.canvasSize, 'class': 'canvasPanel' }).appendTo(document.body);
        var drawContext = newCanvas[0].getContext("2d");
        newCanvas[0].height = window.drawer.canvasSize;
        newCanvas[0].width = window.drawer.canvasSize;

        newCanvas.css("-moz-opacity", alpha);/* For older FF versions */
        newCanvas.css("-khtml-opacity", alpha);
        newCanvas.css("opacity", alpha);
        newCanvas.css("-moz-opacity", alpha);

        var h = img.height,
            w = img.width,
            lengthPath = window.drawer.pathData.length,
            JUMP_STEP = 4;

        var linear1 = window.drawer.getLinearEquation({ x: 400, y: 900 }, { x: 500, y: 800 }, { x: 600, y: 900 });
        var linear2 = window.drawer.getLinearEquation({ x: 400, y: 900 }, { x: 500, y: 800 }, { x: 700, y: 900 });
        var linear4 = window.drawer.getLinearEquation({ x: 400, y: 900 }, { x: 500, y: 800 }, { x: 500, y: 900 });
        var linear5 = window.drawer.getLinearEquation({ x: 300, y: 900 }, { x: 500, y: 800 }, { x: 600, y: 900 });
        var linear6 = window.drawer.getLinearEquation({ x: 300, y: 900 }, { x: 500, y: 800 }, { x: 700, y: 900 });
        var linear6 = window.drawer.getLinearEquation({ x: 300, y: 900 }, { x: 500, y: 1000 }, { x: 700, y: 900 });

        window.drawer.drawImage(drawContext, img, 0, img.width, img.height,
            { x: 0, y: 0 }, { x: 290, y: 0 }, { x: 50, y: 200 }, { x: 350, y: 200 }, img.height, Math.atan(Math.PI / 3));
    };

    window.drawer.drawImage = function (ctx, img, xSrc, wSrc, hSrc, ltPointDes, rtPointDes, lbPointDes, rbPointDes, hDes, angle) {
        var top = Math.sqrt(Math.pow(ltPointDes.x - rtPointDes.x, 2) + Math.pow(ltPointDes.y - rtPointDes.y, 2));
        var bottom = Math.sqrt(Math.pow(lbPointDes.x - rbPointDes.x, 2) + Math.pow(lbPointDes.y - rbPointDes.y, 2));
        var linearEquation = new LinearEquation(angle, 0);
        var hRatio = hDes / hSrc;

        for (var i = 0; i < hSrc; i++) {
            var left = linearEquation.findX(i);
            var width = ((hSrc - i) * top + (i * bottom)) / hSrc;
            ctx.drawImage(img, xSrc, i, wSrc, 1, left, i * hRatio, width, hRatio);
        }
    };

    window.drawer.getLinearEquation = function (lPoint, cPoint, rPoint) {
        // We have linear equation is: y = ax + b.
        // Now we calculate a:
        var a, a1, a2, angle, angle2;
        if (!lPoint) {
            a1 = window.drawer.getDegree(cPoint.x, cPoint.y, rPoint.x, rPoint.y);
            a2 = a1;
        }
        else
            if (!rPoint) {
                a1 = window.drawer.getDegree(lPoint.x, lPoint.y, cPoint.x, cPoint.y);
                a2 = a1;
            }
            else
                if (!cPoint) {
                    a1 = window.drawer.getDegree(lPoint.x, lPoint.y, rPoint.x, rPoint.y);
                    a2 = a1;
                }
                else {
                    a1 = window.drawer.getDegree(lPoint.x, lPoint.y, cPoint.x, cPoint.y);
                    a2 = window.drawer.getDegree(cPoint.x, cPoint.y, rPoint.x, rPoint.y);
                }

        //angle2 = window.drawer.getDegree(lPoint.x, lPoint.y, rPoint.x, rPoint.y, cPoint.x, cPoint.y);
        angle = (a1 + a2) / 2;
        // a = (a + 3/4* Math.PI) % (2*Math.PI);
        if (Math.abs(angle - a1) > Math.PI / 2 && Math.abs(angle - a2) > Math.PI / 2) {
            angle = (angle + Math.PI) % (2 * Math.PI);
        }
        a = angle % Math.PI == Math.PI / 2 ? 1 : Math.tan((angle + Math.PI * 3 / 2) % (2 * Math.PI));
        // And calculate b:
        var b = cPoint.y - a * cPoint.x;
        ret = new LinearEquation(a, b, angle);
        return ret;
    };

    window.drawer.getRange = function (point, width, degree) {
        var y = point.y - width * Math.cos(degree) / 2, x = point.x - width * Math.sin(degree) / 2;
        var y2 = point.y + width * Math.cos(degree) / 2, x2 = point.x + width * Math.sin(degree) / 2;
        return [{ x: x, y: y }, { x: x2, y: y2 }];
    };

    // Check 2 line that are created by 4 points is intersect or not.
    window.drawer.isIntersect = function (point1, point2, point3, point4) {
        var xA = point1.x, yA = point1.y, xB = point2.x, yB = point2.y,
            xC = point3.x, yC = point3.y, xD = point4.x, yD = point4.y;

        var dAB = window.drawer.distance(xA, yA, xB, yB);
        var dCD = window.drawer.distance(xC, yC, xD, yD);

        // A == B
        if (dAB < Epsilon) {
            // C == D
            if (dCD < Epsilon) {
                var dAC = window.drawer.distance(xA, yA, xC, yC);
                if (dAC < Epsilon) {
                    return true;
                }

                return false;
            }

            // C != D
            var dA = (yD - yC) * (xA - xC) - (xD - xC) * (yA - yC);
            if (Math.abs(dA) > Epsilon) {
                return false;
            }

            if (((window.drawer.distance(xA, yA, xC, yC) <= window.drawer.distance(xC, yC, xD, yD)) &&
                 (window.drawer.distance(xA, yA, xD, yD) <= window.drawer.distance(xC, yC, xD, yD)))) {
                return true;
            }

            return false;
        }

        // A != B
        if (Math.abs(dCD) < Epsilon) // C == D
        {
            var dC = (yB - yA) * (xC - xA) - (xB - xA) * (yC - yA);
            if (Math.abs(dC) > Epsilon) {
                return false;
            }

            if (((window.drawer.distance(xC, yC, xA, yA) <= window.drawer.distance(xA, yA, xB, yB)) &&
                 (window.drawer.distance(xC, yC, xB, yB) <= window.drawer.distance(xA, yA, xB, yB)))) {
                return true;
            }

            return false;
        }

        // C != D
        var delta = (yB - yA) * (xD - xC) - (xB - xA) * (yD - yC);
        var dt = -(yB - yA) * (xC - xA) + (xB - xA) * (yC - yA);

        if (Math.abs(delta) < Epsilon) // delta == 0
        {
            if (Math.abs(dt) > Epsilon) // AB || CD
            {
                return false;
            }

            // AB == CD
            var dII = window.drawer.distance((xC + xD) / 2, (yC + yD) / 2, (xA + xB) / 2, (yA + yB) / 2);
            if (dII > (dAB / 2) + (dCD / 2) + Epsilon) {
                return false;
            }

            if ((dII - ((Math.abs(dAB - dCD)) / 2)) < 0) {
                return true;
            }
            else {
                var dAI2 = (((xC + xD) / 2) - xA) * (((xC + xD) / 2) - xA) +
                           (((yC + yD) / 2) - yA) * (((yC + yD) / 2) - yA);
                var dBI2 = (((xC + xD) / 2) - xB) * (((xC + xD) / 2) - xB) +
                           (((yC + yD) / 2) - yB) * (((yC + yD) / 2) - yB);
                var dCI1 = (((xA + xB) / 2) - xC) * (((xA + xB) / 2) - xC) +
                           (((yA + yB) / 2) - yC) * (((yA + yB) / 2) - yC);
                var dDI1 = (((xA + xB) / 2) - xD) * (((xA + xB) / 2) - xD) +
                           (((yA + yB) / 2) - yD) * (((yA + yB) / 2) - yD);

                if (Math.abs(window.drawer.distance((dAI2 > dBI2) ? xB : xA, (dAI2 > dBI2) ? yB : yA,
                 (dCI1 > dDI1) ? xD : xC, (dCI1 > dDI1) ? yD : yC)) < Epsilon) {
                    return true;
                }
            }
        }
        else // delta != 0
        {
            var kk;
            var x = xC + (xD - xC) * (dt / delta);
            var y = yC + (yD - yC) * (dt / delta);

            kk = (Math.abs(xB - xA) < Epsilon) ? (y - yA) / (yB - yA) : (x - xA) / (xB - xA);
            if (((dt / delta < 0) || (dt / delta > 1)) || ((kk < 0) || (kk > 1))) {
                return false;
            }
            else {
                return true;
            }
        }

        return false;
    };

    // Calculate distance between A and B.
    window.drawer.distance = function (xA, yA, xB, yB) {
        var ret = Math.sqrt((xB - xA) * (xB - xA) + (yB - yA) * (yB - yA));
        return (ret < Epsilon) ? 0 : ret;
    };

    // calculate desired degree to draw. 
    window.drawer.calculateDesiredDegree = function (pointA, pointB, centerCD, degreeCD) {
        var degree1 = window.drawer.getDegree(pointA.x, pointA.y, centerCD.x, centerCD.y);
        var degree2 = window.drawer.getDegree(pointB.x, pointB.y, centerCD.x, centerCD.y);

        return Math.abs(degree1 - degreeCD) < Math.abs(degree2 - degreeCD) ? degree1 : degree2;
    };

    window.drawer.drawLine = function (ctx, color, x1, y1, x2, y2) {
        return;
        ctx.save();
        ctx.strokeStyle = color;
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(x1, y1);

        if (x2 && y2) {
            ctx.lineTo(x2, y2);
        }

        ctx.stroke();

        ctx.beginPath();
        ctx.fillStyle = "brown";
        ctx.arc(x1, y1, 2, 0, Math.PI * 2);
        if (x2 && y2) {
            ctx.arc(x2, y2, 2, 0, Math.PI * 2);
        }

        ctx.fill();
    };

    // Calculate degree that is created by line A(x1,y1) and C(x2,y2) with Ox axis. B(x3, y3) is center point.
    window.drawer.getDegree = function (x1, y1, x2, y2, x3, y3) {
        // if y1 < y2 -> degree > 0, else degree < 0;
        var a = (y1 - y2) / (x1 - x2);
        var returnDegree = Math.atan(a);
        if (x3 && y3) {
            var AB = Math.sqrt(Math.pow(x3 - x1, 2) + Math.pow(y3 - y1, 2));
            var BC = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2));
            var AC = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            returnDegree = Math.acos((BC * BC + AB * AB - AC * AC) / (2 * BC * AB));
        }

        if (Math.abs(returnDegree) > Math.PI) {
            alert("larger");
        }

        if (y1 <= y2) {
            if (returnDegree < 0) {
                return Math.PI + returnDegree;
            }
        }
        else if (returnDegree >= 0) {
            return (Math.PI + returnDegree);
        }

        return (returnDegree + Math.PI * 2) % (Math.PI * 2);
    };

    function LinearEquation(a, b, angle) {
        this.a = a;
        this.b = b;
        this.angle = angle ? angle : (Math.atan(a) + 3 / 2 * Math.PI) % (Math.PI * 2);
        this.findX = function (y) {
            return (y - b) / a;
        }

        this.draw = function (ctx, color, fromY, toY) {
            return;
            ctx.save();
            ctx.strokeStyle = color;
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(this.findX(fromY), fromY);
            ctx.lineTo(this.findX(toY), toY);
            ctx.stroke();
        }

        this.findIntersectPoint = function (linearEquation) {
            var x = -(b - linearEquation.b) / (a - linearEquation.a);
            return { x: x, y: a * x + b };
        }
    };
})();