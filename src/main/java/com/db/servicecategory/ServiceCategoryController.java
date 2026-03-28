package com.db.servicecategory;

import com.db.common.Constants;
import com.db.common.Response;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceCategoryController {

    @Autowired
    private IServiceCategory iServiceCategory;

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> saveNewServiceCategory(@RequestBody AddServiceCategoryRequest request) {
        Response response = iServiceCategory.addNewService(request);
        if (Constants.SUCCESS_CODE.equalsIgnoreCase(response.getResponseCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllServices> getAllCategories() {
        GetAllServices allServiceCategories = iServiceCategory.getAllServiceCategories();
        return ResponseEntity.status(HttpStatus.OK).body(allServiceCategories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            iServiceCategory.deleteCategory(id);
            return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<?> getAllServiceProviderOfCategory(@PathVariable Long id) {
        return null;
    }
}
