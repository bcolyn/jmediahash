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

package org.github.bcolyn.jmediahash.index;



import org.github.bcolyn.jmediahash.index.distances.Distance;

import java.io.Serializable;
import java.util.List;

public interface HashIndex<T extends Serializable> {
    void init(Distance<T> distanceFunc);
    List<T> findSimilar(T value, double radius);
    void add(T value);
    void addAll(List<T> data);
}
