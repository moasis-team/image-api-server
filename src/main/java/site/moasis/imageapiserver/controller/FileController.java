package site.moasis.imageapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.moasis.imageapiserver.common.exception.FileIOException;
import site.moasis.imageapiserver.configuration.FilePath;
import site.moasis.imageapiserver.service.FileReadService;
import site.moasis.imageapiserver.service.FileWriteService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "FileController", description = "파일을 업로드 하고 조회 삭제 가능")
public class FileController {

    private final FileReadService readService;
    private final FileWriteService writeService;
    private final FilePath filePath;

    @GetMapping("/lists")
    public ResponseEntity<?> all() {
        File image = new File(filePath.getPath());
        List<String> files = readService.getDirFiles(image);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(files);
    }

    @PostMapping
    @Operation(description = "png, jpg, jpeg, gif의 이미지 확장자를 가진 파일을 업로드")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(writeService.uploadFile(file)));
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(description = "서버에 저장된 이미지를 원하는 비율로 반환")
    public ResponseEntity<?> getResizedImage(
            @RequestParam(value = "resize", defaultValue = "100") int percent,
            @RequestParam(value = "file") String file) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = readService.getResizedImage(percent, file);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = getImageContentType(imageInByte);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(imageInByte);
    }

    private HttpHeaders getImageContentType(byte[] imageInByte) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageInByte.length);
        return headers;
    }

    @PutMapping
    @Operation(description = "서버에 저장된 이미지를 변경")
    public ResponseEntity<?> updateImage(
            @RequestParam(value = "name") @NotNull String name,
            @RequestParam(value = "file") @NotNull MultipartFile file) throws FileIOException {
        HttpStatus httpStatus = writeService.deleteImage(name);
        var uploadedFile = writeService.uploadFile(file);

        return ResponseEntity
                .status(httpStatus)
                .body(CommonResponse.success(uploadedFile));
    }

    @DeleteMapping
    @Operation(description = "서버에 저장된 이미지를 삭제")
    public ResponseEntity<?> deleteImage(@RequestParam(value = "name") String name) {
        HttpStatus httpStatus = writeService.deleteImage(name);
        return ResponseEntity
                .status(httpStatus)
                .body(CommonResponse.success(name));
    }


}