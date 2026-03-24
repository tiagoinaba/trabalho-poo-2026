package com.oz.domain;

public class AreaComum {
	private Long id;
	private String name;

	public AreaComum(String nome) {
		this.id = null;
		this.name = nome;
	}

	public AreaComum(Long id, String nome) {
		this.id = id;
		this.name = nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
