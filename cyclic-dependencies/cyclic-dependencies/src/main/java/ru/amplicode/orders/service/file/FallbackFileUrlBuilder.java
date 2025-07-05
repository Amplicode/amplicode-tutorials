package ru.amplicode.orders.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Service;

@Service
@Fallback
@RequiredArgsConstructor
public class FallbackFileUrlBuilder implements FileUrlBuilder {
    @Value("${storage.bucket}")
    private String bucket;

    @Override
    public String fileUrl(String fileName) {
        return bucket + "/" + fileName;
    }
}
