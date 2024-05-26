package com.datareader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.dataprovider.DataProviderClass;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelReader implements DataReader{

	public static final String SAMPLE_XLSX_FILE_PATH = System.getProperty("user.dir")
			+ "/src/test/resources/testdata/DataResource/UserInformation.xlsx";
	public static final int HEADER_INDEX = 0;

	public static void main(String[] args) 
			throws IOException, InvalidFormatException, ClassNotFoundException, NoSuchFieldException {
	    Class[] classes = getClasses("");
	    List<String> listofDataObjects = new ArrayList<>();
	    for(Class c: classes){
	    	listofDataObjects.add(c.getSimpleName());
	    }
		Class<?> clazz = Class.forName("com.citrus.myproject.dataobjects.UserDetails");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (!field.getType().isPrimitive()) {
				String typeName = field.getType().getSimpleName();
				typeName = typeName.endsWith("[]") ? typeName.substring(0,typeName.length()-2) : typeName;
				if(listofDataObjects.contains(typeName))
					System.out.println(typeName);
				// getTypeName();
				// String typeNameSplit[] = typeName.split("\\.");
				// System.out.println(typeNameSplit[typeNameSplit.length-1]);
			}
		}
		// Creating a Workbook from an Excel file (.xls or .xlsx)
		Workbook workbook = WorkbookFactory.create(new File(SAMPLE_XLSX_FILE_PATH));

		// Retrieving the number of sheets in the Workbook
		System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

		// TestDataToObjectMapper testDataToObjectMapper = new
		// TestDataToObjectMapper(SAMPLE_XLSX_FILE_PATH, UserDetails.class);

		//insertIntoHashTable(workbook, UserDetails.class, 1);

		// workbook.forEach(sheets -> {
		// sheets.forEach(rows -> {
		// rows.forEach(cell -> {
		// printCellValue(cell);
		// System.out.print("\t");
		// });
		// System.out.println();
		// });
		// });
		//

		workbook.close();
	}

	/**
	 * 
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * 
	 * 
	 * @param packageName
	 *            The base package
	 * 
	 * @return The classes
	 * 
	 * @throws ClassNotFoundException
	 * 
	 * @throws IOException
	 * 
	 */

	private static Class[] getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		packageName = "com.citrus.myproject";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<File> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * 
	 * Recursive method used to find all classes in a given directory and subdirs.
	 * 
	 * @param directory - The base directory
	 * 
	 * @param packageName- The package name for classes found inside the base directory
	 * 
	 * @return The classes
	 * 
	 * @throws ClassNotFoundException
	 * 
	 */

	private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
		List classes = new ArrayList();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * Method to find if an object exist with the name provided
	 *
	 * @param cellValue
	 * @return
	 */

	private static boolean isClassObject(String cellValue) {
//		String packagePath = "com.citrus.myproject.dataobjects";
		System.out.println(getCaptalizedClassName(DataProviderClass.DATAOBJECT_PACKAGE, cellValue));
		return true;
	}

	private static Class<?> getCaptalizedClassName(String packagePath, String cellValue) {

		Class<?> clazz = null;
		try {
			clazz = Class.forName(packagePath + "." + StringUtils.capitalize(cellValue));
		} catch (ClassNotFoundException e) {
		}
		return clazz;
	}

	/**
	 * @param workbook
	 * @param sheet
	 * @param range
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	private static Map<Object, Object> mapDataToPOJO(ExcelMetaData metadata, ExcelRegister register)
			throws NoSuchFieldException, CloneNotSupportedException {
		Sheet currentSheet = register.getCurrentSheet();
		int rowNumber = register.getRowNumber();
		int columnLength = currentSheet.getRow(HEADER_INDEX).getPhysicalNumberOfCells();
		Map<Object, Object> testDataPojoMap = new HashMap<>();
		for (int columnNumber = 1; columnNumber < columnLength; columnNumber++) {

			final Object pojoSheetReferenceKey = FileReaderUtils.returnCellValue(currentSheet.getRow(HEADER_INDEX).getCell(columnNumber));
			String packagePath = DataProviderClass.DATAOBJECT_PACKAGE;
			boolean derivedClass = isDerivedClass(currentSheet, pojoSheetReferenceKey);

			if (derivedClass) {
				String normalizedClassName = getNormalizedClassName(returnClassType(currentSheet, pojoSheetReferenceKey, packagePath));
				if (isClassObject(normalizedClassName)) {
					ExcelRegister subregister = new ExcelRegister(currentSheet, rowNumber, columnNumber, pojoSheetReferenceKey);
					extractSingleRow(metadata, testDataPojoMap, subregister);
				}
			} else {
				String cellValue = null;
				try {
					cellValue = FileReaderUtils.returnCellValue(currentSheet.getRow(rowNumber).getCell(columnNumber)).toString();
					testDataPojoMap.put(pojoSheetReferenceKey, cellValue);
				} catch (Exception e) {
					testDataPojoMap.put(pojoSheetReferenceKey, "");
					System.out.println("empty value ignored");
				}
			}
		}
		return testDataPojoMap;
	}

	private List<Object> mapDataToPOJO(ExcelMetaData metaData)
			throws NoSuchFieldException, CloneNotSupportedException {
		int startingIndex = metaData.getStartingIndex();
		int endingIndex = metaData.getEndingIndex();
		List<Object> testDataRowMap = new ArrayList<>(endingIndex);
		for (int rowNumber = startingIndex; rowNumber <= endingIndex; rowNumber++) {
			ExcelRegister register  = new ExcelRegister(metaData.getCurrentSheet(), 1);
			testDataRowMap.add(mapDataToPOJO(metaData, register));
		}
		return testDataRowMap;
	}

	private static void extractSingleRow(ExcelMetaData metadata, 
			Map<Object, Object> testDataPojoMap, ExcelRegister subregister) throws NoSuchFieldException, CloneNotSupportedException {
		Sheet sheet = metadata.getCurrentSheet();
		Object rowIdentifier = FileReaderUtils.returnCellValue(sheet.getRow(subregister.getRowNumber()).getCell(subregister.getColumnNumber()));
		List<String> rowIdentifiers = Arrays.asList(rowIdentifier.toString().split(","));
		ExcelRegister register = (ExcelRegister) subregister.clone();
		if (rowIdentifiers.size() > 1) {
			ArrayList<Object> objects = new ArrayList<>();
			
			register.setRowIdentifiers(rowIdentifiers);
			mapDataToPOJOArray(metadata, register , objects);
			testDataPojoMap.put(subregister.getPojoSheetReferenceKey(), objects);
		} else {
			register.setRowIdentifier(rowIdentifier);
			mapDataToPOJOObject(metadata, register, testDataPojoMap);
		}
	}

	private static void mapDataToPOJOObject(ExcelMetaData metadata, ExcelRegister register, 
			Map<Object, Object> testDataPojoMap) throws NoSuchFieldException, CloneNotSupportedException {
		Map<Object, Object> objectObjectHashMap;
		int rowByMatchingFirstColumnValues = findRowByMatchingFirstColumnValues(metadata.getSheet(register.getPojoSheetReferenceKey().toString()),
				register.getRowIdentifier().toString());
		ExcelRegister registerNew= (ExcelRegister) register.clone();
		registerNew.setCurrentSheet(metadata.getSheet(register.getPojoSheetReferenceKey().toString()));
		registerNew.setRowNumber(rowByMatchingFirstColumnValues);
		objectObjectHashMap = mapDataToPOJO(metadata, registerNew);
		testDataPojoMap.put(register.getPojoSheetReferenceKey(), objectObjectHashMap);
	}

	private static void mapDataToPOJOArray(ExcelMetaData metadata, ExcelRegister register,
			ArrayList<Object> objects) throws CloneNotSupportedException {
		Map<Object, Object> objectObjectHashMap;
		for (String string : register.getRowIdentifiers()) {
			int rowByMatchingFirstColumnValues = findRowByMatchingFirstColumnValues(
					metadata.getSheet(register.getPojoSheetReferenceKey().toString()),string);
			try {
				ExcelRegister registerNew= (ExcelRegister) register.clone();
				registerNew.setCurrentSheet(metadata.getSheet(register.getPojoSheetReferenceKey().toString()));
				registerNew.setRowNumber(rowByMatchingFirstColumnValues);
				
				objectObjectHashMap = mapDataToPOJO(metadata,registerNew);
				objects.add(objectObjectHashMap);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isDerivedClass(Sheet sheet, Object objectKey) {
		boolean isDerived = false;
		try {
			String packagePath = DataProviderClass.DATAOBJECT_PACKAGE;
			Class<?> type = null;
			type = returnClassType(sheet, objectKey, packagePath);
			isDerived = type.getName().contains(packagePath);
		} catch (Exception e) {
			return false;
		}
		return isDerived;
	}

	private static Class<?> returnClassType(Sheet sheet, Object objectKey, String packagePath) {
		Class<?> type = getClassType(sheet, objectKey, packagePath);
		return type;
	}

	private static String getNormalizedClassName(Class<?> type) {
		int lastIndex = type.getName().lastIndexOf(".");
		int length = type.getName().length();
		return type.getName().substring(lastIndex + 1, length).replace(";", "");
	}

	private static Class<?> getClassType(Sheet sheet, Object objectKey, String packagePath) {
		Class<?> clazz = null;
		clazz = getCaptalizedClassName(packagePath, sheet.getSheetName());
		// clazz = Class.forName(packagePath+ "." +sheet.getSheetName());
		Class<?> clazzType = null;
		try {
			clazzType = clazz.getDeclaredField(objectKey.toString()).getType();
			System.out.println("Declared field type" + clazzType.toString());
		} catch (NoSuchFieldException e) {
			try {
				clazzType = clazz.getDeclaredField(StringUtils.capitalize(objectKey.toString())).getType();
			} catch (NoSuchFieldException ex) {
				ex.printStackTrace();
			}
		}
		return clazzType;
	}

	/**
	 * @param sheet
	 * @param cellContentRowIdentifier
	 * @return
	 */
	private static int findRowByMatchingFirstColumnValues(Sheet sheet, String cellContentRowIdentifier) {
		/*
		 * This is the method to find the row number by matching the First
		 * Column
		 */
		for (int row = 1; row <= sheet.getLastRowNum(); row++) {
			if (sheet.getRow(row).getCell(0).getStringCellValue().equalsIgnoreCase(cellContentRowIdentifier)) {
				return row;
			}
			;
		}
		return -1;
	}


	private static void populateTestDataPojoMap(Sheet sheet, int noOfRows, HashMap<Object, Object> testDataPojoMap,
			Object objectKey, int noOfColumns) {
		String cellValue = null;
		try {
			cellValue = FileReaderUtils.returnCellValue(sheet.getRow(noOfRows).getCell(noOfColumns)).toString();
			testDataPojoMap.put(objectKey, cellValue);
		} catch (Exception e) {
			testDataPojoMap.put(objectKey, "");
			System.out.println("empty value ignored");
		}
	}

	@Override
	public Object readTestCase(MetaTestData metaData) {
		ExcelMetaData excelMetaData = (ExcelMetaData) metaData;
		ExcelRegister register  = new ExcelRegister(excelMetaData.getCurrentSheet() , 1);
		;
		final ObjectMapper mapper = new ObjectMapper(); 
		Object pojo=null;
		try {
			pojo = mapper.convertValue(mapDataToPOJO(excelMetaData, register), excelMetaData.getToClass());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pojo;
	}

	@Override
	public List<Object> readTestCases(MetaTestData metaData) {
		List<Object> objectObjectHashMap=null;
		try {
			objectObjectHashMap = mapDataToPOJO((ExcelMetaData)metaData);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectObjectHashMap;
	}
}