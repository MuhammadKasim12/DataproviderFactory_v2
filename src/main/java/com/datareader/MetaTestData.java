package com.datareader;

import java.util.Optional;

import com.dataprovider.REQUEST_TYPE;

import lombok.Data;

public @Data class MetaTestData {
	
	private String fileName;
	private Class<?> toClass;
	private Optional<String> testRange;
	private int startingIndex=-1;
	private int endingIndex=-1;
	private REQUEST_TYPE requestType;
	
	
	public int getStartingIndex(){
		if(this.testRange.isPresent() && startingIndex ==-1){
			String[] split = this.testRange.get().split("-");
			this.startingIndex = Integer.parseInt(split[0]);
		}
		return startingIndex;
	}
	
	public int getEndingIndex(){
		if(this.testRange.isPresent() && endingIndex ==-1){
			String[] split = this.testRange.get().split("-");
			this.endingIndex = Integer.parseInt(split[1]);
		}
		return endingIndex;
	}
	
}
