package com.datareader;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;

public @Data class ExcelMetaData extends MetaTestData{
	private Workbook workbook;
	private Sheet sheet;
	private Sheet currentSheet;
	
	
	public Sheet getSheet(String sheetName){
		return this.workbook.getSheet(sheetName);
	}

}
