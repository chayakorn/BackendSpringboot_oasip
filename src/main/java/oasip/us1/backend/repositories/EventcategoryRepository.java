package oasip.us1.backend.repositories;

import oasip.us1.backend.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventcategoryRepository extends JpaRepository<EventCategory, Integer> {
}