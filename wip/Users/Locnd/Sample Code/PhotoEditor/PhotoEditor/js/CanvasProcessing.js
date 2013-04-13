(function () {
    "use strict";

    window.CanvasProcessing = {};
    window.CanvasProcessing.Filter = null;
    window.CanvasProcessing.Args = {};
    window.CanvasProcessing.Args.Arg1 = 50;
    window.CanvasProcessing.Args.Arg2 = 50;
    window.CanvasProcessing.Args.Arg3 = 50;
    
    window.CanvasProcessing.runFilter = function (img, arg1, arg2) {
        if (!window.CanvasProcessing.Filter) {
            window.CanvasProcessing.Filter = Filters.grayscale;
        }
                
        var idata = Filters.filterImage(window.CanvasProcessing.Filter, img, window.CanvasProcessing.Args ,arg1, arg2);
        window.LayerManager.Current.width = idata.width;
        window.LayerManager.Current.height = idata.height;
        var ctx = window.LayerManager.Current.getContext('2d');
        ctx.putImageData(idata, 0, 0);
    };
    
    window.CanvasProcessing.updateArgs = function (arg1, arg2, arg3) {
        if (arg1.length > 0) {
            window.CanvasProcessing.Args.Arg1 = arg1;
        }
        
        if (arg2.length > 0) {
            window.CanvasProcessing.Args.Arg2 = arg2;
        }
        
        if (arg3.length > 0) {
            window.CanvasProcessing.Args.Arg3 = arg3;
        }

        window.CanvasProcessing.runFilter();
    };

})();