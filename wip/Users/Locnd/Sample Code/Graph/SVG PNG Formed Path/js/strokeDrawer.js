(function () {
    if (!window.strokeDrawer) {
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

        $("#slider").slider({
            max: 1, min: 0, step: 0.1, value: 1, change: function () {

            }
        });

        window.strokeDrawer = new StrokeDrawer();
        $(window).load(function () {
            window.strokeDrawer.InitializeUI($("#canvasPanel")[0]);
            window.strokeDrawer.InitializeEvents();
        });
    }

    // Define ImagePoint class.
    function ImagePoint(x, y) {
        this.x = x;
        this.y = y;

    };

    function Calculator() {
        var Epsilon = 0.0000000000001;
        this.distance = function (xA, yA, xB, yB) {
            var ret = Math.sqrt((xB - xA) * (xB - xA) + (yB - yA) * (yB - yA));
            return (ret < Epsilon) ? 0 : ret;
        };
    }

    // Define StrokeDrawer class.
    function StrokeDrawer() {
        // public variables.
        this.points = [];
        this.canvasConfig = null;

        // private variables.
        var _isDrawing = false,
            _context = null,
            _canvas = null,
            _startPt = {x:0,y:0},
            _lastPt = { x: 0, y: 0 },
            _endPt = { x: 0, y: 0 },
            _sumDistance = 0,
            _ctrlPt2 = { x: 0, y: 0 },
            _ctrlPt1 = { x: 0, y: 0 },
            _calculator = new Calculator(),
                
            /// CONSTANTS.
            SMALLEST_DISTANCE = 3;

        this.InitializeEvents = function () {
            document.body.onmousedown = _pointerDown;
            document.body.onmousemove = _pointerMove;
            document.body.onmouseup = _pointerUp;
        };

        this.InitializeUI = function(canvas, config){
            // get canvas and context 2d.
            _canvas = canvas;
            _context = canvas.getContext("2d");

            // get config.
            canvasConfig = config;

            // TODO: Config canvas.
            // ...
            // Initilize stroke values: color, width,...
            _context.strokeStyle = "#000000";
            _context.lineJoin = 2;
            _context.lineWidth = 1;

        };

        //------------------ Handle user's gestures. -----------------------//
        // Pointer down event.
        var _pointerDown = function (evt) {
            if (evt.target.tagName != "CANVAS") { return; }
            
            // T E S T ------------------
            _startPt.x = evt.offsetX;
            _startPt.y = evt.offsetY;

            _lastPt.x = evt.offsetX;
            _lastPt.y = evt.offsetY;
            // T E S T ------------------


            // Initialize pathData for stroke.
            // Clear old data.
            points = [];

            _context.beginPath();
            _pushImagePoint(evt.offsetX, evt.offsetY, true);

            _isDrawing = true;
        };

        // Pointer move event.
        var _pointerMove = function (evt) {
            var distance =_calculator.distance(_lastPt.x, _lastPt.y, evt.offsetX, evt.offsetY);
            if (_isDrawing
                // && distance > SMALLEST_DISTANCE
                )
            {
                // T E S T-----------------------------
                _endPt.x = evt.offsetX; _endPt.y = evt.offsetY;

                //if (controllPoint2.x && controllPoint2.y) {
                //    for (var i = 0; i < STEP_COUNT - 1; i++) {
                //        parameter = i / STEP_COUNT;
                //        nextParameter = (i + 1) / STEP_COUNT;
                //        bSpline = getBsplinePoint(nextParameter);
                //        nextPos = bSpline;
                //        bSpline = getBsplinePoint(parameter);
                //        sumDistance += Math.sqrt((nextPos.x - bSpline.x) * (nextPos.x - bSpline.x) + (nextPos.y - bSpline.y) * (nextPos.y - bSpline.y));
                //        if (sumDistance > THRESHOLD_DIST) {
                //            _pushImagePoint(bSpline.x, bSpline.y, true);
                _pushImagePoint(evt.offsetX, evt.offsetY, true);
                //            window.drawer.drawContext.lineTo(bSpline.x, bSpline.y);
                //            window.drawer.drawContext.stroke();
                //            sumDistance -= THRESHOLD_DIST;
                //        }
                //    }
                //}
                //window.drawer.drawContext.fillStyle = "blue";
                //window.drawer.drawContext.rect(evt.offsetX, evt.offsetY, 5, 5);
                //window.drawer.drawContext.fill();

                //controllPoint2 = { x: controllPoint1.x, y: controllPoint1.y };
                //controllPoint1 = { x: start.x, y: start.y };
                _startPt = { x: _endPt.x, y: _endPt.y };
                // T E S T-----------------------------
                _lastPt.x = evt.offsetX;
                _lastPt.y = evt.offsetY;
            }
        };


        // Pointer up event.
        var _pointerUp = function (evt) {
            if (!_isDrawing) {
                return;
            }

            // T E S T ================
            _sumDistance = 0;
            _ctrlPt1 = { x: 0, y: 0 };
            _ctrlPt2 = { x: 0, y: 0 };
            _startPt = { x: 0, y: 0 };
            _endPt = { x: 0, y: 0 };
            // T E S T ================



            // Push data to pathData.
            _pushImagePoint(evt.offsetX, evt.offsetY, true);
            
            // Close stroke.
            _isDrawing = false;
            _context.closePath();

            _fillImageToPath();
        };

        var _pushImagePoint = function (x, y, isDraw) {
            var imagePoint = new ImagePoint(x, y);
            points.push(imagePoint);

            if (isDraw) {
                _context.moveTo(x, y);
                _context.stroke();
            }
        };

        var _fillImageToPath = function () {
        };
    };
})();