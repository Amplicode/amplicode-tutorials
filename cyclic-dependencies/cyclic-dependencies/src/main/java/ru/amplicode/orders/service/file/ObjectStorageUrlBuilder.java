package ru.amplicode.orders.service.file;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("yc")
@RequiredArgsConstructor
public class ObjectStorageUrlBuilder implements FileUrlBuilder {
    private final AmazonS3 amazonS3;

    @Value("${storage.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public String fileUrl(String fileName) {
        return amazonS3.getUrl(
            bucket, fileName
        ).toString();
    }
}
