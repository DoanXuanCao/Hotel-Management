package hotel_management.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Optional<Account> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByIdNumber(String idNumber);
}
