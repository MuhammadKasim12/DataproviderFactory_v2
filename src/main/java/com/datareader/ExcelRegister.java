package com.datareader;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
@AllArgsConstructor
public @Data class ExcelRegister implements Cloneable{
	
	private @NonNull Sheet currentSheet;
	private int rowNumber;
	private int columnNumber;
	private Object pojoSheetReferenceKey;
	private List<String> rowIdentifiers;
	private Object rowIdentifier;
	
	public ExcelRegister( Sheet currentSheet, int rowNumber){
		this.currentSheet = currentSheet;
		this.rowNumber = rowNumber;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public ExcelRegister(Sheet currentSheet, int rowNumber, int columnNumber, Object pojoSheetReferenceKey) {
		this(currentSheet,rowNumber);
		this.columnNumber = columnNumber;
		this.pojoSheetReferenceKey = pojoSheetReferenceKey;
	}
	

}
