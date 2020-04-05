package com.brianrook.medium.customer.dao;

import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDAO extends CrudRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByEmail(String email);
}
