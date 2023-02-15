package site.moasis.imageapiserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import site.moasis.imageapiserver.common.exception.ErrorCode;
import site.moasis.imageapiserver.common.exception.FileIOException;
import site.moasis.imageapiserver.configuration.FilePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileWriteService {

    private final FilePath filePath;
    private Path fileDir;

    @PostConstruct
    public void postConstruct() {
        fileDir = Paths.get(filePath.getPath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileDir);
        } catch (IOException ignored) {
            log.error("Fail to create directories");
        }
    }

    public String uploadFile(MultipartFile file) throws FileIOException {
        String allowExtends = "png, jpg, jpeg, gif";
        checkExtends(file, allowExtends);

        return storeFile(file);
    }

    public HttpStatus deleteImage(String name) {
        File target = new File(filePath.getPath() + name);

        return getStatusAfterDelete(target);
    }

    private void checkExtends(MultipartFile file, String allowExtends) throws FileIOException {
        if (!allowExtends.contains(Objects.requireNonNull(file.getContentType()).split("/")[1]))
            throw new FileIOException(ErrorCode.INVALID_PARAMETER);
    }

    private String storeFile(MultipartFile file) throws FileIOException {
        String uploadFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        Path targetLocation = fileDir.resolve(uploadFileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_IO_ERROR);
        }

        try {
            file.transferTo(new File(filePath.getPath() + uploadFileName));
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return uploadFileName;
    }

    private HttpStatus getStatusAfterDelete(File target) {
        HttpStatus httpStatus;
        if (target.exists())
            if (target.delete())
                httpStatus = HttpStatus.OK;
            else
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        else httpStatus = HttpStatus.NO_CONTENT;
        return httpStatus;
    }
}
