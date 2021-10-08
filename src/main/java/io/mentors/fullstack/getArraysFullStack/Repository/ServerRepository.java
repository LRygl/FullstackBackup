package io.mentors.fullstack.getArraysFullStack.Repository;

import io.mentors.fullstack.getArraysFullStack.Model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findByIpAddress(String ipAddress);
}
