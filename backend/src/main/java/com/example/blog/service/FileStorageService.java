package com.example.blog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // Folder where files will be saved
    private final String uploadDir = "uploads";

    public String storeFile(MultipartFile file) {
        // Get original file name
        String originalFilename = file.getOriginalFilename();
        
        // Create a unique file name (to avoid duplicates)
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID() + fileExtension;

        try {
            // Full path: uploads/abc123.jpg
            Path filePath = Paths.get(uploadDir).resolve(uniqueFileName).normalize();
            
            // Make sure the 'uploads' folder exists
            Files.createDirectories(filePath.getParent());
            
            // Save the file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return just the file name (e.g., "abc123.jpg")
            return "http://localhost:8080/api/files/" + uniqueFileName;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + uniqueFileName, e);
        }
    }

    // This method gives you the public URL to access the file
    public String getFileUrl(String fileName) {
        return "/api/files/" + fileName;
    }
}