package money.Tracker.presentation.customviews;

import java.util.ArrayList;
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

public class ReportDetailCategory extends LinearLayout {

	private LinearLayout category_list;
	private boolean checkMonthly;
	private Date startDate;
	private Date endDate;
	private boolean changedState = false;
	private String checkName = "";
	
	public ReportDetailCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ReportDetailCategory(Context context, String name, long value, boolean checkMonthly, Date startDate, Date endDate) {
		super(context);

		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_detail_category, this, true);
		
		this.checkMonthly = checkMonthly;
		this.startDate = startDate;
		this.endDate = endDate;
		this.checkName = name;

		// Get control from .xml file.
		TextView product_name = (TextView) findViewById(R.id.report_detail_category_item_name);
		TextView product_total = (TextView) findViewById(R.id.report_detail_category_item_total);
		category_list = (LinearLayout) findViewById(R.id.report_detail_category_list);
		Button collapsedButton = (Button) findViewById(R.id.report_collapsed_button);
		category_list.removeAllViews();
		
		// Set value to category.
		product_name.setText(name);
		product_total.setText(Converter.toString(value));
		
		if (name.equals("Budget"))
		{
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
			if (checkName.equals("Income"))
				getIncome();
			else if (checkName.equals("Expense"))
				getExpense();
			else if (checkName.equals("Borrowing"))
				getBorrowing();
			else if (checkName.equals("Lending"))
				getLending();
			
			changedState = true;
		} else
		{
			category_list.removeAllViews();
			changedState = false;
		}
	}
	
	private void getIncome() {
		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();

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

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
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
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}

									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						if (entryDate.compareTo(startDate) > 0
								&& entryDate.compareTo(endDate) < 0
								|| entryDate.compareTo(startDate) == 0
								|| entryDate.compareTo(endDate) == 0) {
							Cursor entryDetailCursor = SqlHelper.instance
									.select("EntryDetail",
											"Category_Id, sum(Money) as Total",
											"Entry_Id=" + id
													+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										int categoryID = entryDetailCursor
												.getInt(entryDetailCursor
														.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance
												.select("Category", "*", "Id="
														+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor
															.getString(categoryCursor
																	.getColumnIndex("Name"));
												} while (categoryCursor
														.moveToNext());
											}
										}

										value = entryDetailCursor
												.getLong(entryDetailCursor
														.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName
													.size(); i++) {
												if (entryCategoryName.get(i)
														.equals(name)) {
													entryCategoryValue.set(
															i,
															entryCategoryValue
																	.get(i)
																	+ value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
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
			category_list.addView(new ReportDetailProduct(this.getContext(), entryCategoryName.get(i), Converter.toLong(entryCategoryValue.get(i).toString())), params);
		}
	}
	
	private void getExpense() {
		long totalExpense = 0;

		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*","Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate,"MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate,"yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
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
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
								|| entryDate.compareTo(startDate) == 0
								|| entryDate.compareTo(endDate) == 0) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;										

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
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
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		if (totalExpense != 0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			for (int i = 0; i < entryCategoryName.size(); i++)
			{
				category_list.addView(new ReportDetailProduct(this.getContext(), entryCategoryName.get(i), entryCategoryValue.get(i)), params);
			}
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
						String entryDateYear = Converter.toString(entryDate, "YYYY");
						String startDateYear = Converter.toString(startDate,"YYYY");
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							String name = borrowingCursor.getString(borrowingCursor.getColumnIndex("Person_name"));
							long value = Long.parseLong(borrowingCursor.getString(borrowingCursor.getColumnIndex("Money")));
							
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
						}
					} else
					{
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						
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
		Cursor lendingCursor = SqlHelper.instance.select("BorrowLend", "*",
				"Debt_type like 'Lending'");
		if (lendingCursor != null) {
			if (lendingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(lendingCursor
							.getString(lendingCursor
									.getColumnIndex("Start_date")));
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
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0 || entryDate.compareTo(endDate) == 0) {
						
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
