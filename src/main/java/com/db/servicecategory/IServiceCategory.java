package com.db.servicecategory;

import com.db.common.Response;

import java.util.List;

public interface IServiceCategory {
    Response addNewService(AddServiceCategoryRequest request);
    GetAllServices getAllServiceCategories();
    public void deleteCategory(Long id) ;
}
