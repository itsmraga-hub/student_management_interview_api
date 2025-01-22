package com.api.student_management.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    private static final String IMAGE_DIRECTORY = "C:/var/log/applications/API/StudentPhotos/"; // Updated path

    @GetMapping("/images/{imageName}")
    public Resource getImage(@PathVariable String imageName) throws Exception {
        Path path = Paths.get(IMAGE_DIRECTORY).resolve(imageName).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new Exception("Could not read the file!");
        }
    }
}
