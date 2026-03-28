package com.db.auth.dto;

import com.db.common.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse extends Response {
    private String userId;
    private String userRole;
}
