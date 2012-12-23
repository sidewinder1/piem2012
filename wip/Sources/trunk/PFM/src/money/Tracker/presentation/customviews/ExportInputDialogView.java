package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ExportInputDialogView extends LinearLayout {
	public EditText mNameValue;
	public CheckBox mIsImport, mIsExcel;

	/**
	 * Constructor of dialog.
	 * @param context
	 * Parent context.
	 */
	public ExportInputDialogView(Context context) {
		super(context);
		initializeComponent();
	}

	/**
	 * Initialize component of custom control.
	 */
	private void initializeComponent() {
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.import_export_input_dialog, this, true);

		mNameValue = (EditText) findViewById(R.id.file_name_value);
		mIsExcel = (CheckBox) findViewById(R.id.export_choose_excel);
		mIsImport = (CheckBox) findViewById(R.id.export_choose_import);
	}
}
