package ru.amplicode.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.amplicode.orders.domain.OrderLine;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}
