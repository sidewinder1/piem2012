(function () {
    "use strict";

    window.CanvasProcessing = {};
    window.CanvasProcessing.Filter = null;
    window.CanvasProcessing.Args = null;
    
    window.CanvasProcessing.runFilter = function (id, img, arg1, arg2) {
        if (!window.CanvasProcessing.Filter) {
            window.CanvasProcessing.Filter = Filters.grayscale;
        }
        
        var c = document.getElementById(id);
        var idata = Filters.filterImage(window.CanvasProcessing.Filter, img, window.CanvasProcessing.Args ,arg1, arg2);
        c.width = idata.width;
        c.height = idata.height;
        var ctx = c.getContext('2d');
        ctx.putImageData(idata, 0, 0);
    };
    

})();