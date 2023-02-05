package site.moasis.imageapiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.moasis.imageapiserver.common.exception.BusinessException;
import site.moasis.imageapiserver.common.exception.ErrorCode;
import site.moasis.imageapiserver.configuration.FilePath;
import site.moasis.imageapiserver.domain.file.FileMetaData;
import site.moasis.imageapiserver.infrastructure.repository.UploadRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileReadService {

    private final UploadRepository uploadRepository;
    private final FilePath filePath;

    public List<FileMetaData> allFiles() {
        return uploadRepository.findAll();
    }

    public ByteArrayOutputStream getResizedImage(Integer width, Integer height, String file) throws IOException {

        uploadRepository.findByDisplayName(file).orElseThrow(() ->
                new BusinessException(ErrorCode.NOT_FOUND, "images.notExist", List.of(file)));

        BufferedImage originalImage = ImageIO.read(new File(filePath.getPath() + file));

        if (width.equals(0)) width = originalImage.getWidth();
        if (height.equals(0)) height = originalImage.getHeight();

        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", bas);
        bas.flush();
        return bas;
    }
}
