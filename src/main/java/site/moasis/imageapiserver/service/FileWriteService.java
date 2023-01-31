package site.moasis.imageapiserver.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import site.moasis.imageapiserver.common.exception.ErrorCode;
import site.moasis.imageapiserver.common.exception.FileIOException;
import site.moasis.imageapiserver.configuration.FilePath;
import site.moasis.imageapiserver.domain.file.FileMetaData;
import site.moasis.imageapiserver.infrastructure.repository.UploadRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileWriteService {

    private final UploadRepository uploadRepository;
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

        if (!allowExtends.contains(Objects.requireNonNull(file.getContentType()).split("/")[1]))
            throw new FileIOException(ErrorCode.INVALID_PARAMETER);

        String uploadFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String storedName = UUID.randomUUID() + uploadFileName;

        Path targetLocation = fileDir.resolve(storedName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileIOException(ErrorCode.FILE_IO_ERROR);
        }

        FileMetaData uploadFile = FileMetaData.builder()
                .displayName(storedName)
                .size((int) file.getSize())
                .uploadDateTime(LocalDateTime.now())
                .build();
        try {
            file.transferTo(new File(filePath.getPath() + storedName));
        }catch (IOException e){
            throw new FileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
        }finally {
            uploadRepository.save(uploadFile);
        }
        return uploadFile.getDisplayName();
    }

    public HttpStatus deleteImage(String file) {
        uploadRepository.deleteByDisplayName(file);
        File target = new File(filePath.getPath()+ file);

        HttpStatus httpStatus;
        if( target.exists() )
            if(target.delete())
                httpStatus = HttpStatus.OK;
            else
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        else httpStatus = HttpStatus.NO_CONTENT;
        return httpStatus;
    }
}
