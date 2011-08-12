package org.github.bcolyn.jmediahash.images;

import org.ejml.data.DenseMatrix64F;
import org.github.bcolyn.jmediahash.util.lucene.OpenBitSet;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class DCTImageHashAlgorithmTest {
    private static final double DEFAULT_THRESHOLD = 15.0;

    @Test
    public void testGetHash() throws Exception {
        URL url = getClass().getResource("/sample/");
        File dir = new File(url.toURI());
        ImageHashAlgorithm algorithm = new DCTImageHashAlgorithm();
        List<OpenBitSet> results = new ArrayList<OpenBitSet>();
        for (File file : dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("jpg");
            }
        })) {
            BufferedImage bufferedImage = ImageIO.read(file);
            OpenBitSet hash = algorithm.getHash(bufferedImage);
            System.out.printf("%s\t%x\n", file.toString(), hash.getBits()[0]);
            results.add(hash);
        }

        double min = Double.MAX_VALUE;
        for (int i = 0; i < results.size(); i++) {
            for (int j = i + 1; j < results.size(); j++) {
                OpenBitSet hash1 = results.get(i);
                OpenBitSet hash2 = results.get(j);
                hash1.xor(hash2);
                double distance = hash1.cardinality();
                if (distance < min) min = distance;
                assertThat(distance, greaterThan(DEFAULT_THRESHOLD));
            }
        }
        System.out.println("Minimum distance:" + Double.valueOf(min));
    }

    @Test
    public void testEqual() throws IOException {
        BufferedImage png = ImageIO.read(getClass().getResource("/sample/IMG_1548.png"));
        String[] resources = new String[]{
                "/sample/IMG_1548.jpg", //just recompressed
                "/sample/IMG_1548_crayon.jpe", //edited with crayon
                "/sample/IMG_1548_scaled.jpe", //scaled back a lot
                "/sample/IMG_1548_cropped.jpe", //cropped - this is tough
        };
        ImageHashAlgorithm algorithm = new DCTImageHashAlgorithm();
        OpenBitSet pnghash = algorithm.getHash(png);
        double max = 0;
        for (String resource : resources) {
            BufferedImage jpg = ImageIO.read(getClass().getResource(resource));
            OpenBitSet jpghash = algorithm.getHash(jpg);
            jpghash.xor(pnghash);
            double distance = jpghash.cardinality();
            System.out.printf("Distance for %s = %f\n", resource, distance);
            if (distance > max) max = distance;
            assertThat(distance, lessThanOrEqualTo(DEFAULT_THRESHOLD));
        }
        System.out.println("Maxiumum distance:" + Double.valueOf(max));
    }

    @Test
    public void testSampling() {
        DCTImageHashAlgorithm service = new DCTImageHashAlgorithm();
        DenseMatrix64F result = service.doDct(new DenseMatrix64F(Data.reference_imgdata));
        double[] sample = service.extractSample(result, 1, 8);
        System.out.println(Arrays.toString(Data.reference_sample));
        System.out.println(Arrays.toString(sample));
        assertEqualsDoubleArray(Data.reference_sample, sample, 0.1);
    }

    private void assertEqualsDoubleArray(double[] reference_hash, double[] sample, double tolerance) {
        assertTrue(reference_hash.length == sample.length);
        for (int i = 0; i < reference_hash.length; i++) {
            assertTrue("value not within tolerance:" + Double.toString(sample[i]), Math.abs(reference_hash[i] - sample[i]) < tolerance);
        }
    }

    @Test
    public void testReferenceHash() {
        ImageHashAlgorithm algorithm = new DCTImageHashAlgorithm();
        OpenBitSet actual = algorithm.getHash(Data.reference_imgdata);
        OpenBitSet exp = new OpenBitSet(new long[]{Data.reference_hash}, 1);
        System.out.println(Long.toBinaryString(Data.reference_hash));
        System.out.println(Long.toBinaryString(actual.getBits()[0]));
        assertEquals(exp, actual);
    }
}
