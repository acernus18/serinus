package org.maples.serinus.service;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;

@Slf4j
@Service
public class MediaService {
    public void test(String mediaFile) throws Exception {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(mediaFile);
        grabber.start();

        int length = grabber.getLengthInFrames();
        log.info("Grab {}th from {}", length / 2, mediaFile);
        Frame frame = grabber.grabKeyFrame();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        ImageIO.write(converter.convert(frame), "png", new File("D:\\maple\\Downloads\\ttt.png"));
        grabber.stop();
    }
}
