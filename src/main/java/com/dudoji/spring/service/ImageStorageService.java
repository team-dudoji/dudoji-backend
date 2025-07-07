package com.dudoji.spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Service
public class ImageStorageService {

    private final Path imageStorageLocation;

    public ImageStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.imageStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.imageStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload dir!", e);
        }
    }

    public String storeImageToRandomName(MultipartFile file, String path) {
        String fileName = path + generateTimeBasedFileName() + getFileExtension(file.getOriginalFilename());
        return storeImageActually(file, fileName);
    }

    public String storeImageWithPathName(MultipartFile file, String pathName) {
        String fileName = pathName + getFileExtension(file.getOriginalFilename());
        return storeImageActually(file, fileName);
    }

    public String storeImageActually(MultipartFile file, String fileName) {
        try {
            Path targetLocation = this.imageStorageLocation.resolve(fileName);

            Path parentDir = targetLocation.getParent();
            if (Files.notExists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file!", e);
        }
    }


    public String generateTimeBasedFileName() {
        String currentTime = Instant.now().toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(currentTime.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
            return originalFilename.substring(dotIndex);  // 점(.) 포함해서 반환, 예: ".jpg"
        } else {
            return "";  // 확장자가 없는 경우 빈 문자열 반환
        }
    }
}
