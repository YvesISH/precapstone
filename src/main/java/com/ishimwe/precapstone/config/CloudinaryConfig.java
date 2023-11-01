package com.ishimwe.precapstone.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;


import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "do6ppagwx",
                "api_key", "839832658887975",
                "api_secret", "YIILPqEd1mQKkTb7EJIxE2w6KYU"
        ));
    }
}