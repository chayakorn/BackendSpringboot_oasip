package oasip.us1.backend.repositories;

import oasip.us1.backend.entities.User;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByOrderByNameAsc();
    List<User> findAllByName(String name);
    List<User> findAllByEmail(String email);
    User findByEmail(String email);

    @Query(
            value = "select * from user where name = :name and userId <> :id",
            nativeQuery = true
    )
    List<User> findAllByNameExceptMyself(@Param("name") String name, @Param("id") Integer id);
}