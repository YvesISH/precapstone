package com.ishimwe.precapstone.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FileUploadController {
    private final Cloudinary cloudinary;

    public FileUploadController(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam("images") List<MultipartFile> multipartFiles, Model model) {
        try {
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : multipartFiles) {
                // Uploading each image to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageURL = (String) uploadResult.get("url");

                // Add the image URL to the list
                imageUrls.add(imageURL);
            }

            // Adding the list of image URLs to the model
            model.addAttribute("imageURLs", imageUrls);
        } catch (IOException e) {
            // Handle the exception (e.g., log it or show an error message)
            e.printStackTrace();
        }

        return "gallery";
    }


    @GetMapping("/gallery")
    public String displayGallery(Model model) throws Exception {
        // Fetching images from Cloudinary
        // Here, I'm just fetching a list of all uploaded images.
        Map result = cloudinary.api().resources(ObjectUtils.asMap("type", "upload"));
        model.addAttribute("images", result.get("resources"));

        return "picture";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadImage(@RequestParam("publicId") String publicId, Model model) {
        try {
            // Generating the download URL for the image based on the publicId
            String downloadUrl = cloudinary.url().resourceType("image").generate(publicId);

            // Creating an HTTP client
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                // Creating an HTTP GET request for the download URL
                HttpGet httpGet = new HttpGet(downloadUrl);

                // Executing the GET request and obtain the response
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = (HttpEntity) response.getEntity();

                // Checking if the response is successful
                if (response.getStatusLine().getStatusCode() == 200) {
                    // Retrieving the content of the image
                    byte[] content = EntityUtils.toByteArray((org.apache.http.HttpEntity) entity);

                    // Creating a ByteArrayResource to wrap the image content
                    ByteArrayResource resource = new ByteArrayResource(content);

                    // Defining response headers for downloading the image
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + publicId + ".jpg");

                    // Returning a ResponseEntity with the resource and headers
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(resource);
                }
            }
        } catch (Exception e) {
            // Handling the exception (e.g., log it or show an error message)
            e.printStackTrace();
        }

        // If there is an error, return an empty resource or an error resource
        return ResponseEntity.notFound().build();
    }
}