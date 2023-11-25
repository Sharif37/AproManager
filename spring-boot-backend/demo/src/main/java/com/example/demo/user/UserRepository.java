package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository 
                extends JpaRepository<User,Long>{
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
    boolean existsByEmailAndPassword(String email, String password);

    
}