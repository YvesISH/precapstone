package com.ishimwe.precapstone.service;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUpload {
    String UploadFile (MultipartFile multipartFile) throws IOException;
    Resource downloadFile(String publicId) throws IOException;
}
