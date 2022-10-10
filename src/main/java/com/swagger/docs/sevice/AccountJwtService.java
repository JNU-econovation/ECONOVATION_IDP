package com.swagger.docs.sevice;

import com.swagger.docs.dto.LoginResponseDto;

public interface AccountJwtService {
    public LoginResponseDto reIssueAccessToken(String email, String refreshedToken);
    public LoginResponseDto login(String email, String password);
    public void logout(String refreshToken);
}
