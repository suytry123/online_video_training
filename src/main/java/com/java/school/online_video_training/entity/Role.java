package com.java.school.online_video_training.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		    name = "roles_permissions",
		    joinColumns = @JoinColumn(name = "role_id"),
		    inverseJoinColumns = @JoinColumn(name = "permissions_id")
		)
	private Set<Permission> permissions;
}
