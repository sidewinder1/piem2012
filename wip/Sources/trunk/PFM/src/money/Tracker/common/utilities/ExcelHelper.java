package money.Tracker.common.utilities;

import java.io.File;
import java.io.IOException;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.database.Cursor;
import android.os.Environment;

public class ExcelHelper {
	private static ExcelHelper sInstance;
	private String mDefaultPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator + "PFMData" + File.separator;

	private String[][] mTableInfos = {
			{
					"Category",
					"Id, CreatedDate, ModifiedDate, Name, IsDeleted,User_Color",
					"LONG, DATE, DATE, TEXT, INT,TEXT" },
			{
					"Schedule",
					"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, Start_date, End_date",
					"LONG, DATE, DATE, LONG, INT, INT, DATE, DATE" },
			{
					"ScheduleDetail",
					"Id, CreatedDate, ModifiedDate, Budget, IsDeleted, Category_Id, Schedule_Id",
					"LONG, DATE, DATE, LONG, INT, LONG, LONG" },
			{ "Entry", "Id, CreatedDate, ModifiedDate, IsDeleted,Date, Type",
					"LONG, DATE, DATE, INT,DATE, INT" },
			{
					"EntryDetail",
					"Id, CreatedDate, ModifiedDate, Category_Id, Name, IsDeleted,Money, Entry_Id",
					"LONG, DATE, DATE, LONG, TEXT, INT,LONG, LONG" },
			{
					"BorrowLend",
					"ID, CreatedDate, ModifiedDate, IsDeleted, Debt_type, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_phone, Person_address",
					"LONG, DATE, DATE, INT, TEXT, LONG, TEXT, INT, DATE, DATE, TEXT, TEXT, TEXT" } };

	public ExcelHelper() {

	}

	public static ExcelHelper getInstance() {
		return sInstance == null ? (sInstance = new ExcelHelper()) : sInstance;
	}

	public boolean importData(String filePath) {
		Workbook workbook = null;

		try {
			workbook = Workbook.getWorkbook(new File(filePath));
		} catch (BiffException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		}

		for (Sheet sheet : workbook.getSheets()) {
			importToLocalDB(sheet);
		}

		workbook.close();
	
		Alert.getInstance().show(PfmApplication.getAppContext(),
				PfmApplication.getAppResources().getString(R.string.success));
		return true;
	}

	private void importToLocalDB(Sheet sheet) {
		String[] columnInfo = getColumnInfo(sheet);
		if (columnInfo == null) {
			return;
		}

		int idIndex = Integer.parseInt(columnInfo[1].split(",")[0]);
		int modifiedDateIndex = Integer.parseInt(columnInfo[1].split(",")[1]);

		for (int row = 1; row < sheet.getRows(); row++) {
			String condition = new StringBuilder("Id=").append(
					sheet.getCell(idIndex, row).getContents()).toString();
			Cursor checkExistedId = SqlHelper.instance.select(sheet.getName(),
					"ModifiedDate", condition);
			if (checkExistedId != null && checkExistedId.moveToFirst()) {
				// This record exists.
				// Check modifiedDate.
				if (checkExistedId.getString(0).compareTo(
						sheet.getCell(modifiedDateIndex, row).getContents()) < 0) {
					SqlHelper.instance.update(sheet.getName(),
							columnInfo[0].split(","), getRowData(sheet, row),
							condition);
				}
			} else {
				// If this record doesn't exist, then insert it into database.
				SqlHelper.instance.insert(sheet.getName(),
						columnInfo[0].split(","), getRowData(sheet, row));
			}
		}

	}

	private String[] getRowData(Sheet sheet, int rowIndex) {
		String[] rowData = new String[sheet.getColumns()];
		int col = 0;
		for (Cell row : sheet.getRow(rowIndex)) {
			rowData[col++] = row.getContents();
		}

		return rowData;
	}

	// / Get header of table.
	// / Return: columnName and columnInfo (index of Id and ModifiedDate column)
	private String[] getColumnInfo(Sheet sheet) {
		Cell cell = sheet.getCell(0, 0);
		if ("".equals(cell.getContents())) {
			Alert.getInstance().show(
					PfmApplication.getAppContext(),
					PfmApplication.getAppResources().getString(
							R.string.import_wrong_format));
			return null;
		}

		String columnName = "";
		String columnInfo = "";
		Cell[] columns = sheet.getRow(0);
		for (int col = 0; col < columns.length; col++) {
			columnName += new StringBuilder(col == 0 ? "" : ",").append(
					columns[col].getContents()).toString();
			if ("ID".equals(columns[col].getContents().toUpperCase())) {
				columnInfo = new StringBuilder().append(col).append(columnInfo)
						.toString();
			} else if ("MODIFIEDDATE".equals(columns[col].getContents()
					.toUpperCase())) {
				columnInfo = new StringBuilder(columnInfo).append(",")
						.append(col).toString();
			}
		}

		return new String[] { columnName, columnInfo };
	}

