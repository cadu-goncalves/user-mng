package com.creativedrive.user.persistence;

import com.creativedrive.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * User entity repository.
 *
 * <p>Spring Data builds the implementation dynamically</p>
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, String> {
}
