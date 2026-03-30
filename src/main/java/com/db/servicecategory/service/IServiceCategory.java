package com.db.servicecategory.service;

import com.db.common.Response;
import com.db.servicecategory.dto.AddServiceCategoryRequest;
import com.db.servicecategory.dto.GetAllServices;
import com.db.servicecategory.dto.ServiceProvidersResponse;

public interface IServiceCategory {
    Response addNewService(AddServiceCategoryRequest request);
    GetAllServices getAllServiceCategories();
    public void deleteCategory(Long id) ;
    public ServiceProvidersResponse getAllServices(Long categoryId);
}
