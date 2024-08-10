package com.monocept.myapp.entity;

import java.util.List;


import lombok.Data;
@Data
public class PagedResponse<T>{
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean last;
	public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
		super();
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.last = last;
	}
	
	
}
