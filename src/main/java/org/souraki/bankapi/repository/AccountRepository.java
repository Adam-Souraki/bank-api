package org.souraki.bankapi.repository;

import org.souraki.bankapi.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

}
