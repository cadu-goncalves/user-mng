package com.creativedrive.user.persistence;

import com.creativedrive.user.domain.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User entity repository.
 *
 * <p>Spring Data builds the implementation dynamically</p>
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    @Query(value = "{ 'name' : ?0 }" )
    Optional<User> findByName(String name);

    @Query(value = "{ $and : [ { 'name' : ?0 }, { 'password' : ?1} ] }" )
    Optional<User> checkAuth(String name, String password);

}
