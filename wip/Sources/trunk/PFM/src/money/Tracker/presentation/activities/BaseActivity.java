package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.ExcelHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {
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
				ExcelHelper.getInstance().exportData("data.xls");
				Alert.getInstance().show(this,
						getResources().getString(R.string.saved));
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
					new String[] { ".xls" });
			explorerIntent.putExtra("FileExplorerActivity.file_icon",
					R.drawable.xls_file_icon);
			PfmApplication.getAppContext().startActivity(explorerIntent);
			break;
		}

		return true;
	}
}
