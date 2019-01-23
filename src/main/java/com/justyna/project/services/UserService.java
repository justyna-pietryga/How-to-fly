package com.justyna.project.services;

import com.justyna.project.model.relational.User;
import com.justyna.project.security.model.UserDto;

import java.util.List;

public interface UserService {

    User save(UserDto user);

    List<User> findAll();

    void delete(long id);

    User findOne(String username);

    User findById(Long id);
}