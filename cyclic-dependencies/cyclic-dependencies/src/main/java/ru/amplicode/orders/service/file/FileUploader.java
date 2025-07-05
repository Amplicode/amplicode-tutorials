package ru.amplicode.orders.service.file;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    @SneakyThrows
    String uploadPhoto(MultipartFile file,
                       Long productId);

    @SneakyThrows
    String fileUrl(String fileName);
}
