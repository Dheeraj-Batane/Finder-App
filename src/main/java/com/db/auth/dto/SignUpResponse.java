package com.db.auth.dto;

import com.db.common.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponse extends Response {
    private String userId;
}
