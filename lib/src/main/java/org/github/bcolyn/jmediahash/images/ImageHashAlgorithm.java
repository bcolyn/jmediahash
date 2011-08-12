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

import org.github.bcolyn.jmediahash.util.lucene.OpenBitSet;

import java.awt.image.BufferedImage;

/**
 * Service for jmediahash images
 */
public interface ImageHashAlgorithm {

    /**
     * Prepares a bufferedimage for jmediahash, resizes it and converts to grayscale
     *
     * @param originalImage the original image
     * @return the resized, color converted image
     */
    BufferedImage createResizedCopy(BufferedImage originalImage);

    /**
     * Hashes an image.
     * - prepares the image, converts to grayscale and resizes (optional, will not be performed
     * if the input image has the right properties already)
     * - applies 2D discrete cosine transform
     * - extracts the top-left corner of the DCT result
     * - determines for each extracted value if it lies below or above the median - gives 1 or 0 in the final hash
     * <p/>
     * This algorithm is suitable for finding recompressed images, rescaled images or images that have small local
     * edits. It takes the whole image into account, so manipulations which affect the whole image (cropping for instance)
     * cause the hash values to diverge more.
     *
     * @param image the image to hash
     * @return content-based image hash
     */
    OpenBitSet getHash(BufferedImage image);

    /**
     * Hashes raw input of pixel values. matrix should have right dimensions.
     *
     * @param imageMatrix
     * @return
     */
    OpenBitSet getHash(double[][] imageMatrix);
}
