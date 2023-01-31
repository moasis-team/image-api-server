package site.moasis.imageapiserver.domain.file;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaData{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column @NotBlank
    private String displayName;

    @Column @NonNull
    private Integer size;

    @Column @CreatedDate
    private LocalDateTime uploadDateTime;

    @Transient
    private ByteArrayOutputStream outputImage;
}
