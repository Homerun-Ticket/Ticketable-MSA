package com.example.moduleauth.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtil {
	public static Pageable correctPageIndex(Pageable pageable){
		return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
	}
}
