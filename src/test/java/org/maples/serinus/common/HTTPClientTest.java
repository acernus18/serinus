package org.maples.serinus.common;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class HTTPClientTest {
    public void test() throws IOException {
        URL url = new URL("");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Cookie", ";");

        connection.connect();
    }

    @Test
    public void testDownloadFile() throws IOException {
        // https://search.pstatic.net/common?src=https://i.nyahentai.net/galleries/1266854/2.jpg

        String template = "https://search.pstatic.net/common?src=https://i.nyahentai.net/galleries/%d/%d.jpg";
        String targetPath = "D:/maple/Downloads/Nier";

        for (int i = 0; i < 17; i++) {
            URL url = new URL(String.format(template, 1266854, i + 1));
            ReadableByteChannel urlChannel = Channels.newChannel(url.openStream());
            FileOutputStream outputStream = new FileOutputStream(targetPath + i + ".jpg");
            outputStream.getChannel().transferFrom(urlChannel, 0, Long.MAX_VALUE);
            outputStream.close();
        }
    }
}
