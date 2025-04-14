package com.robin.paymentgateway.service;

import com.robin.paymentgateway.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    public long getCountryCount(){
        return countryRepository.count();
    }
}
