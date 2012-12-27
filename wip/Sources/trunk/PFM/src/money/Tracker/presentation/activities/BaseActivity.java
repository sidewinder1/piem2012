package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.ExcelHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.ViewHelper;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.customviews.ExportInputDialogView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author Kaminari.hp
 * 
 */
public abstract class BaseActivity extends Activity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		super.onCreate(savedInstanceState);
	};

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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
						getBaseContext());
				final Dialog dialog = ViewHelper.createAppDialog(this,
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

								checkFileName(inputDialog.mNameValue.getText(),
										button);
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

								checkFileName(inputDialog.mNameValue.getText(),
										button);
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

								checkFileName(s, button);
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

						Alert.getInstance().show(
								getBaseContext(),
								getResources().getString(
										R.string.exported_successfully)
										.replace(
												"{0}",
												inputDialog.mNameValue
														.getText().toString()));
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

	/**
	 * Check file name validation of a string.
	 * 
	 * @param str
	 *            A string is checked.
	 * 
	 * @param button
	 *            Related save button.
	 */
	private void checkFileName(Editable str, View button) {
		if (!str.toString().matches(
				"([a-z]|[A-Z])+((_*|[.]?)([a-z]|[A-Z]|[0-9])+)*")) {
			Alert.getInstance().show(getBaseContext(),
					getResources().getString(R.string.invalid_file_name));
			button.setBackgroundResource(R.drawable.save_icon_disabled);
			button.setEnabled(false);
		} else {
			button.setBackgroundResource(R.drawable.save_icon);
			button.setEnabled(true);
		}
	}
}
