package com.sample.test;

import org.testng.annotations.Test;

import com.dataprovider.DataProviderClass;
import com.dataprovider.DataProviderFactory;
import com.dataprovider.REQUEST_TYPE;
import com.sample.test.dataobjects.UserDetails;

public class DataProviderTest {


    private static final String SHEET_NAME = "UserDetails";
	private static final String EXCEL_FILE = "src/test/resources/testdata/DataResource/UserInformation.xlsx";

	@DataProviderFactory(requestType = REQUEST_TYPE.POJO,fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "POJOProvider"  , dataProviderClass = DataProviderClass.class, enabled=false)
    public void sampleRestPOJOTest(Object dataRow) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(dataRow.toString());
        System.out.println("After printing the sample ");
        System.out.println(dataRow.toString());
    }


    @DataProviderFactory(requestType = REQUEST_TYPE.XML,fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "XMLProvider"  , dataProviderClass = DataProviderClass.class, enabled=false)
    public void sampleRestXMLTest(Object dataRow) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(dataRow.toString());
        System.out.println("After printing the sample ");
    }

    @DataProviderFactory(requestType = REQUEST_TYPE.JSON,fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "JSONDataProvider"  , dataProviderClass = DataProviderClass.class, enabled=false)
    public void sampleRestJSONTest(Object userDetails) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(userDetails.toString());
        System.out.println("After printing the sample ");
    }

    @DataProviderFactory(requestType = REQUEST_TYPE.JSON,fileName = EXCEL_FILE,sheetName = SHEET_NAME)
    @Test(dataProvider = "UniversalProvider"  , dataProviderClass = DataProviderClass.class, enabled=false)
    public void sampleRestUniversalTest(Object dataRow) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(dataRow);
        System.out.println("After printing the sample "); 
    }
    
    @DataProviderFactory(requestType = REQUEST_TYPE.POJO,fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "ExcelToAnyProvider", dataProviderClass = DataProviderClass.class)
    public void sampleRestUniversalRowTest(UserDetails userDetails) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(userDetails);
        System.out.println("After printing the sample ");
    }
    
    @DataProviderFactory(requestType = REQUEST_TYPE.JSON, fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "ExcelToAnyProvider"  , dataProviderClass = DataProviderClass.class)
    public void universalProviderExcelToJSON(String userDetails) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(userDetails);
        System.out.println("After printing the sample ");
    }
    
    @DataProviderFactory(requestType = REQUEST_TYPE.XML, fileName = EXCEL_FILE, sheetName = SHEET_NAME)
    @Test(dataProvider = "ExcelToAnyProvider"  , dataProviderClass = DataProviderClass.class)
    public void universalProviderExcelToXML(String userDetails) throws Exception {
        System.out.println("Before printing the sample ");
        System.out.println(userDetails);
        System.out.println("After printing the sample ");
    }

}
