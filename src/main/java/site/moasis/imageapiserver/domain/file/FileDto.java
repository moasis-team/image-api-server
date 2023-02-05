package site.moasis.imageapiserver.domain.file;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public record FileDto (
){
    public record fileResponseDto(
            Long id,
            String displayName,
            Integer size,
            LocalDateTime createAt,
            ByteArrayOutputStream bas
    ){}
}
