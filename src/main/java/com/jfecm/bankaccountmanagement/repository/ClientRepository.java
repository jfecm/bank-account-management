package com.jfecm.bankaccountmanagement.repository;

import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByDni(String dni);

    Client findByDni(String dni);

    List<Client> findByUserStatus(UserStatus userStatus);
}
