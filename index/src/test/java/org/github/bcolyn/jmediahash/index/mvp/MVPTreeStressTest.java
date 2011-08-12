package org.github.bcolyn.jmediahash.index.mvp;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.github.bcolyn.jmediahash.index.distances.Distance;
import org.github.bcolyn.jmediahash.index.distances.LongHammingDistance;
import org.github.bcolyn.jmediahash.index.dummy.DummyHashIndex;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.github.bcolyn.jmediahash.index.Util.range;

public class MVPTreeStressTest {
    private Distance<Long> d = new LongHammingDistance();
    private Random rnd = new Random();

    @Test
    public void stressTest() {
        Logger logger = Logger.getLogger(getClass().getPackage().getName());
        Level oldlvl = logger.getLevel();
        logger.setLevel(Level.WARN);
        long start = System.currentTimeMillis();
        int i = 0;
        while (System.currentTimeMillis() - start < 1000) {
            runRandomDataSet();
            i++;
        }
        logger.setLevel(oldlvl);
        logger.info("Completed " + i + " iterations");
    }

    private void runRandomDataSet() {
        MVPTree<Long> tree = new MVPTree<Long>();
        DummyHashIndex<Long> dummy = new DummyHashIndex<Long>();
        long query = rnd.nextLong() % 275;
        double query_radius = Math.random() * 3;
        tree.init(d);
        dummy.init(d);
        List<Long> data = range(0, 255);
        Collections.shuffle(data);
        tree.addAll(data);
        dummy.addAll(data);
        List<Long> expected = dummy.findSimilar(query, query_radius);
        Collections.sort(expected);
        List<Long> actual = tree.findSimilar(query, query_radius);
        Collections.sort(actual);
        Assert.assertEquals(expected, actual);
    }
}