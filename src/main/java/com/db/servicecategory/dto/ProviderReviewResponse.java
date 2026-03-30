package com.db.servicecategory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderReviewResponse {
    private Integer stars;
    private String comments;
    private String reviewerName;
    private String date;
}
