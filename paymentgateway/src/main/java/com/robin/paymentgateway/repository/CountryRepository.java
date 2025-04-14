package com.robin.paymentgateway.repository;

import com.robin.paymentgateway.entity.Country;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country,String> {

    @Query("select count(1) from Country c where c.continent = 'North America'")
    public long count();
}
