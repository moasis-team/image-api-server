package site.moasis.imageapiserver.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.moasis.imageapiserver.domain.file.FileMetaData;
import java.util.Optional;

@Repository
public interface UploadRepository extends JpaRepository<FileMetaData, Long> {

    Optional<FileMetaData> findById(Long id);
    Optional<FileMetaData> findByDisplayName(String name);
    Boolean existsByDisplayName(String name);
    void deleteByDisplayName(String name);
}
