package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.SyncSettingActivity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
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
					if (SynchronizeTask.isSynchronizing()) {
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
						RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(
								true);
						spinner.execute((AnimationDrawable) sync_data
								.getBackground());
						// ((AnimationDrawable)
						// sync_data.getBackground()).start();
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

	public EmailAccountCustomView(Context context) {
		super(context);
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
		boolean animating = !mIsSublist
				&& ((AnimationDrawable) sync_data.getBackground()).isRunning();

		if (animating) {
			((AnimationDrawable) sync_data.getBackground()).stop();
		}

		sync_data.setBackgroundResource(mIsSublist ? R.drawable.syn_icon_1
				: (isActive && mAutoSync ? R.drawable.refresh_animation
						: R.drawable.refresh_animation2));
		// if (sync_data.getVisibility() != View.VISIBLE) {
		sync_data
				.setVisibility(isActive || (animating && !mIsSublist) ? View.VISIBLE
						: View.GONE);
		// }

		if (!mIsSublist) {
			if ((animating && SynchronizeTask.isSynchronizing())) {
				RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(
						true);
				spinner.execute((AnimationDrawable) sync_data.getBackground());
				// ((AnimationDrawable) sync_data.getBackground()).start();
			} else {
				RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(
						false);
				spinner.execute((AnimationDrawable) sync_data.getBackground());
				// ((AnimationDrawable) sync_data.getBackground()).stop();
			}
		}

		mIsActive = isActive && mAutoSync;

		sync_data.setTag(mIsActive);
		mStatus.setText(getResources().getString(
				mIsActive ? R.string.sync_view_account_active
						: R.string.sync_view_account_deactive));
	}

	public void setAutoSync(boolean isAuto) {
		if (mIsSublist) {
			return;
		}

		mAutoSync = isAuto;
		boolean isAnimating = ((AnimationDrawable) sync_data.getBackground())
				.isRunning() && sync_data.getVisibility() == View.VISIBLE;
		
		((AnimationDrawable)sync_data.getBackground()).stop();
		sync_data.setBackgroundResource(isAuto ? R.drawable.refresh_animation
				: R.drawable.refresh_animation2);
		if (!mAutoSync) {
			setActive(false);
		}
		
//		RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(isAnimating);
//		spinner.execute((AnimationDrawable) sync_data.getBackground());
		if (isAnimating) {
			sync_data.setVisibility(View.VISIBLE);
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

	class RefreshIconSpinAsyncTask extends
			AsyncTask<AnimationDrawable, Void, Void> {
		private boolean mIsStart;

		public RefreshIconSpinAsyncTask(boolean isStart) {
			super();
			mIsStart = isStart;
		}

		@Override
		protected Void doInBackground(AnimationDrawable... arg0) {
			if (mIsStart) {
				arg0[0].start();
			} else {
				arg0[0].stop();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
}
