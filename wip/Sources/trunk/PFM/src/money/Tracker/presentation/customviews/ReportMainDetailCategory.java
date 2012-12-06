package money.Tracker.presentation.customviews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportMainDetailCategory extends LinearLayout {

	private LinearLayout category_list;
	private boolean checkMonthly;
	private Date startDate;
	private Date endDate;
	private boolean changedState = false;
	private String checkName = "";
	private List<String> scheduleCategoryName;
	private List<Long> scheduleCategoryValue;
	
	public ReportMainDetailCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ReportMainDetailCategory(Context context, String name, long value, boolean checkMonthly, Date startDate, Date endDate) {
		super(context);

		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_main_detail_category, this, true);
		
		this.checkMonthly = checkMonthly;
		this.startDate = startDate;
		this.endDate = endDate;
		this.checkName = name;

		// Get control from .xml file.
		TextView product_name = (TextView) findViewById(R.id.report_detail_main_category_item_name);
		TextView product_total = (TextView) findViewById(R.id.report_detail_main_category_item_total);
		category_list = (LinearLayout) findViewById(R.id.report_detail_main_category_list);
		Button collapsedButton = (Button) findViewById(R.id.report_main_collapsed_button);
		category_list.removeAllViews();
		
		// Set value to category.
		product_name.setText(name);
		product_total.setText(Converter.toString(value));
		
		scheduleCategoryName = new ArrayList<String>();
		scheduleCategoryValue = new ArrayList<Long>();
		
		if (name.equals("Ngân Sách"))
		{
			String whereCondition = "";
			if (checkMonthly)
				whereCondition = "Type = 1";
			else
				whereCondition = "Type = 0";
			
			int count = 0;
			
			Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", whereCondition);
			if (scheduleCursor != null)
			{
				if (scheduleCursor.moveToFirst())
				{
					do{
						if (checkMonthly) {
							Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
							String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
							String scheduleYear = Converter.toString(scheduleStartDate, "yyy");
							String startDateMonth = Converter.toString(startDate,"MM");
							String startDateYear = Converter.toString(startDate,"yyyy");

							if (scheduleMonth.equals(startDateMonth) && scheduleYear.equals(startDateYear)) {
								long id = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Id"));

								Cursor scheduleDetailCursor = SqlHelper.instance.select("ScheduleDetail", "*","Schedule_Id=" + id);

								if (scheduleDetailCursor != null) {
									if (scheduleDetailCursor.moveToFirst()) {
										do {
											String nameCategory = "";
											long categoryID = scheduleDetailCursor.getLong(scheduleDetailCursor.getColumnIndex("Category_Id"));
											long categoryValue = scheduleDetailCursor.getLong(scheduleDetailCursor.getColumnIndex("Budget"));

											Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
											if (categoryCursor != null) {
												if (categoryCursor.moveToFirst()) {
													do {
														nameCategory = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													} while (categoryCursor.moveToNext());
												}
											}

											scheduleCategoryName.add(nameCategory);
											scheduleCategoryValue.add(categoryValue);
											count++;
										} while (scheduleDetailCursor.moveToNext());
									}
								}
							}
						} else {
							Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
							String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
							String startDateMonth = Converter.toString(startDate, "MM");
							
					        Calendar calScheduleStart = Calendar.getInstance();
					        calScheduleStart.setTime(scheduleStartDate);
					        int scheduleWeek = calScheduleStart.get(Calendar.WEEK_OF_MONTH);
					        Calendar calStartDate = Calendar.getInstance();
					        calStartDate.setTime(scheduleStartDate);
					        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

							if (scheduleMonth.equals(startDateMonth) && scheduleWeek == startDateWeek){
								long id = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Id"));

								Cursor scheduleDetailCursor = SqlHelper.instance.select("ScheduleDetail", "*","Schedule_Id=" + id);

								if (scheduleDetailCursor != null) {
									if (scheduleDetailCursor.moveToFirst()) {
										do {
											String nameCategory = "";
											int categoryID = scheduleDetailCursor.getInt(scheduleDetailCursor.getColumnIndex("Category_Id"));
											long categoryValue = scheduleDetailCursor.getLong(scheduleDetailCursor.getColumnIndex("Budget"));

											Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
											if (categoryCursor != null) {
												if (categoryCursor.moveToFirst()) {
													do {
														nameCategory = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													} while (categoryCursor.moveToNext());
												}
											}

											scheduleCategoryName.add(nameCategory);
											scheduleCategoryValue.add(categoryValue);
											count++;
										} while (scheduleDetailCursor.moveToNext());
									}
								}
							}
						}
					} while (scheduleCursor.moveToNext());
				} 
			}
			
			if (count == 0)
				collapsedButton.setVisibility(View.INVISIBLE);
		}
		
		collapsedButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bindData();
			}
		});
	}
	
	private void bindData()
	{
		if (changedState == false)
		{
			if(checkName.equals("Ngân Sách"))
				getBudget();
			else if (checkName.equals("Thu Nhập"))
				getIncome();
			else if (checkName.equals("Chi Phí"))
				getExpense();
			else if (checkName.equals("Vay"))
				getBorrowing();
			else if (checkName.equals("Cho Vay"))
				getLending();
			
			changedState = true;
		} else
		{
			category_list.removeAllViews();
			changedState = false;
		}
	}
	
	private void getBudget()
	{
		for(int i = 0; i < scheduleCategoryName.size(); i++)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
			category_list.addView(new ReportDetailProduct(this.getContext(), scheduleCategoryName.get(i), scheduleCategoryValue.get(i)), params);
		}
	}
	
	private void getIncome() {
		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();
		List<Long> entryIDList = new ArrayList<Long>();
		List<Long> categoryIDList = new ArrayList<Long>();

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=0");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						String entryDateYear = Converter.toString(entryDate, "yyyy");
						String startDateYear = Converter.toString(startDate, "yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													Log.d("Check add name Category",  name);
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i,entryCategoryValue.get(i)+ value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryIDList.add(id);
												categoryIDList.add(categoryID);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryIDList.add(id);
											categoryIDList.add(categoryID);
										}

									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek)
						{
							entryIDList.add(id);
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										categoryIDList.add(categoryID);
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i,entryCategoryValue.get(i)+ value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryIDList.add(id);
												categoryIDList.add(categoryID);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryIDList.add(id);
											categoryIDList.add(categoryID);
										}

									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		for (int i = 0; i < entryCategoryName.size(); i++)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			category_list.addView(new ReportDetailCategory(this.getContext(), entryCategoryName.get(i), Converter.toLong(entryCategoryValue.get(i).toString()), entryIDList.get(i), categoryIDList.get(i)), params);
		}
	}
	
	private void getExpense() {
		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();
		List<Long> entryIDList = new ArrayList<Long>();
		List<Long> categoryIDList = new ArrayList<Long>();

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						String entryDateYear = Converter.toString(entryDate, "yyyy");
						String startDateYear = Converter.toString(startDate, "yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													Log.d("Check add name Category",  name);
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i,entryCategoryValue.get(i)+ value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryIDList.add(id);
												categoryIDList.add(categoryID);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryIDList.add(id);
											categoryIDList.add(categoryID);
										}

									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						Log.d("Check get Expense", "Check 1");
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek){
							entryIDList.add(id);
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										categoryIDList.add(categoryID);
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i,entryCategoryValue.get(i)+ value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryIDList.add(id);
												categoryIDList.add(categoryID);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryIDList.add(id);
											categoryIDList.add(categoryID);
										}

									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		for (int i = 0; i < entryCategoryName.size(); i++)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			category_list.addView(new ReportDetailCategory(this.getContext(), entryCategoryName.get(i), Converter.toLong(entryCategoryValue.get(i).toString()), entryIDList.get(i), categoryIDList.get(i)), params);
		}
	}

	private void getBorrowing() {		
		Cursor borrowingCursor = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like 'Borrowing'");
		if (borrowingCursor != null) {
			if (borrowingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(borrowingCursor.getString(borrowingCursor.getColumnIndex("Start_date")),"dd/MM/yyyy");
					if (checkMonthly)
					{
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate, "yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							String name = borrowingCursor.getString(borrowingCursor.getColumnIndex("Person_name"));
							long value = Long.parseLong(borrowingCursor.getString(borrowingCursor.getColumnIndex("Money")));
							
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
							category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
						}
					} else
					{
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek){
						
						String name = borrowingCursor.getString(borrowingCursor.getColumnIndex("Person_name"));
						long value = Long.parseLong(borrowingCursor.getString(borrowingCursor.getColumnIndex("Money")));
						
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
						category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
					}
					}
				} while (borrowingCursor.moveToNext());
			}
		}
	}

	private void getLending() {
		Cursor lendingCursor = SqlHelper.instance.select("BorrowLend", "*","Debt_type like 'Lending'");
		if (lendingCursor != null) {
			if (lendingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(lendingCursor.getString(lendingCursor.getColumnIndex("Start_date")));
					if (checkMonthly)
					{
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate, "YYYY");
						String startDateYear = Converter.toString(startDate,"YYYY");
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							String name = lendingCursor.getString(lendingCursor.getColumnIndex("Person_name"));
							long value = Long.parseLong(lendingCursor.getString(lendingCursor.getColumnIndex("Money")));
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
						}
					}else
					{
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek){
						
						String name = lendingCursor.getString(lendingCursor.getColumnIndex("Person_name"));
						long value = Long.parseLong(lendingCursor.getString(lendingCursor.getColumnIndex("Money")));
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
						category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
					}
					}
				} while (lendingCursor.moveToNext());
			}
		}

	}
}
