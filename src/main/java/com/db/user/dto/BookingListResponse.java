package com.db.user.dto;

import com.db.common.Response;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BookingListResponse extends Response {
    private List<BookingResponse> data;
}