package com.db.servicecategory;

import com.db.common.Response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetAllServices extends Response {
    private List<ServiceCategoryResponse> data;
}