	public boolean exportData(String fileName) {
		// Check file exists.
		if (new File(mDefaultPath + fileName).exists()) {
			fileName = new StringBuilder(fileName.split("[.]")[0])
					.append("_")
					.append(Converter.toString(DateTimeHelper.now(false),
							"yyyyMMddHHmmss")).append(".")
					.append(fileName.split("[.]")[1]).toString();
		}

		WritableWorkbook workbook = null;
		try {
			workbook = Workbook
					.createWorkbook(new File(mDefaultPath + fileName));
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		}

		for (int sheetIndex = 0; sheetIndex < mTableInfos.length; sheetIndex++) {
			createSheetData(workbook, mTableInfos[sheetIndex], sheetIndex);
		}

		try {
			workbook.write();
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
			return false;
		}
		try {
			workbook.close();
		} catch (WriteException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		}
		return true;
	}

	private void createHeader(WritableSheet sheet, String[] headerInfo,
			int row, String[] headerType) {
		WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 13);
		WritableCellFormat arial10format = new WritableCellFormat(arial10font);
		try {
			arial10format.setBackground(Colour.AQUA);
			arial10format.setBorder(Border.ALL, BorderLineStyle.DOUBLE);
		} catch (WriteException e) {
			Logger.Log(e.getMessage(), "ExcelHelper");
		}

		for (int index = 0; index < headerInfo.length; index++) {
			Label label = new Label(index, row, headerInfo[index].trim(),
					arial10format);
			try {
				sheet.addCell(label);
			} catch (RowsExceededException e) {
				Logger.Log(e.getMessage(), "ExcelHelper");
			} catch (WriteException e) {
				Logger.Log(e.getMessage(), "ExcelHelper");
			}
		}
	}

	private void createSheetData(WritableWorkbook workbook, String[] tableInfo,
			int sheetIndex) {
		WritableSheet sheet = workbook.createSheet(tableInfo[0], sheetIndex);

		Cursor selector = SqlHelper.instance.select(tableInfo[0], tableInfo[1],
				null, true);

		// Create table header.
		int row = 0;
		createHeader(sheet, tableInfo[1].split(","), row,
				tableInfo[2].split(","));

		if (selector != null && selector.moveToFirst()) {
			String[] columnInfos = tableInfo[2].split(",");
			DateFormat customDateFormat = new DateFormat("dd/MM/yyyy hh:mm:ss");
			WritableCellFormat dateFormat = new WritableCellFormat(
					customDateFormat);

			WritableCellFormat numberFormat = new WritableCellFormat(
					NumberFormats.INTEGER);
			WritableCellFormat textFormat = new WritableCellFormat();
			try {
				dateFormat.setBorder(Border.ALL, BorderLineStyle.DOUBLE);
				numberFormat.setBorder(Border.ALL, BorderLineStyle.DOUBLE);
				textFormat.setBorder(Border.ALL, BorderLineStyle.DOUBLE);
			} catch (WriteException e) {
				Logger.Log(e.getMessage(), "ExcelHelper");
			}

			// Create table content.
			do {
				++row;
				for (int col = 0; col < selector.getColumnCount(); col++) {
					try {
						if ("LONG INT".contains(columnInfos[col].trim()
								.toUpperCase())) {
							jxl.write.Number number = new jxl.write.Number(col,
									row, selector.getLong(col), numberFormat);

							sheet.addCell(number);
						} else if ("TEXT".equals(columnInfos[col].trim()
								.toUpperCase())) {
							Label label = new Label(col, row,
									selector.getString(col), textFormat);
							sheet.addCell(label);
						} else if ("DATE".equals(columnInfos[col].trim()
								.toUpperCase())) {
							DateTime dateCell = new DateTime(col, row,
									Converter.toDate(selector.getString(col)),
									dateFormat);
							sheet.addCell(dateCell);
						}
					} catch (RowsExceededException e) {
						Logger.Log(e.getMessage(), "ExcelHelper");
					} catch (WriteException e) {
						Logger.Log(e.getMessage(), "ExcelHelper");
					}
				}
			} while (selector.moveToNext());
		}
	}
}
