package org.github.bcolyn.jmediahash.index;


import org.github.bcolyn.jmediahash.index.distances.Distance;
import org.github.bcolyn.jmediahash.index.distances.LongHammingDistance;
import org.github.bcolyn.jmediahash.index.dummy.DummyHashIndex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import static java.util.Arrays.asList;
import static java.util.Collections.shuffle;
import static java.util.Collections.sort;
import static org.github.bcolyn.jmediahash.index.Util.range;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public abstract class HashIndexFunctionalTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(HashIndexFunctionalTest.class);

    protected HashIndex<Long> index;
    private final List<Long> data;
    private final long query;
    private final double query_radius;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(
                new Object[]{
                        asList(0L, 10L, 1L, 2L, 3L, 9L, 5L, 6L, 7L, 8L, 4L), 1L, 1.0
                },
                new Object[]{
                        range(0, 16), 5L, 2.0
                },
                new Object[]{
                        range(1, 16), 1L, 1.0
                },
                new Object[]{
                        range(0, 255), 111L, 2.0
                }
        );
    }

    public HashIndexFunctionalTest(List<Long> data, long query, double query_radius) {
        shuffle(data);
        this.data = data;
        this.query = query;
        this.query_radius = query_radius;
    }

    @Test
    public void compareWithDummy() {
        DummyHashIndex<Long> dummy = new DummyHashIndex<Long>();
        Distance<Long> distance = new LongHammingDistance();
        buildIndex(dummy, distance);
        buildIndex(index, distance);
        List<Long> expected = dummy.findSimilar(query, query_radius);
        sort(expected);
        List<Long> actual = index.findSimilar(query, query_radius);
        sort(actual);
        LOGGER.info("expected\t{}", expected);
        LOGGER.info("actual\t{}", actual);
        assertEquals(expected, actual);
    }

    private void buildIndex(HashIndex<Long> dummy, Distance<Long> distance) {
        dummy.init(distance);
        dummy.addAll(data);
    }
}
