package org.github.bcolyn.jmediahash.index;

import java.util.Arrays;
import java.util.List;

public class Util {
    /**
     * creates range of integral values [a,b[
     */
    public static List<Long> range(int a, int b) {
        Long[] result = new Long[b - a];
        for (int i = 0; i < result.length; i++) {
            result[i] = (long) a + i;
        }
        return Arrays.asList(result);
    }
}