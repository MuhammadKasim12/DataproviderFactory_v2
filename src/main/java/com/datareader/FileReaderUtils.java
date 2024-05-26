package com.datareader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class FileReaderUtils {
	
	public static void printCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			System.out.print(cell.getBooleanCellValue());
			break;
		case STRING:
			System.out.print(cell.getRichStringCellValue().getString());
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				System.out.print(cell.getDateCellValue());
			} else {
				System.out.print(cell.getNumericCellValue());
			}
			break;
		case FORMULA:
			System.out.print(cell.getCellFormula());
			break;
		case BLANK:
			System.out.print("");
			break;
		default:
			System.out.print("");
		}

		System.out.print("\t");
	}

	public static Object returnCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case STRING:
			return cell.getRichStringCellValue().getString();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return cell.getNumericCellValue();
			}
		case FORMULA:
			return cell.getCellFormula();
		default:
			System.out.print("");
		}
		System.out.print("\t");
		return null;
	}

}
