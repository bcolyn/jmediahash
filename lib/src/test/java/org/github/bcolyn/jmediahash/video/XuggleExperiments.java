package org.github.bcolyn.jmediahash.video;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class XuggleExperiments {
    private final static Logger LOGGER = LoggerFactory.getLogger(XuggleExperiments.class);

    @Test
    public void decodeVid() throws URISyntaxException {
        File mediaFile = new File(getClass().getResource("/video/video.flv").toURI());
        IMediaReader reader = ToolFactory.makeReader(mediaFile.getAbsolutePath());
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        reader.addListener(new MediaListenerAdapter() {
            public void onVideoPicture(IVideoPictureEvent evt) {
                if (evt.getMediaData().isKeyFrame()) {
                    try {
                        if (evt.getImage() != null) {
                            File file = new File("E:\\TEMP\\images\\keyframe-" + System.currentTimeMillis() + ".jpg");
                            ImageIO.write(evt.getImage(), "jpg", file);
                        } else {
                            System.out.println("Dropping keyframe");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        while (reader.readPacket() == null) {
        }
    }


}
