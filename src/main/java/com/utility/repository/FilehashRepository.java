package com.utility.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.utility.model.FilehashBean;

@Repository
public interface FilehashRepository extends JpaRepository<FilehashBean, Long> {

	 public FilehashBean findByFullpath(String fullpath);

	
	
}
