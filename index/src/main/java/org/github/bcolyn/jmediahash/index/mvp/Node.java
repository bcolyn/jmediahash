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

package org.github.bcolyn.jmediahash.index.mvp;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;

public class Node<K> implements Iterable<Node<K>>{
    private final K key;
    private final double[] partitions;
    private final Node<K>[] children;

    public Node(K key, double[] partitions, Node<K>[] children) {
        this.key = key;
        this.partitions = partitions;
        this.children = children;
    }

    public K getKey() {
        return key;
    }

    public double[] getPartitions() {
        return partitions;
    }

    public Node<K>[] getChildren() {
        return children;
    }

    public void prettyPrint(PrintStream ps, int indent) {
        ps.printf("%s%s\n",indent(indent), key.toString());
        //ps.printf("%sValue= %s\n", indent(indent), key.toString());
        //ps.printf("%sDistanceToParent= %f\n", indent(indent), distanceToParent);
        for (int i = 0; i < partitions.length; i++) {
            Node<K> child = children[i];
            if (child != null) {
                //double partition = partitions[i];
                //ps.printf("%sPartition: < %f\n", indent(indent), partition);
                child.prettyPrint(ps, indent + 1);
            }
        }

        Node<K> child = children[partitions.length];
        if (child != null) {
            //ps.printf("%sPartition: remainder\n", indent(indent));
            child.prettyPrint(ps, indent + 1);
        }
    }

    private String indent(int depth) {
        char[] chars = new char[depth];
        Arrays.fill(chars, '-');
        return new String(chars);
    }

    @Override
    public Iterator<Node<K>> iterator() {
        return new NodeIterator<K>(this);
    }

    @Override
    public String toString() {
        return "Node{" +
                "key=" + key +
                '}';
    }
}
