package site.moasis.imageapiserver.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class FilePath {
    private final String path = System.getProperty("user.dir")+"/files/";
}
