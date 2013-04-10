(function () {
    "use strict";

    window.Filters = {};
   
    window.Filters.getPixels = function (img) {
        var c, ctx;
        if (img.getContext) {
            c = img;
            try { ctx = c.getContext('2d'); } catch (e) { }
        }

        if (!ctx) {
            c = this.getCanvas(img.width, img.height);
            ctx = c.getContext('2d');
            ctx.drawImage(img, 0, 0);
        }
        return ctx.getImageData(0, 0, c.width, c.height);
    };

    window.Filters.getCanvas = function (w, h) {
        var c = document.createElement('canvas');
        c.width = w;
        c.height = h;
        return c;
    };

    window.Filters.filterImage = function (filter, image, var_args) {
        var args = [this.getPixels(image)];
        for (var i = 2; i < arguments.length; i++) {
            args.push(arguments[i]);
        }
        return filter.apply(null, args);
    };

    window.Filters.grayscale = function (pixels, args) {
        var d = pixels.data;
        for (var i = 0; i < d.length; i += 4) {
            var r = d[i];
            var g = d[i + 1];
            var b = d[i + 2];
            // CIE luminance for the RGB
            // The human eye is bad at seeing red and blue, so we de-emphasize them.
            var v = 0.2126 * r + 0.7152 * g + 0.0722 * b;
            d[i] = d[i + 1] = d[i + 2] = v;
        }
        return pixels;
    };

    window.Filters.brightness = function (pixels, adjustment) {
        var d = pixels.data;
        for (var i = 0; i < d.length; i += 4) {
            d[i] += adjustment;
            d[i + 1] += adjustment;
            d[i + 2] += adjustment;
        }
        return pixels;
    };

    window.Filters.threshold = function (pixels, threshold) {
        var d = pixels.data;
        for (var i = 0; i < d.length; i += 4) {
            var r = d[i];
            var g = d[i + 1];
            var b = d[i + 2];
            var v = (0.2126 * r + 0.7152 * g + 0.0722 * b >= threshold) ? 255 : 0;
            d[i] = d[i + 1] = d[i + 2] = v;
        }
        return pixels;
    };

    window.Filters.tmpCanvas = document.createElement('canvas');
    window.Filters.tmpCtx = window.Filters.tmpCanvas.getContext('2d');

    window.Filters.createImageData = function (w, h) {
        return this.tmpCtx.createImageData(w, h);
    };

    window.Filters.convolute = function (pixels, weights, opaque) {
        var side = Math.round(Math.sqrt(weights.length));
        var halfSide = Math.floor(side / 2);
        var src = pixels.data;
        var sw = pixels.width;
        var sh = pixels.height;
        // pad output by the convolution matrix
        var w = sw;
        var h = sh;
        var output = window.Filters.createImageData(w, h);
        var dst = output.data;
        // go through the destination image pixels
        var alphaFac = opaque ? 1 : 0;
        for (var y = 0; y < h; y++) {
            for (var x = 0; x < w; x++) {
                var sy = y;
                var sx = x;
                var dstOff = (y * w + x) * 4;
                // calculate the weighed sum of the source image pixels that
                // fall under the convolution matrix
                var r = 0, g = 0, b = 0, a = 0;
                for (var cy = 0; cy < side; cy++) {
                    for (var cx = 0; cx < side; cx++) {
                        var scy = sy + cy - halfSide;
                        var scx = sx + cx - halfSide;
                        if (scy >= 0 && scy < sh && scx >= 0 && scx < sw) {
                            var srcOff = (scy * sw + scx) * 4;
                            var wt = weights[cy * side + cx];
                            r += src[srcOff] * wt;
                            g += src[srcOff + 1] * wt;
                            b += src[srcOff + 2] * wt;
                            a += src[srcOff + 3] * wt;
                        }
                    }
                }
                dst[dstOff] = r;
                dst[dstOff + 1] = g;
                dst[dstOff + 2] = b;
                dst[dstOff + 3] = a + alphaFac * (255 - a);
            }
        }
        return output;
    };
    

    //brightness = function() {
    //    runFilter('brightness', Filters.brightness, 40);
    //},

    //threshold = function() {
    //    runFilter('threshold', Filters.threshold, 128);
    //},

    //sharpen = function() {
    //    runFilter('sharpen', Filters.convolute,
    //      [ 0, -1,  0,
    //       -1,  5, -1,
    //        0, -1,  0]);
    //},

    //blurC = function() {
    //    runFilter('blurC', Filters.convolute,
    //      [ 1/9, 1/9, 1/9,
    //        1/9, 1/9, 1/9,
    //        1/9, 1/9, 1/9 ]);
    //},

    //sobel = function() {
    //    runFilter('sobel', function(px) {
    //        px = Filters.grayscale(px);
    //        var vertical = Filters.convoluteFloat32(px,
    //          [-1,-2,-1,
    //            0, 0, 0,
    //            1, 2, 1]);
    //        var horizontal = Filters.convoluteFloat32(px,
    //          [-1,0,1,
    //           -2,0,2,
    //           -1,0,1]);
    //        var id = Filters.createImageData(vertical.width, vertical.height);
    //        for (var i=0; i<id.data.length; i+=4) {
    //            var v = Math.abs(vertical.data[i]);
    //            id.data[i] = v;
    //            var h = Math.abs(horizontal.data[i]);
    //            id.data[i+1] = h
    //            id.data[i+2] = (v+h)/4;
    //            id.data[i+3] = 255;
    //        }
    //        return id;
    //    });
    //},

    //custom = function() {
    //    var inputs = document.getElementById('customMatrix').getElementsByTagName('input');
    //    var arr = [];
    //    for (var i=0; i<inputs.length; i++) {
    //        arr.push(parseFloat(inputs[i].value));
    //    }
    //    runFilter('custom', Filters.convolute, arr, true);
    //}

})();