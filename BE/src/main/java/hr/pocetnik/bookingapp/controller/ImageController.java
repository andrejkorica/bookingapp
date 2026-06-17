package hr.pocetnik.bookingapp.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import hr.pocetnik.bookingapp.service.ImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) {

        String imageUrl = imageService.uploadImage(file);

        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}