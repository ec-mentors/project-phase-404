package io.everyonecodes.cryptolog.data;

import javax.persistence.*;

@Entity
@Table(name = "auth_role")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "auth_role_id")
	private int id;

	@Column(name = "role_name")
	private String role;

	@Column(name = "role_desc")
	private String desc;

	public Role() {
	}

	public Role(String role) {
		this.role = role;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}