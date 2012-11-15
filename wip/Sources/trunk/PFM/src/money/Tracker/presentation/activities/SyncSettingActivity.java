package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.presentation.adapters.AccountViewAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SyncSettingActivity extends Activity {
	ListView mAccountList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sync_setting);
		mAccountList = (ListView) findViewById(R.id.sync_view_account_list);
		AccountViewAdapter adapter = new AccountViewAdapter(this,
				R.layout.sync_account_item, AccountProvider.getInstance()
						.getAccounts());
		mAccountList.setAdapter(adapter);
	}
}
