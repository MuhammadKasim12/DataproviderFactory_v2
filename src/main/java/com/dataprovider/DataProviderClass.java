package com.dataprovider;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import com.datareader.ExcelMetaData;
import com.datareader.ExcelReader;
import com.datareader.MetaDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import net.minidev.json.JSONArray;

public class DataProviderClass {
	public static final String DATAOBJECT_PACKAGE = "com.sample.test.dataobjects";

	private static Logger logger = LoggerFactory.getLogger(DataProviderClass.class);

	@DataProvider(name = "ExcelToAnyProvider", parallel = false)
    public Object[][] universalRowProvider(Method method) throws Exception {

        Object[][] rows = null;
        try {
            ExcelMetaData metaData = new MetaDataBuilder()
            		.withExcelAsSource()
            		.useMethodAnnotation(method)
            		.setTestDataRange("1-2")
            		.build();

            List<? extends Object> objects = new ExcelReader().readTestCases(metaData);

            rows = new Object[objects.size()][1]; //[rows][no of parameters on the method]

            switch (metaData.getRequestType()) {
                case JSON:
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.addAll(objects);
                    for(int i=0;i< jsonArray.size(); i++){
                        rows[i][0]=jsonArray.get(i).toString();
                    }
                    break;
                case XML:
                    ObjectMapper xmlMapper = new XmlMapper();
                    for(int i=0;i< rows.length; i++){
                        String xmlRequest = xmlMapper.writer()
                                .with(SerializationFeature.INDENT_OUTPUT)
                                .with(SerializationFeature.WRAP_ROOT_VALUE)
                                .withRootName("xmlRequest")
                                .writeValueAsString(objects.get(i));
                        rows[i][0]=xmlRequest;
                    }
                    break;
                default:
            	   ObjectMapper mapper = new ObjectMapper();
                   Class<Object[]> arrayClass = (Class<Object[]>) Class.forName("[L" + metaData.getToClass().getName() + ";");
                   Object[] objects2 = mapper.readValue(JSONArray.toJSONString(objects), arrayClass);
                   for(int i=0;i< objects2.length; i++){
                       rows[i][0]=objects2[i];
                   }
                    
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        if (rows == null) {
            logger.info("rows from Data Provider is null ");
        }
        return rows;
    }
}
