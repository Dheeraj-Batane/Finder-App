package com.db.servicecategory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddServiceCategoryRequest {
    private String name;
    private String description;
}
