package com.robin.paymentgateway.controller;

import com.robin.paymentgateway.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {

    @Autowired
    private CountryService countryService;

    @RequestMapping("/health")
    public long getHealth(){
        return countryService.getCountryCount();
    }
}
