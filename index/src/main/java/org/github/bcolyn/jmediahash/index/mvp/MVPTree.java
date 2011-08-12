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

import org.github.bcolyn.jmediahash.index.HashIndex;
import org.github.bcolyn.jmediahash.index.distances.Distance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MVPTree<K extends Serializable> implements HashIndex<K> {
    private Distance<K> d;
    private Node<K> root;
    private static final int NUM_PARTIONS = 4;
    private final static Logger LOGGER = LoggerFactory.getLogger(MVPTree.class);
    private double[] startPartitions = new double[]{1.0, 2.0, 3.0, 4.0};  //FIXME

    @Override
    public void init(Distance<K> distanceFunc) {
        this.d = distanceFunc;
    }

    @Override
    public List<K> findSimilar(K query, double radius) {
        ArrayList<K> results = new ArrayList<K>();
        Stack<Node<K>> toTest = new Stack<Node<K>>();
        LOGGER.debug("-> Searching for {} radius {}", query, radius);
        toTest.push(root);
        while (toTest.size() > 0) {
            final Node<K> node = toTest.pop();
            double distance = d.apply(node.getKey(), query);
            LOGGER.debug("vantage point : {}\tdistance : {} ({} - {})", new Object[]{node.toString(), distance, distance - radius, distance + radius});
            if (distance <= radius) {
                LOGGER.debug("adding vp to results.");
                results.add(node.getKey());
            }
            double lrad = distance - radius;
            double urad = distance + radius;
            for (int i = 0; i < node.getPartitions().length; i++) {
                double ubound = node.getPartitions()[i];
                double lbound = i == 0 ? 0 : node.getPartitions()[i - 1];

                if (overlaps(ubound, lbound, lrad, urad)) {
                    LOGGER.debug("following partition <= {}", node.getPartitions()[i]);
                    Node<K> item = node.getChildren()[i];
                    if (item != null) {
                        toTest.push(item);
                    }
                } else {
                    LOGGER.debug("skipping partition <= {}", node.getPartitions()[i]);
                }
            }
            double lbound = node.getPartitions()[NUM_PARTIONS - 1];
            if (urad > lbound) {
                Node<K> item = node.getChildren()[NUM_PARTIONS];
                LOGGER.debug("following partition remainder");
                if (item != null) {
                    toTest.push(item);
                }
            } else {
                LOGGER.debug("skipping partition remainder");
            }
        }
        return results;
    }

    private boolean overlaps(double ubound, double lbound, double lrad, double urad) {
        return (lrad <= lbound && ubound <= urad) ||  //partition fits inside the radius
                (lbound <= lrad && lrad <= ubound) || //partition contains start of radius
                (lbound <= urad && urad <= ubound);   //partition contains end of radius
    }

    @Override
    public void add(K value) {
        if (root == null) {
            createRootNode(value);
        } else {
            appendNode(value);
        }
    }

    public Node<K> getRoot() {
        return root;
    }

    private void appendNode(K value) {
        Node<K> node = null;
        int part_idx = 0;
        double distance;

        do {
            if (node == null) { //bootstrap
                node = root;
            } else { //continue down the tree
                node = node.getChildren()[part_idx];
            }
            distance = d.apply(value, node.getKey());
            double[] partitions = node.getPartitions();
            for (part_idx = 0; part_idx < partitions.length; part_idx++) {
                if (distance <= partitions[part_idx]) break;
            }
        } while (node.getChildren()[part_idx] != null);

        if (part_idx == NUM_PARTIONS) { //outermost partition
            node.getChildren()[part_idx] = buildNode(value, buildPartitions(distance, distance));
        } else {
            double parent_partition_radius = node.getPartitions()[part_idx];
            node.getChildren()[part_idx] = buildNode(value, buildPartitions(parent_partition_radius, distance));
        }
    }

    private double[] buildPartitions(double parent_partition_radius, double distance) {
        double max = parent_partition_radius + distance;
        double[] partitions = new double[NUM_PARTIONS];

        for (int i = NUM_PARTIONS; i > 0; i--) {
            partitions[i - 1] = i * (max / NUM_PARTIONS);
        }
        return partitions;
    }

    @SuppressWarnings("unchecked") // arrays and generics :-/
    private Node<K> buildNode(K value, double[] partitions) {
        return new Node<K>(value, partitions, new Node[NUM_PARTIONS + 1]);
    }

    private void createRootNode(K value) {
        root = buildNode(value, startPartitions);
    }

    @Override
    public void addAll(List<K> data) {
        for (K elem : data) {
            add(elem);
        }
    }
}
