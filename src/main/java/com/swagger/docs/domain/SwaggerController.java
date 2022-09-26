package com.swagger.docs.domain;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 api")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SwaggerController {
        @Operation(summary = "회원가입", description = "회원가입 메서드입니다.")
        @PostMapping("/signUp")
        public void signUp(@RequestParam String id) {
            System.out.println("id = " + id);
        }
}
