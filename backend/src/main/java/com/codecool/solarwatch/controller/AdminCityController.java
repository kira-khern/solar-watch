package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.city.CityAdminRequest;
import com.codecool.solarwatch.dto.city.CityAdminResponse;
import com.codecool.solarwatch.service.CityAdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cities")
public class AdminCityController {

    private final CityAdminService cityAdminService;

    public AdminCityController(CityAdminService cityAdminService) {
        this.cityAdminService = cityAdminService;
    }

    @PostMapping
    public CityAdminResponse create(@RequestBody CityAdminRequest request) {
        return cityAdminService.createCity(request);
    }

    @PutMapping("/{id}")
    public CityAdminResponse update(@PathVariable Long id, @RequestBody CityAdminRequest request) {
        return cityAdminService.updateCity(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cityAdminService.deleteCity(id);
    }
}