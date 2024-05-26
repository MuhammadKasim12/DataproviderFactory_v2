package com.datareader;

import java.util.List;

public interface DataReader {
	
	Object readTestCase(MetaTestData metaData);
	
	List<Object> readTestCases(MetaTestData meta);

}
