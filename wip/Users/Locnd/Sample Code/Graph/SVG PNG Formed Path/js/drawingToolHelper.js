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

    // This function create a image from source image and transform it to form to current path.
    window.drawer.formImageToPath = function () {
        var img = $("#fillSource")[0];
       
        // Clear canvas content.
        window.drawer.drawContext.clearRect(0, 0, window.drawer.canvasSize, window.drawer.canvasSize);
        var Magic_Degree = 0;
        var numSlices = window.drawer.pathData.length;
        var h = img.height, w = img.width, degree = Magic_Degree;
        sliceWidth = w / numSlices;
        window.drawer.drawContext.save();

        // iterate over all slices      
        for (var n = 0; n < numSlices; n++) {
            window.drawer.drawContext.save();
            var id1 = n, id2 = n + 2;
            if (id2 >= numSlices)
            {
                id2 = numSlices - 1;
                id1 = numSlices - 3;
            }

            window.drawer.drawContext.translate(window.drawer.pathData[n].x, window.drawer.pathData[n].y);

            // Get degree of current line with Ox.
            var currentDegree = window.drawer.getDegree(window.drawer.pathData[id1].x, window.drawer.pathData[id1].y,
                window.drawer.pathData[id2].x, window.drawer.pathData[id2].y);

            // Rotate canvas to draw new points.
            //            window.drawer.drawContext.rotate(currentDegree - degree);
            if (Math.abs(currentDegree - degree) < 0.0005 || currentDegree == 0) {
                window.drawer.drawContext.rotate(currentDegree);

                degree = currentDegree;
            }
          
            // Calculate small adjustment.
            var y = -h * Math.cos(degree)/2, x = -h* Math.sin(degree)/2;
            x = 0;
            y = - h / 2;
            // Draw new points.
            window.drawer.drawContext.drawImage(img, n * sliceWidth, 0,
                sliceWidth, h, x, y, Math.max(sliceWidth, 5), h);
            window.drawer.drawContext.restore();
        }

        window.drawer.drawContext.restore();
    };

    window.drawer.getRange = function (point, width, degree)
    {
        var y = -width * Math.cos(degree) / 2, x = -width * Math.sin(degree) / 2;
        var y2 = width * Math.cos(degree) / 2, x2 = width * Math.sin(degree) / 2;
        return [{ x: x, y: y }, { x: x2, y: y2 }];
    }

    // Check 2 line that are created by 4 points is intersect or not.
    window.drawer.isIntersect = function (point1, point2, point3, point4)
    {
        return true;
    }

    window.drawer.getDegree = function (x1, y1, x2, y2) {
        var a = (y1 - y2) / (x1 - x2);
        return Math.atan(a);
    };
})();