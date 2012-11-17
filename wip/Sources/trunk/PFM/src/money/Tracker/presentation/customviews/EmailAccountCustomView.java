package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.SyncHelper;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmailAccountCustomView extends LinearLayout {
	TextView mEmailAcount, mStatus;
	Button sync_data;

	public EmailAccountCustomView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.sync_account_item, this, true);

		mEmailAcount = (TextView) findViewById(R.id.sync_email_account);
		sync_data = (Button) findViewById(R.id.sync_refresh_sync);
		mStatus = (TextView) findViewById(R.id.sync_email_account_status);

		sync_data.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				SyncHelper.getInstance().synchronize();
			}
		});
	}

	public void setEmailAccount(String email) {
		mEmailAcount.setText(email);
	}

	public void setStatus(String status) {
		mStatus.setText(status);
	}
}
