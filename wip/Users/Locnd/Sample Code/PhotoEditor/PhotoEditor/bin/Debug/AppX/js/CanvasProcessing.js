(function () {
    "use strict";

    window.CanvasProcessing = {};
    window.CanvasProcessing.Filter = null;
    
    window.CanvasProcessing.runFilter = function (id, img, arg1, arg2, arg3) {
        if (!window.CanvasProcessing.Filter) {
            window.CanvasProcessing.Filter = Filters.grayscale;
        }
        
        var c = document.getElementById(id);
        var idata = Filters.filterImage(window.CanvasProcessing.Filter, img, arg1, arg2, arg3);
        c.width = idata.width;
        c.height = idata.height;
        var ctx = c.getContext('2d');
        ctx.putImageData(idata, 0, 0);
    };
    

})();