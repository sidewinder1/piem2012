package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SyncHelper;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.SyncSettingActivity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmailAccountCustomView extends LinearLayout {
	private TextView mEmailAcount, mStatus;
	private Button sync_data;
	private boolean mIsActive;
	private boolean mAutoSync;
	private boolean mIsSublist;

	public EmailAccountCustomView(Context context, boolean isSublist) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.sync_account_item, this, true);

		mEmailAcount = (TextView) findViewById(R.id.sync_email_account);
		sync_data = (Button) findViewById(R.id.sync_refresh_sync);
		mStatus = (TextView) findViewById(R.id.sync_email_account_status);
		mIsSublist = isSublist;
		if (mIsSublist) {
			mAutoSync = true;
		}

		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if (SyncHelper.getInstance().isSchronizing()) {
						return;
					}

					AccountProvider.getInstance().setCurrentAccount(
							mEmailAcount.getText().toString());

					LinearLayout parentList = (LinearLayout) getParent();
					if (parentList != null) {
						for (int index = 0; index < parentList.getChildCount(); index++) {
							EmailAccountCustomView email = (EmailAccountCustomView) parentList
									.getChildAt(index);
							if (email != null) {
								email.setActive(false);

							}
						}
					}

					setActive(true);
					sync_data.setVisibility(View.VISIBLE);
					if (mIsSublist) {
						for (int index = 0; index < parentList.getChildCount(); index++) {
							EmailAccountCustomView email = (EmailAccountCustomView) parentList
									.getChildAt(index);
							EmailAccountCustomView mainEmail = (EmailAccountCustomView) SyncSettingActivity.sAccountList
									.getChildAt(index);
							if (email != null && mainEmail != null) {
								mainEmail.setActive(email.getActive());
								mainEmail.setAutoSync(email.getAutoSync());
								mainEmail.setIconVisibility(email
										.getIconVisibility());
							}
						}
					} else {
						((AnimationDrawable) sync_data.getBackground()).start();
						SynchronizeTask syncTask = new SynchronizeTask(
								sync_data);
						syncTask.execute();
					}
				} catch (Exception e) {
					Logger.Log(e.getMessage(), getClass().toString());
				}
			}
		});
	}

	protected void setIconVisibility(int iconVisibility) {
		sync_data.setVisibility(iconVisibility);
	}

	public String getText() {
		return mEmailAcount.getText().toString();
	}

	public boolean getActive() {
		return mIsActive;
	}

	protected boolean getAutoSync() {
		return mAutoSync;
	}

	protected int getIconVisibility() {
		return sync_data.getVisibility();
	}

	public void setActive(boolean isActive) {
		sync_data
		.setBackgroundResource(isActive ? R.drawable.refresh_animation
				: R.drawable.refresh_animation2);
		if (sync_data.getVisibility()== View.VISIBLE && SyncHelper.getInstance().isSchronizing()){
			((AnimationDrawable)sync_data.getBackground()).start();
		}
		else{
			sync_data.setVisibility(isActive ? View.VISIBLE : View.GONE);
		}
		
		mIsActive = isActive && mAutoSync;
		sync_data.setTag(mIsActive);
		mStatus.setText(getResources().getString(
				mIsActive ? R.string.sync_view_account_active
						: R.string.sync_view_account_deactive));
	}

	public void setAutoSync(boolean isAuto) {
		mAutoSync = isAuto;
		if (!mAutoSync) {
			setActive(false);
		}
	}

	public void setEmailAccount(String email) {
		mEmailAcount.setText(email);
	}

	public void setStatus(String status) {
		mStatus.setText(status);
	}

	public Button getButton() {
		return sync_data;
	}
}
