package com.mindstormsoftware.vethackday.entity;

import javax.persistence.Id;

public class MOC {
	@Id private Long id;
	String code;
	String branch;
	String title;
	String civilianEquivalent;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}
	/**
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the civilianEquivalent
	 */
	public String getCivilianEquivalent() {
		return civilianEquivalent;
	}
	/**
	 * @param civilianEquivalent the civilianEquivalent to set
	 */
	public void setCivilianEquivalent(String civilianEquivalent) {
		this.civilianEquivalent = civilianEquivalent;
	}
	
}
