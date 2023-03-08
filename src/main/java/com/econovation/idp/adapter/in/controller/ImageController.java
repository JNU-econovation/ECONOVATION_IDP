package com.econovation.idp.adapter.in.controller;


import com.econovation.idp.application.service.ImageService;
import com.econovation.idp.domain.dto.ImageUploadDto;
import com.econovation.idp.domain.image.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ImageController {
    private final ImageService imageService;

    /* 이미지 업로드 */
    @PostMapping("/upload")
    public ResponseEntity<?> imageUpload(ImageUploadDto imageUploadDto) {
        if(imageUploadDto.getFile().isEmpty()) {
            throw new IllegalArgumentException("이미지가 첨부되지 않았습니다.");
        }
        imageService.upload(imageUploadDto);
        return new ResponseEntity<>("이미지 업로드 성공", HttpStatus.OK);
    }

    @GetMapping("/image")
    public ResponseEntity<?> story(Integer idpId) throws IOException {
        List<Image> images = imageService.imageSearch(Long.valueOf(idpId));

        Map<String, Object> imageMap = imageService.downloadImage(images);
        Resource resource = (Resource) imageMap.get("resource");
        Image image = (Image) imageMap.get("image");
        imageMap.get("image");
        String contentType = Files.probeContentType(Paths.get(image.getPost_image_url()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(image.getCaption(), StandardCharsets.UTF_8)
                        .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return new ResponseEntity<>("이미지 스토리 불러오기 성공", HttpStatus.OK);
    }
}



