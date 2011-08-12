/*
 * Copyright 2011 Benny Colyn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.github.bcolyn.jmediahash.images;

import com.mortennobel.imagescaling.ResampleOp;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.github.bcolyn.jmediahash.util.lucene.OpenBitSet;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.util.Arrays;

import static java.lang.Math.*;
import static java.util.Arrays.fill;
import static org.ejml.ops.CommonOps.mult;
import static org.ejml.ops.CommonOps.multTransB;

public class DCTImageHashAlgorithm implements ImageHashAlgorithm {
    public static final int INTERNAL_SIZE = 32;
    private final static DenseMatrix64F dct = dctMatrix(INTERNAL_SIZE);
    public static final int INTERNAL_IMAGE_TYPE = BufferedImage.TYPE_USHORT_GRAY;

    @Override
    public BufferedImage createResizedCopy(BufferedImage originalImage) {
        ResampleOp resampleOp = new ResampleOp(INTERNAL_SIZE, INTERNAL_SIZE);
        ColorConvertOp colorConvertOp = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage result = new BufferedImage(INTERNAL_SIZE, INTERNAL_SIZE, INTERNAL_IMAGE_TYPE);
        colorConvertOp.filter(resampleOp.filter(originalImage, null), result);
        return result;
    }

    @Override
    public OpenBitSet getHash(BufferedImage image) {
        final BufferedImage smallSize;
        if (image.getWidth() == INTERNAL_SIZE &&
                image.getHeight() == INTERNAL_SIZE && image.getType() == INTERNAL_IMAGE_TYPE) {
            // client has prepared the image, we can use this directly
            smallSize = image;
        } else {
            // do out own prepping
            smallSize = createResizedCopy(image);
        }
        DenseMatrix64F imageMatrix = getImageMatrix(smallSize);
        return doHash(imageMatrix);
    }

    @Override
    public OpenBitSet getHash(double[][] imageMatrix) {
        assert (imageMatrix.length == INTERNAL_SIZE);
        assert (imageMatrix[0].length == INTERNAL_SIZE);
        return doHash(new DenseMatrix64F(imageMatrix));
    }

    private OpenBitSet doHash(DenseMatrix64F imageMatrix) {
        DenseMatrix64F result = doDct(imageMatrix);
        double[] sample = extractSample(result, 1, 8);
        return hashSample(sample);
    }

    DenseMatrix64F doDct(DenseMatrix64F imageMatrix) {
        DenseMatrix64F intermediate = new DenseMatrix64F(INTERNAL_SIZE, INTERNAL_SIZE);
        mult(dct, imageMatrix, intermediate);
        DenseMatrix64F result = new DenseMatrix64F(INTERNAL_SIZE, INTERNAL_SIZE);
        multTransB(intermediate, dct, result);
        return result;
    }

    double[] extractSample(DenseMatrix64F matrix, int offset, int size) {
        DenseMatrix64F extract = CommonOps.extract(matrix, offset, offset + size, offset, offset + size);
        return extract.data;
    }

    private DenseMatrix64F getImageMatrix(BufferedImage smallSize) {
        Raster data = smallSize.getData();
        int width = data.getWidth();
        int height = data.getHeight();
        double[] pixels = data.getPixels(0, 0, width, height, new double[width * height]);
        return new DenseMatrix64F(height, width, true, pixels);
    }

    private OpenBitSet hashSample(double[] sample) {
        double median = findMedian(sample);
        OpenBitSet hash = new OpenBitSet();
        for (int i = 0, sampleLength = sample.length; i < sampleLength; i++) {
            double current = sample[i];
            if (current > median) {
                hash.set(i);
            }
        }
        return hash;
    }

    private double findMedian(double[] sample) {
        double[] copy = Arrays.copyOf(sample, sample.length);
        Arrays.sort(copy);
        double m1 = copy[copy.length / 2];
        double m2 = copy[(copy.length / 2) - 1];
        return (m2 + m1) / 2;
    }

    private static DenseMatrix64F dctMatrix(final int N) {
        DenseMatrix64F dctMatrix = new DenseMatrix64F(N, N);
        fill(dctMatrix.data, 1 / sqrt(N));
        final double c1 = sqrt(2.0 / N);
        for (int x = 0; x < N; x++) {
            for (int y = 1; y < N; y++) {
                double value = c1 * cos((PI / 2 / N) * y * (2 * x + 1));
                dctMatrix.set(x, y, value);
            }
        }
        return dctMatrix;
    }

}
