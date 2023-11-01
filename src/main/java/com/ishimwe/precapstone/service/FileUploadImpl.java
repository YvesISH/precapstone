package com.ishimwe.precapstone.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload{

    private final Cloudinary cloudinary;
    @Override
    public String UploadFile(MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(),
                        Map.of("public_id", UUID.randomUUID().toString()))
                .get("url")
                .toString();
    }

    @Override
    public Resource downloadFile(String publicId) throws IOException {
        // Generating the download URL for the image based on the publicId
        String downloadUrl = cloudinary.url()
                .resourceType("image")
                .generate(publicId);

        // Retrieving the image content as bytes
        byte[] imageBytes = downloadUrl.getBytes();

        // a ByteArrayResource from the image bytes
        return (Resource) new ByteArrayResource(imageBytes);
    }
}
