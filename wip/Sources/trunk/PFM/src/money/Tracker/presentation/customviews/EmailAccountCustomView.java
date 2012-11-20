package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmailAccountCustomView extends LinearLayout {
	private  TextView mEmailAcount, mStatus;
	private Button sync_data;
	private AnimationDrawable mAnimationDrawable;
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
				try {
					AccountProvider.getInstance().currentAccount = AccountProvider
							.getInstance().findAccountByEmail(
									mEmailAcount.getText().toString());
					sync_data.setBackgroundResource(R.drawable.refresh_animation);
					mAnimationDrawable = (AnimationDrawable) sync_data.getBackground();
					mAnimationDrawable.start();
					SynchronizeTask syncTask = new SynchronizeTask(sync_data);
					syncTask.execute();
				} catch (Exception e) {
					Logger.Log(e.getMessage(), getClass().toString());
				}
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
