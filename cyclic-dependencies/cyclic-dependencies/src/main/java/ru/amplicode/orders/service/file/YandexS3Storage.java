package ru.amplicode.orders.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.amplicode.orders.service.ProductService;

import java.util.UUID;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Service
public class YandexS3Storage implements FileUploader {

    private final AmazonS3 amazonS3;
    private final FileUrlBuilder fileUrlBuilder;
    @Value("${storage.bucket}")
    private String bucket;

    @SneakyThrows
    @Override
    public String uploadPhoto(MultipartFile file,
                              Long productId) {
        String fileName = UUID.randomUUID().toString();

        if (file.getOriginalFilename() != null) {
            fileName = fileName + file.getOriginalFilename();
        }

        try {
            amazonS3.putObject(
                new PutObjectRequest(
                    bucket,
                    fileName,
                    file.getInputStream(),
                    new ObjectMetadata()
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @SneakyThrows
    @Override
    public String fileUrl(String fileName) {
        return fileUrlBuilder.fileUrl(fileName);
    }
}
