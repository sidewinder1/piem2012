package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.SyncSettingActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
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
						Alert.getInstance().show(
								getContext(),
								getResources().getString(
										R.string.sync_waiting_synchronization));
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
								if (email.getText().equals(
										AccountProvider.getInstance()
												.getCurrentAccount().name)) {
									email.setActive(true);
									email.setTag(true);
									email.setIconVisibility(View.VISIBLE);
								} else {
									email.setActive(false);
									email.setTag(false);
									email.setIconVisibility(View.GONE);
								}
							}
						}
					}

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
						// RefreshIconSpinAsyncTask spinner = new
						// RefreshIconSpinAsyncTask(
						// true);
						// spinner.execute((AnimationDrawable) sync_data
						// .getBackground());
						sync_data.startAnimation(AnimationUtils.loadAnimation(
								getContext(), R.anim.sync_background));

						sync_data.setTag(mAutoSync);
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
		// boolean animating = !mIsSublist
		// && (sync_data.getVisibility() == View.VISIBLE && ((AnimationDrawable)
		// sync_data
		// .getBackground()).isRunning());

		// if (animating) {
		// ((AnimationDrawable) sync_data.getBackground()).stop();
		// }

		// sync_data.setBackgroundResource(mIsSublist ? R.drawable.syn_icon_1
		// : (mAutoSync ? R.drawable.refresh_animation
		// : R.drawable.refresh_animation2));
		// // if (sync_data.getVisibility() != View.VISIBLE) {
		sync_data
				.setVisibility(mAutoSync
						&& getText().equals(
								AccountProvider.getInstance()
										.getCurrentAccount().name) ? View.VISIBLE
						: View.GONE);

		if (!mIsSublist && sync_data.getAnimation() != null) {
			if (((isActive || (sync_data.getAnimation().hasStarted())) && SynchronizeTask
					.isSynchronizing())) {
				// RefreshIconSpinAsyncTask spinner = new
				// RefreshIconSpinAsyncTask(
				// true);
				// spinner.execute((AnimationDrawable)
				// sync_data.getBackground());
				sync_data.startAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.sync_background));
				// ((AnimationDrawable) sync_data.getBackground()).start();
			} else {
				// RefreshIconSpinAsyncTask spinner = new
				// RefreshIconSpinAsyncTask(
				// false);
				// spinner.execute((AnimationDrawable)
				// sync_data.getBackground());
				// ((AnimationDrawable) sync_data.getBackground()).stop();
				sync_data.getAnimation().cancel();
				sync_data.getAnimation().reset();
				sync_data.clearAnimation();
			}
		}

		mIsActive = isActive && mAutoSync;

		mStatus.setText(getResources().getString(
				mIsActive ? R.string.sync_view_account_active
						: R.string.sync_view_account_deactive));
	}

	public void setAutoSync(boolean isAuto) {
		if (mIsSublist) {
			return;
		}

		mAutoSync = isAuto;
		sync_data.setBackgroundResource(mAutoSync ? R.drawable.syn_icon_1
				: R.drawable.unsyn_icon_1);
		// ((AnimationDrawable) sync_data.getBackground()).stop();
		// sync_data.setBackgroundResource(isAuto ? R.drawable.refresh_animation
		// : R.drawable.refresh_animation2);

		sync_data.setTag(mAutoSync);
		setActive(mAutoSync && mIsActive);

		// RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(
		// isAnimating);
		// spinner.execute((AnimationDrawable) sync_data.getBackground());
		//
		if (sync_data.getVisibility() == View.VISIBLE
				&& SynchronizeTask.isSynchronizing()) {

			sync_data.startAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.sync_background));
			// RefreshIconSpinAsyncTask spinner = new RefreshIconSpinAsyncTask(
			// true);
			// spinner.execute((AnimationDrawable) sync_data.getBackground());
		} else {
			// ((AnimationDrawable) sync_data.getBackground()).stop();
			if (sync_data.getAnimation() != null) {
				sync_data.getAnimation().cancel();
				sync_data.getAnimation().reset();
			}

			sync_data.clearAnimation();

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
