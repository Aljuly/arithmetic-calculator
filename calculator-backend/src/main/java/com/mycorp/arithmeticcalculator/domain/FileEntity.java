package com.mycorp.arithmeticcalculator.domain;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "file")
public class FileEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Lob
	private byte[] data;
	private String name;
	private String contentType;
	
	public FileEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Objects.hash(contentType, id, name);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileEntity other = (FileEntity) obj;
		return Objects.equals(contentType, other.contentType) && Arrays.equals(data, other.data)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "FileEntity [id=" + id + ", data=" + Arrays.toString(data) + ", name=" + name + ", contentType="
				+ contentType + "]";
	}
	
}