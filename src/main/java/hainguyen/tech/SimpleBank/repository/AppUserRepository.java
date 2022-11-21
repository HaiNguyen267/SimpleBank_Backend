package hainguyen.tech.SimpleBank.repository;

import hainguyen.tech.SimpleBank.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByAccountNo(String accountNo);


    Optional<AppUser> findByEmailIgnoreCase(String email);
}
