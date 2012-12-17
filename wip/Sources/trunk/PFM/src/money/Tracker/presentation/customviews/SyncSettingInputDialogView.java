package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SyncSettingInputDialogView extends LinearLayout {
	public static final int SCHEDULE_WARNING_BEFORE = 1;
	public static final int WARNING_REMIND = 2;
	public static final int BORROW_WARNING_BEFORE = 3;

	public TextView mUnitText;
	public EditText mInputValue;
	public Button mOkBtn, mCancelBtn;

	/*
	 * <summary>Constructor of dialog.</summary> <parameter
	 * value="context">parent context.</parameter> <parameter
	 * value="dialogType">type of view of dialog, including: the time that
	 * dialog warns schedule function, borrow before, remind time, </parameter>
	 */
	public SyncSettingInputDialogView(Context context) {
		super(context);
		initializeComponent();
	}

	/*
	 * <summary>Constructor of dialog.</summary> <parameter
	 * value="context">parent context.</parameter> <parameter
	 * value="dialogType">type of view of dialog, including: the time that
	 * dialog warns schedule function, borrow before, remind time, </parameter>
	 */
	public SyncSettingInputDialogView(Context context, int dialogType) {
		super(context);
		initializeComponent();
		initializeDisplayView(dialogType);
	}

	private void initializeDisplayView(int type) {
		switch (type) {
		case SCHEDULE_WARNING_BEFORE:
			mUnitText.setText(R.string.percent);
			mInputValue
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							2) });
			break;
		case BORROW_WARNING_BEFORE:
			mUnitText.setText(R.string.hours);
			break;
		case WARNING_REMIND:
			mUnitText.setText(R.string.minutes);
			break;
		}
	}

	private void initializeComponent() {
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.sync_setting_input_dialog, this, true);

		mUnitText = (TextView) findViewById(R.id.sync_dialog_unit);
		mInputValue = (EditText) findViewById(R.id.sync_dialog_value_box);
		mOkBtn = (Button) findViewById(R.id.sync_setting_doneBtn);
		mCancelBtn = (Button) findViewById(R.id.sync_setting_cancelBtn);
		Drawable drawable = getResources().getDrawable(R.drawable.save_icon);
		drawable.setAlpha(45);
		mOkBtn.setBackgroundDrawable(drawable);
		mOkBtn.setEnabled(false);

		mInputValue.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (("".equals(s.toString()) || Converter.toLong(s.toString()) == 0)) {
					if (mOkBtn.isEnabled()) {
						mOkBtn.getBackground().setAlpha(45);
						mOkBtn.setEnabled(false);
					}
				} else if (!mOkBtn.isEnabled()) {
					mOkBtn.setEnabled(true);
					Drawable drawable = getResources().getDrawable(
							R.drawable.save_icon);
					drawable.setAlpha(255);
					mOkBtn.setBackgroundDrawable(drawable);
				}
			}
		});
	}

	public EditText getInputValue() {
		return mInputValue;
	}

	public TextView getTitle() {
		TextView title = new TextView(getContext());
		title.setTextAppearance(getContext(), R.style.HeaderTitle);
		title.setText(R.string.input_dialog_title);
		title.setBackgroundResource(R.drawable.title_background);
		title.setPadding(10, 10, 10, 10);
		return title;
	}

	public void setPositiveButton(OnClickListener action) {
		mOkBtn.setOnClickListener(action);
	}

	public void setNegativeButton(OnClickListener action) {
		mCancelBtn.setOnClickListener(action);
	}
}
