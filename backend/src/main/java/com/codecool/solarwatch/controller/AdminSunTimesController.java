package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.dto.suntimes.SunTimesAdminRequest;
import com.codecool.solarwatch.dto.suntimes.SunTimesAdminResponse;
import com.codecool.solarwatch.service.SunTimesAdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/suntimes")
public class AdminSunTimesController {

    private final SunTimesAdminService sunTimesAdminService;

    public AdminSunTimesController(SunTimesAdminService sunTimesAdminService) {
        this.sunTimesAdminService = sunTimesAdminService;
    }

    @PostMapping
    public SunTimesAdminResponse create(@RequestBody SunTimesAdminRequest request) {
        return sunTimesAdminService.create(request);
    }

    @PutMapping("/{id}")
    public SunTimesAdminResponse update(@PathVariable Long id, @RequestBody SunTimesAdminRequest request) {
        return sunTimesAdminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sunTimesAdminService.delete(id);
    }
}