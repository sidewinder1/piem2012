(function () {
    if (!window.drawer) {
        window.drawer = {};
    }

    window.drawer.mainCanvas = null;
    window.drawer.drawContext = null;
    window.drawer.canvasSize = 600;
    window.drawer.pathData = [];

    var painting = false;

    // Initialize some data for this tool as: get main canvas, set mouse events to this canvas.
    $(window).load(function () {
        window.drawer.mainCanvas = $("#canvasPanel")[0];
        window.drawer.drawContext = window.drawer.mainCanvas.getContext("2d");

        // Check undefined.
        if (!window.drawer.mainCanvas) { return; }


        // ------- I N I T I A L I Z E   T E S T ================
        var parameter,
		nextParameter,
		bSpline = { x: 0, y: 0 },
		nextPos = { x: 0, y: 0 },
		THRESHOLD_DIST = 0.3,
		STEP_COUNT = 40;

        var getBsplinePoint = function (t) {
            var spline = { x: 0, y: 0 },
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

            spline.x = S * (
                (p0.x * m[0][0] + p1.x * m[0][1] + p2.x * m[0][2] + p3.x * m[0][3]) * t3 +
                    (p0.x * m[1][0] + p1.x * m[1][1] + p2.x * m[1][2] + p3.x * m[1][3]) * t2 +
                    (p0.x * m[2][0] + p1.x * m[2][1] + p2.x * m[2][2] + p3.x * m[2][3]) * t +
                    (p0.x * m[3][0] + p1.x * m[3][1] + p2.x * m[3][2] + p3.x * m[3][3]));
            spline.y = S * (
                (p0.y * m[0][0] + p1.y * m[0][1] + p2.y * m[0][2] + p3.y * m[0][3]) * t3 +
                    (p0.y * m[1][0] + p1.y * m[1][1] + p2.y * m[1][2] + p3.y * m[1][3]) * t2 +
                    (p0.y * m[2][0] + p1.y * m[2][1] + p2.y * m[2][2] + p3.y * m[2][3]) * t +
                    (p0.y * m[3][0] + p1.y * m[3][1] + p2.y * m[3][2] + p3.y * m[3][3]));

            return {
                x: spline.x,
                y: spline.y
            };
        };
        sumDistance = 0;
        controllPoint2 = { x: 0, y: 0 };
        controllPoint1 = { x: 0, y: 0 };
        start = { x: 0, y: 0 };
        end = { x: 0, y: 0 };
        // T E S T ================


        // Handle mouse down event for main canvas.
        window.drawer.mainCanvas.onmousedown = function (evt) {
            // Initilize stroke values: color, width,...
            window.drawer.drawContext.strokeStyle = "#000000";
            window.drawer.drawContext.lineJoin = 2;
            window.drawer.drawContext.lineWidth = 1;

            // T E S T ------------------
            start.x = evt.offsetX;
            start.y = evt.offsetY;
            // T E S T ------------------

            
            // Initialize pathData for stroke.
            // Clear old data.
            window.drawer.pathData = [];
            window.drawer.pathData.push({ x: evt.offsetX, y: evt.offsetY });

            painting = true;
            window.drawer.drawContext.beginPath();
            window.drawer.drawContext.moveTo(evt.offsetX, evt.offsetY);
            window.drawer.drawContext.stroke();
        };

        // Handle mouse move event for main canvas.
        window.drawer.mainCanvas.onmousemove = function (evt) {

            if (painting) {
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
                            window.drawer.pathData.push({ x: bSpline.x, y: bSpline.y });
                            window.drawer.drawContext.lineTo(bSpline.x, bSpline.y);
                            window.drawer.drawContext.stroke();
                            sumDistance -= THRESHOLD_DIST;
                        }
                    }
                }
                controllPoint2 = { x: controllPoint1.x, y: controllPoint1.y };
                controllPoint1 = { x: start.x, y: start.y };
                start = { x: end.x, y: end.y };
                // T E S T-----------------------------


                // Push data to pathData.
                window.drawer.pathData.push({ x: evt.offsetX, y: evt.offsetY });

                // Draw stroke.
                window.drawer.drawContext.lineTo(evt.offsetX, evt.offsetY);
                window.drawer.drawContext.stroke();
            }
        };

        window.drawer.mainCanvas.onmouseup = function (evt) {
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
            window.drawer.pathData.push({ x: evt.offsetX, y: evt.offsetY });

            // Close stroke.
            painting = false;
            window.drawer.drawContext.closePath();

            window.drawer.formImageToPath();
        };
    });

    var Epsilon = 0.005;

    // This function create a image from source image and transform it to form to current path.
    window.drawer.formImageToPath = function () {
        var img = $("#fillSource")[0];
       
        // Clear canvas content.
        window.drawer.drawContext.clearRect(0, 0, window.drawer.canvasSize, window.drawer.canvasSize);
        var Magic_Degree = 0;
        var numSlices = window.drawer.pathData.length;
        var h = img.height, w = img.width, degree = 0, lastX = -1, lastY = -1;
        sliceWidth = w / numSlices;
        window.drawer.drawContext.save();
        var THRESHOLD_DIS = 0.01
        degree = window.drawer.getDegree(window.drawer.pathData[0].x, window.drawer.pathData[0].y,
                window.drawer.pathData[3].x, window.drawer.pathData[3].y);
        // iterate over all slices      
        for (var n = 0; n < numSlices; n++) {
            if (Math.abs(lastX - window.drawer.pathData[n].x) > THRESHOLD_DIS &&
                Math.abs(lastY - window.drawer.pathData[n].y) > THRESHOLD_DIS) {
                window.drawer.drawContext.save();
           
                var id1 = n, id2 = n + 2;
                if (id2 >= numSlices) {
                    id2 = numSlices - 1;
                    id1 = numSlices - 3;
                }

                // Translate to correct position.
                window.drawer.drawContext.translate(window.drawer.pathData[n].x, window.drawer.pathData[n].y);

                // Get degree of current line with Ox.
                var currentDegree = window.drawer.getDegree(window.drawer.pathData[id1].x, window.drawer.pathData[id1].y,
                    window.drawer.pathData[id2].x, window.drawer.pathData[id2].y);
                console.log("Current degree: %d ", Math.round(currentDegree / Math.PI * 180));

                if (n > 0) {
                    var line1 = window.drawer.getRange(window.drawer.pathData[n - 1], h, degree);
                    var line2 = window.drawer.getRange(window.drawer.pathData[n], h, currentDegree);

                    if (window.drawer.isIntersect(line1[0], line1[1], line2[0], line2[1])
                        || Math.abs(currentDegree - degree) > 0.005) {
                        // degree = currentDegree = (degree * 2 + currentDegree) / 3;
                        currentDegree = window.drawer.calculateDesiredDegree(line1[0], line2[1],
                            window.drawer.pathData[n], currentDegree);
                        console.log("Adjusted current degree to: %d ", Math.round(currentDegree / Math.PI * 180));
                    }
                }
                // Rotate canvas to draw new points.
                //            window.drawer.drawContext.rotate(currentDegree - degree);
                //if () {
                window.drawer.drawContext.rotate(currentDegree);
                console.log("Changed degree from %d to %d ", Math.round(degree / Math.PI * 180), Math.round(currentDegree / Math.PI * 180));
                degree = currentDegree;
                //}

                // Calculate small adjustment.
                var y = -h * Math.cos(degree) / 2, x = -h * Math.sin(degree) / 2;
                x = 0;
                y = -h / 2;
                // Draw new points.
                window.drawer.drawContext.drawImage(img, n * sliceWidth, 0,
                    sliceWidth, h, x, y, Math.max(sliceWidth, 5), h);
            

                window.drawer.drawContext.restore();
                lastX = window.drawer.pathData[n].x;
                lastY = window.drawer.pathData[n].y;
            }
        }

        window.drawer.drawContext.restore();
    };

    window.drawer.getRange = function (point, width, degree)
    {
        var y = point.y - width * Math.cos(degree) / 2, x = point.x - width * Math.sin(degree) / 2;
        var y2 = point.y + width * Math.cos(degree) / 2, x2 = point.x + width * Math.sin(degree) / 2;
        return [{ x: x, y: y }, { x: x2, y: y2 }];
    }

    // Check 2 line that are created by 4 points is intersect or not.
    window.drawer.isIntersect = function (point1, point2, point3, point4)
    {
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
    }

    // Calculate distance between A and B.
    window.drawer.distance = function (xA, yA, xB, yB)
    {
        var ret = Math.sqrt((xB - xA) * (xB - xA) + (yB - yA) * (yB - yA));
        return (ret < Epsilon) ? 0 : ret;
    }

    // calculate desired degree to draw. 
    window.drawer.calculateDesiredDegree = function(pointA, pointB, centerCD, degreeCD)
    {
        var degree1 = window.drawer.getDegree(pointA.x, pointA.y, centerCD.x, centerCD.y);
        var degree2 = window.drawer.getDegree(pointB.x, pointB.y, centerCD.x, centerCD.y);

        return Math.abs(degree1 - degreeCD) < Math.abs(degree2 - degreeCD) ? degree1 : degree2;
    }

    // Calculate degree that is created by line A(x1,y1) and B(x2,y2) with Ox axis.
    window.drawer.getDegree = function (x1, y1, x2, y2) {
        var a = (y1 - y2) / (x1 - x2);
        return Math.atan(a);
    };


})();