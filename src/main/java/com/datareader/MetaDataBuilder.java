package com.datareader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.dataprovider.DataProviderClass;
import com.dataprovider.DataProviderFactory;
import com.dataprovider.REQUEST_TYPE;

public class MetaDataBuilder {
	
	private ExcelMetaData excelMetaData;
	
	public MetaDataBuilder(){
		
	}
	public MetaDataBuilder withExcelAsSource(){
		this.excelMetaData = new ExcelMetaData();
		return this;
	}
	
	public MetaDataBuilder useMethodAnnotation(Method method){
		String sheetName = method.getAnnotation(DataProviderFactory.class).sheetName();
        String fileName = method.getAnnotation(DataProviderFactory.class).fileName();
        REQUEST_TYPE request_type = method.getAnnotation(DataProviderFactory.class).requestType();
        Objects.requireNonNull(sheetName, "Sheet Name attribute is required");
        Objects.requireNonNull(fileName, "File Name attribute is required");
        excelMetaData.setRequestType(request_type);
        
        String packagePath = DataProviderClass.DATAOBJECT_PACKAGE;
        String workbookAbsolutePath = System.getProperty("user.dir") +"/"+ fileName;
        this.excelMetaData.setFileName(fileName);
        try {
			Workbook workbook = WorkbookFactory.create(new File(workbookAbsolutePath));
			this.excelMetaData.setWorkbook(workbook);
			this.excelMetaData.setSheet(workbook.getSheet(sheetName));
			this.excelMetaData.setCurrentSheet(this.excelMetaData.getSheet());
		} catch (EncryptedDocumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			Class<?> clazz = Class.forName(packagePath + "." + sheetName);
			this.excelMetaData.setToClass(clazz);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public MetaDataBuilder setTestDataRange(String range){
		excelMetaData.setTestRange(Optional.of(range));
		return this;
	}
	
	public ExcelMetaData build(){
		return excelMetaData;
	}
	
	

}
