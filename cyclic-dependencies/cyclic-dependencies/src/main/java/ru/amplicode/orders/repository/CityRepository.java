package ru.amplicode.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.amplicode.orders.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {

}
