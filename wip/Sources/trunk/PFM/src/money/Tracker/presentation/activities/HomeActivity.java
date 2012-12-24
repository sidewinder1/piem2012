package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.ExcelHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.ViewHelper;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.customviews.ExportInputDialogView;
import android.os.Bundle;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * @author Kaminari.hp
 * 
 */
public class HomeActivity extends TabActivity {
	private final String mTypeTabPathId = "type.tab.path.id";
	public static int sCurrentTab = 0;
	private static Context sContext;
	private float initialX = 0;
	private float initialY = 0;
	private float deltaX = 0;
	private float deltaY = 0;
	private TabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setWindowAnimations(2);
		setContentView(R.layout.home_activity);

		// All of code blocks for initialize view should be placed here.
		mTabHost = getTabHost();
		sContext = mTabHost.getContext();
		// mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);

		// Create Expense & income tab.
		Intent managementIntent = new Intent(this, MainViewActivity.class);
		managementIntent.putExtra(mTypeTabPathId, 0);
		setupTab(managementIntent, "Expenses\n& Incomes", mTabHost,
				R.drawable.tab_bg_selector_entry);

		// Create tab and intent for schedule.
		Intent scheduleIntent = new Intent(this, MainViewActivity.class);
		scheduleIntent.putExtra(mTypeTabPathId, 1);
		setupTab(scheduleIntent, "Schedule", mTabHost,
				R.drawable.tab_bg_selector_schedule);

		// Create tab and intent for Borrowing and Lending.
		Intent borrowAndLendIntent = new Intent(this,
				BorrowLendMainViewActivity.class);
		setupTab(borrowAndLendIntent, "Borrowing\n& Lending", mTabHost,
				R.drawable.tab_bg_selector_borrow);

		// Create tab and intent for report
		Intent reportIntent = new Intent(this, ReportMainViewActivity.class);
		setupTab(reportIntent, "Báo cáo", mTabHost,
				R.drawable.tab_bg_selector_report);

		mTabHost.setCurrentTab(sCurrentTab);
		Alert.getInstance().stopNotify();
	}

	/**
	 * Initialize tabs for activity tab.
	 * 
	 * @param intent
	 *            Type of intent of tab.
	 * @param tag
	 *            The name of tabs that should be displayed on tab.
	 * @param mTabHost
	 *            The tab host parent that contains tabs.
	 * @param resourceId
	 *            Resource id of image on tab.
	 */
	public static void setupTab(final Intent intent, final String tag,
			TabHost mTabHost, final int resourceId) {
		View tabview = createTabView(mTabHost.getContext(), resourceId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);
		mTabHost.addTab(setContent);
	}

	/**
	 * Create a tab view with specified resource id image.
	 * 
	 * @param context
	 *            Parent data context.
	 * @param id
	 *            Resource id image.
	 * @return The view that is displayed on tab.
	 */
	private static View createTabView(final Context context, final int id) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.main_tab_background, null);
		// view.setBackgroundResource(id);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.main_tab_background_icon);
		imageView.setImageResource(id);
		return view;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// reset deltaX and deltaY
			deltaX = deltaY = 0;
			sCurrentTab = mTabHost.getCurrentTab();
			// get initial positions
			initialX = event.getRawX();
			initialY = event.getRawY();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			deltaX = event.getRawX() - initialX;
			deltaY = event.getRawY() - initialY;

			if (Math.abs(deltaX) <= Math.abs(deltaY)) {
				return false;
			}

			// Swiped up
			if (deltaX > 10) {
				// make your object/character move left
				sCurrentTab = Math.max(sCurrentTab - 1, 0);
				mTabHost.setCurrentTab(sCurrentTab);
			} else if (deltaX < -10) {
				// make your object/character move right
				sCurrentTab = Math.min(sCurrentTab + 1, 3);
				mTabHost.setCurrentTab(sCurrentTab);
			}
		}

		return false;
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_activity, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent sync = new Intent(this, SyncSettingActivity.class);
			startActivity(sync);
			break;
		case R.id.menu_export:
			try {
				final ExportInputDialogView inputDialog = new ExportInputDialogView(
						sContext);
				final Dialog dialog = ViewHelper.createAppDialog(sContext,
						R.string.input_dialog_title, inputDialog);
				View button = dialog.findViewById(R.id.app_dialog_doneBtn);
				button.setBackgroundResource(R.drawable.save_icon_disabled);
				button.setEnabled(false);
				inputDialog.mIsExcel
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								View button = dialog
										.findViewById(R.id.app_dialog_doneBtn);
								if ((isChecked || inputDialog.mIsImport
										.isChecked())
										&& !"".equals(inputDialog.mNameValue
												.getText().toString())) {
									button.setBackgroundResource(R.drawable.save_icon);
									button.setEnabled(true);
								} else {
									button.setBackgroundResource(R.drawable.save_icon_disabled);
									button.setEnabled(false);
								}
							}
						});

				inputDialog.mIsImport
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								View button = dialog
										.findViewById(R.id.app_dialog_doneBtn);
								if ((isChecked || inputDialog.mIsExcel
										.isChecked())
										&& !"".equals(inputDialog.mNameValue
												.getText().toString())) {
									button.setBackgroundResource(R.drawable.save_icon);
									button.setEnabled(true);
								} else {
									button.setBackgroundResource(R.drawable.save_icon_disabled);
									button.setEnabled(false);
								}
							}
						});

				inputDialog.mNameValue
						.addTextChangedListener(new TextWatcher() {
							public void onTextChanged(CharSequence s,
									int start, int before, int count) {
							}

							public void beforeTextChanged(CharSequence s,
									int start, int count, int after) {
							}

							public void afterTextChanged(Editable s) {
								View button = dialog
										.findViewById(R.id.app_dialog_doneBtn);
								if ((inputDialog.mIsExcel.isChecked() || inputDialog.mIsImport
										.isChecked())
										&& !"".equals(s.toString())) {
									button.setBackgroundResource(R.drawable.save_icon);
									button.setEnabled(true);
								} else {
									button.setBackgroundResource(R.drawable.save_icon_disabled);
									button.setEnabled(false);
								}
							}
						});

				ViewHelper.attachAction(dialog, new View.OnClickListener() {
					public void onClick(View arg0) {
						if (inputDialog.mIsExcel.isChecked()) {
							ExcelHelper.getInstance()
									.exportFile(
											inputDialog.mNameValue.getText()
													.toString(), false);
						}
						if (inputDialog.mIsImport.isChecked()) {
							ExcelHelper.getInstance()
									.exportFile(
											inputDialog.mNameValue.getText()
													.toString(), true);
						}

						Alert.getInstance().show(getBaseContext(),
								getResources().getString(R.string.saved));
						dialog.dismiss();
					}
				}, new View.OnClickListener() {
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});

				dialog.show();
			} catch (Exception e) {
				Alert.getInstance().show(this,
						getResources().getString(R.string.error_load));
				Logger.Log(e.getMessage(), "HomeViewActivity");
			}
			break;
		case R.id.menu_import:
			Intent explorerIntent = new Intent(PfmApplication.getAppContext(),
					FileExplorerActivity.class);
			explorerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			explorerIntent.putExtra("FileExplorerActivity.allowedExtension",
					new String[] { ".pif" });
			explorerIntent.putExtra("FileExplorerActivity.file_icon",
					R.drawable.xls_file_icon);
			PfmApplication.getAppContext().startActivity(explorerIntent);
			break;
		}

		return true;
	}
}