package com.michaelcgood.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.michaelcgood.model.ComputerSystem;

@Repository
public interface ComputerSystemRepository extends CrudRepository<ComputerSystem,Long> {

}