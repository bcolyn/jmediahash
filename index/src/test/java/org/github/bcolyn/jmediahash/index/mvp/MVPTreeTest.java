package org.github.bcolyn.jmediahash.index.mvp;


import org.github.bcolyn.jmediahash.index.distances.Distance;
import org.github.bcolyn.jmediahash.index.distances.LongHammingDistance;
import org.junit.Test;

import java.util.List;


import static java.util.Collections.shuffle;
import static org.github.bcolyn.jmediahash.index.Util.range;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class MVPTreeTest {
    Distance<Long> d = new LongHammingDistance();

    @Test
    public void testFilling() {
        MVPTree<Long> tree = new MVPTree<Long>();
        tree.init(d);
        List<Long> data = range(0, 255);
        shuffle(data);
        tree.addAll(data);
        Node<Long> root = tree.getRoot();
        root.prettyPrint(System.out, 0);
        assertDeepChildDistances(root);
    }

    private void assertDeepChildDistances(Node<Long> vp) {
        Node<Long>[] children = vp.getChildren();
        double[] partitions = vp.getPartitions();

        for (int i = 0; i < partitions.length; i++) {
            double maxDist = partitions[i];
            Node<Long> child = children[i];
            if (child == null) continue;
            for (Node<Long> node : child) { //all nodes, incl current
                assertThat(d.apply(vp.getKey(), node.getKey()), lessThanOrEqualTo(maxDist));
            }
            assertDeepChildDistances(child);
        }
    }

}
