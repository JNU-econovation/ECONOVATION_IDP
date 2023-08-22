package com.econovation.idpdomain.domains.images;


import com.econovation.idpdomain.domains.users.domain.Accounts;
import com.econovation.idpdomain.domains.users.domain.BaseTimeEntity;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caption;
    private String post_image_url; // 사진을 전달받아서 서버의 특정 폴더에 저장할 것이므로 사진이 저장된 경로를 저장

    @ManyToOne
    @JoinColumn(name = "accounts_id")
    private Accounts account;
}
