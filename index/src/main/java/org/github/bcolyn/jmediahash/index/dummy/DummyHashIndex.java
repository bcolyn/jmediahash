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

package org.github.bcolyn.jmediahash.index.dummy;



import org.github.bcolyn.jmediahash.index.HashIndex;
import org.github.bcolyn.jmediahash.index.distances.Distance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DummyHashIndex<T extends Serializable> implements HashIndex<T> {
    private Distance<T> d;
    private List<T> values;

    @Override
    public void init(Distance<T> distanceFunc) {
        this.d = distanceFunc;
        values = new ArrayList<T>();
    }

    @Override
    public List<T> findSimilar(T value, double radius) {
        List<T> results = new ArrayList<T>();
        for (T item : values) {
            if (d.apply(value, item) <= radius){
                results.add(item);
            }
        }
        return results;
    }

    @Override
    public void add(T value) {
        values.add(value);
    }

    @Override
    public void addAll(List<T> data) {
        for (T elem : data) {
            add(elem);
        }
    }
}
