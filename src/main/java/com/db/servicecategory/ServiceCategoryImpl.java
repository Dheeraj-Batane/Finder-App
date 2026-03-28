package com.db.servicecategory;

import com.db.common.Constants;
import com.db.common.Response;
import com.db.database.RepositoryFactory;
import com.db.database.entities.Schedule;
import com.db.database.entities.ServiceCategory;
import com.db.database.entities.ServiceProvider;
import com.db.enums.ServicesCategories;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceCategoryImpl implements IServiceCategory{
    @Autowired
    private RepositoryFactory repositoryFactory;


    @Override
    public Response addNewService(AddServiceCategoryRequest request) {
      log.debug("inside add new service method");
      if(null==request.getName() || null==request.getDescription()){
          return new Response(Constants.ERROR_CODE,"Name or description is null");
      }
      ServiceCategory category=new ServiceCategory();
      category.setName(request.getName());
      category.setDescription(request.getDescription());
      repositoryFactory.getServiceCategoryRepository().save(category);
      return new Response(Constants.SUCCESS_CODE,Constants.SUCCESS_MSG);
    }

    @Override
    public GetAllServices getAllServiceCategories() {
        GetAllServices resp=new GetAllServices();
        List<ServiceCategory> categories = repositoryFactory.getServiceCategoryRepository().findAll();
        if(categories.isEmpty()){
            resp.setResponseCode(Constants.SUCCESS_CODE);
            resp.setResponseMessage("Categories not found");
            return resp;
        }else{
            List<ServiceCategoryResponse> responseList=new ArrayList<>();
            for(ServiceCategory serviceCategory:categories){
                ServiceCategoryResponse response=new ServiceCategoryResponse();
                response.setId(String.valueOf(serviceCategory.getId()));
                response.setName(serviceCategory.getName());
                response.setDescription(serviceCategory.getDescription());
                responseList.add(response);
            }
            resp.setData(responseList);
            resp.setResponseCode(Constants.SUCCESS_CODE);
            resp.setResponseMessage(Constants.SUCCESS_MSG);
            return resp;
        }
    }

    @Override
    public void deleteCategory(Long id) {
        ServiceCategory category = repositoryFactory.getServiceCategoryRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Service Category not found with ID: " + id));
        // Note: If you have providers linked to this category in the provider_categories
        // join table, you may need to clear those links first or handle the DataIntegrityViolationException.
        repositoryFactory.getServiceCategoryRepository().delete(category);
    }

    @Override
    public ServiceProvidersResponse getAllServices(Long categoryId) {
        ServiceCategory serviceCategory = repositoryFactory.getServiceCategoryRepository().findById(categoryId).
                orElseThrow(() -> new RuntimeException("Error: Category not found"));

        // Fetch verified providers offering this category
        List<ServiceProvider> providers = repositoryFactory.getServiceProviderRepository().findByServiceCategoriesId(categoryId);

        List<ServiceProvidersList> providerResponses = new ArrayList<>();

        // Map Entities to DTOs
        for (ServiceProvider provider : providers) {
            ServiceProvidersList dto = new ServiceProvidersList();
            dto.setProviderId(provider.getId());
            dto.setFirstName(provider.getUser().getFirstName());
            dto.setLastName(provider.getUser().getLastName());
            dto.setExperienceYears(provider.getExperienceYears());
            dto.setHourlyRate(provider.getHourlyRate());
            dto.setBio(provider.getBio());

            // Map Schedules
            List<Schedule> scheduleResponses = provider.getSchedules().stream().map(schedule -> {
                Schedule sDto = new Schedule();
                sDto.setDayOfWeek(schedule.getDayOfWeek());
                sDto.setStartTime(schedule.getStartTime());
                sDto.setEndTime(schedule.getEndTime());
                return sDto;
            }).collect(Collectors.toList());

            dto.setSchedules(scheduleResponses);
            providerResponses.add(dto);
        }

        // Wrap in the standard response format
        ServiceProvidersResponse response = new ServiceProvidersResponse();
        response.setResponseCode("00000000");
        response.setResponseMessage("Success");
        response.setData(providerResponses);

        return response;
    }
}
