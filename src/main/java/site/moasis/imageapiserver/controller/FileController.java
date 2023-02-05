package site.moasis.imageapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.moasis.imageapiserver.service.FileReadService;
import site.moasis.imageapiserver.service.FileWriteService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "FileController", description = "파일을 업로드 하고 조회 삭제 가능")
public class FileController {

    private final FileReadService readService;
    private final FileWriteService writeService;

    @GetMapping("/lists") @Operation(description = "DB에 저장 돼 있는 파일 정보를 모두 반환")
    public ResponseEntity<?> findAllFiles(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(readService.allFiles()));
    }

    @PostMapping@Operation(description = "png, jpg, jpeg, gif의 이미지 확장자를 가진 파일을 업로드")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(writeService.uploadFile(file)));
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)@Operation(description = "서버에 저장된 이미지를 원하는 크기로 반환")
    public ResponseEntity<?> getResizedImage(
            @RequestParam(value = "width", defaultValue = "0") int width,
            @RequestParam(value = "height", defaultValue = "0") int height,
            @RequestParam(value = "file") String file) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = readService.getResizedImage(width, height, file);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = getHttpHeaders(imageInByte);

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(CommonResponse.success(imageInByte));
    }

    @DeleteMapping@Operation(description = "서버에 저장된 이미지를 삭제")
    public ResponseEntity<?> deleteImage(@RequestParam(value = "file") String file){
        HttpStatus httpStatus = writeService.deleteImage(file);
        return ResponseEntity
                .status(httpStatus)
                .body(CommonResponse.success(file));
    }

    private HttpHeaders getHttpHeaders(byte[] imageInByte) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(imageInByte.length);
        return headers;
    }
}