//package com.econovation.idpapi.adapter.in.controller;
//
//import com.econovation.idpapi.adapter.in.controller.AccountController;
//import com.econovation.idpapi.application.port.in.AccountUseCase;
//import com.econovation.idpdomain.domains.dto.LoginRequestDto;
//import com.econovation.idpdomain.domains.dto.LoginResponseDto;
//import com.econovation.idpdomain.domains.dto.LoginResponseDtoWithRedirectUrl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AccountControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc; // Inject MockMvc
//
//    @BeforeEach
//    public void setup() throws Exception {
//        // Register the user account
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/sign-up")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"userName\":\"testuser\", \"year\":\"21\", \"userEmail\":\"test@example.com\", \"password\":\"Dltjgus119@@\"}"))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//    }
//
//    @Test
//    void login_ShouldIssueCookie() throws Exception {
//        // Create a LoginRequestDto with test data
//        LoginRequestDto loginDto = new LoginRequestDto();
//        loginDto.setUserEmail("test@example.com");
//        loginDto.setPassword("Dltjgus119@@");
//        loginDto.setRedirectUrl("/home");
//
//        // Mock the login response
//        LoginResponseDto responseDto = new LoginResponseDto("access-token", "refresh-token");
//
//        // Perform the request and validate the response
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/accounts/login/process")
//                        .param("userEmail", loginDto.getUserEmail())
//                        .param("password", loginDto.getPassword())
//                        .param("redirectUrl", loginDto.getRedirectUrl()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
//                .andExpect(MockMvcResultMatchers.cookie().value("refresh_token", responseDto.getRefreshToken()));
//    }
//}
