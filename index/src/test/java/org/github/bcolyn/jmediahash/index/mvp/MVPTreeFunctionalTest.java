package org.github.bcolyn.jmediahash.index.mvp;



import org.github.bcolyn.jmediahash.index.HashIndexFunctionalTest;

import java.util.List;


public class MVPTreeFunctionalTest extends HashIndexFunctionalTest {
    public MVPTreeFunctionalTest(List<Long> data, long query, double query_radius) {
        super(data, query, query_radius);
        index = new MVPTree<Long>();
    }
}
