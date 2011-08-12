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

public class DefaultImageHashService implements ImageHashService {

    private final DCTImageHashAlgorithm dctAlgorithm = new DCTImageHashAlgorithm();

    @Override
    public OpenBitSet getHash(BufferedImage image, Algorithm algorithm) {
        switch (algorithm) {
            case DCT:
                return dctAlgorithm.getHash(image);
            default:
                throw new IllegalArgumentException("Algorithm not supported");
        }
    }
}
