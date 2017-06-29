package com.utility.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utility.model.ServerBean;

public interface ServerRepository extends JpaRepository<ServerBean, Long> {
	public ServerBean findByServerName(String serverName);

}
