package com.econovation.idp.adapter.in.controller;

import com.econovation.idp.application.service.ImageService;
import com.econovation.idp.domain.dto.ImageUploadDto;
import com.econovation.idp.domain.image.Image;
import com.econovation.idp.global.common.exception.ImageIOException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
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
    public void downloadImage(HttpServletResponse response,Integer idpId) throws IOException {
        List<Image> images = imageService.imageSearch(Long.valueOf(idpId));

        String url = imageService.downloadImage(images);
        String contentType = Files.probeContentType(Paths.get(url));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(idpId.toString(), StandardCharsets.UTF_8)
                        .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        outputImage(response, url);
    }

    private void outputImage(HttpServletResponse response, String imagePath) {
        File file = new File(imagePath);
        if (!file.isFile()) {
            throw new ImageIOException("이미지를 불러오는 중에 문제가 생겼습니다.");
        }

        FileInputStream fis = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream bStream = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            bStream = new ByteArrayOutputStream();
            int imgByte;
            while ((imgByte = in.read()) != -1) {
                bStream.write(imgByte);
            }

            String type = "";
            String ext = FilenameUtils.getExtension(file.getName());
            if (!ext.isEmpty()) {
                if (ext.equalsIgnoreCase("jpg")) {
                    type = "image/jpeg";
                } else {
                    type = "image/" + ext.toLowerCase();
                }
            }

            response.setHeader("Content-Type", type);
            response.setContentLength(bStream.size());
            bStream.writeTo(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            throw new ImageIOException(e.getMessage());
        } finally {
            try {
                if (bStream != null) {
                    bStream.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                throw new ImageIOException(e.getMessage());
            }
        }
    }
}



