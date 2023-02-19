package site.moasis.imageapiserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.moasis.imageapiserver.configuration.FilePath;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileReadService {

    private final FilePath filePath;

    public ByteArrayOutputStream getResizedImage(int percent, String file) throws IOException {

        BufferedImage originalImage = ImageIO.read(new File(filePath.getPath() + file));
        float width = originalImage.getWidth() * percent * 0.01f;
        float height = originalImage.getHeight() * percent * 0.01f;

        return getByteArrayOutputStream(resize(originalImage, (int)width, (int)height));
    }

    public List<String> getDirFiles(File f) {

        List<String> fileList = new ArrayList<>();
        File[] files = f.listFiles();
        for (File file : files)
            fileList.add(file.getName());

        return fileList;
    }

    private ByteArrayOutputStream getByteArrayOutputStream(BufferedImage resizedImage) throws IOException {

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", bas);
        bas.flush();

        return bas;
    }

    private BufferedImage resize(BufferedImage originalImage, int width, int height) {

        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }
}
