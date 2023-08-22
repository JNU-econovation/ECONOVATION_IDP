package com.econovation.idpdomain.domains.dto;


import com.econovation.idpdomain.domains.images.Image;
import com.econovation.idpdomain.domains.users.domain.Accounts;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUploadDto {
    private Long idpId;
    private MultipartFile file;
    private String caption;

    public Image toEntity(Accounts account, String post_image_url) {
        return Image.builder()
                .account(account)
                .post_image_url(post_image_url)
                .caption(caption)
                .build();
    }
}
