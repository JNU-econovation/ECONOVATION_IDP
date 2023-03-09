package com.econovation.idp.application.service;

import com.econovation.idp.application.port.out.LoadAccountPort;
import com.econovation.idp.domain.dto.ImageUploadDto;
import com.econovation.idp.domain.image.Image;
import com.econovation.idp.domain.image.ImageRepository;
import com.econovation.idp.domain.user.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private static final String NO_MATCH_IMAGE = "업로드 된 이미지가 없습니다";
    private final ImageRepository imageRepository;
    private final LoadAccountPort loadAccountPort;
    private final ResourceLoader resourceLoader;

    /* 이미지 업로드 폴더 */
    @Value("${file.path}")
    private String uploadFolder;

    /* 검색 */
    public List<Image> imageSearch(Long idpId) {
        log.info("uploadFolder : " + uploadFolder);
        Account account = loadAccountPort.loadById(idpId);
        return imageRepository.findByAccount(account);
    }

    /* 이미지 업로드 */
    @Transactional
    public void upload(ImageUploadDto imageUploadDto) {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + imageUploadDto.getFile().getOriginalFilename();
        Path imageFilePath = Paths.get(uploadFolder + imageFileName);

        Long idpId = imageUploadDto.getIdpId();
        Account account = loadAccountPort.loadById(idpId);

        try {
            Path write = Files.write(imageFilePath, imageUploadDto.getFile().getBytes());
            log.info(String.valueOf(write));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Image image = imageUploadDto.toEntity(account, imageFileName);
        imageRepository.save(image);
    }

    public Map<String, Object> downloadImage(List<Image> images) throws IOException {
        Map<String, Object> map = new HashMap<>();

        Image image = images.stream()
                .max(Comparator.comparing(Image::getModifiedDate))
                .orElseThrow(NoSuchElementException::new);
        String postImageUrl = image.getPost_image_url();
        Path imageUrl = Paths.get(uploadFolder + postImageUrl);
        Resource resource = resourceLoader.getResource(String.valueOf(imageUrl));
        map.put("image", image);
        map.put("resource", resource);
        if(resource.exists()){
            return map;
        }
        throw new NoSuchElementException();
    }
}
