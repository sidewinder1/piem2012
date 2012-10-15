package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.EntryDetailRepository;
import money.Tracker.repository.EntryRepository;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EntryDetailViewActivity extends Activity {
	int entry_id;
	LinearLayout entry_list;
TextView entry_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_detail_view);

		Bundle extras = getIntent().getExtras();
		entry_id = extras.getInt("entry_id");
		entry_list = (LinearLayout) findViewById(R.id.entry_detail_list_item);
		entry_title = (TextView) findViewById(R.id.entry_detail_view_title);
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		Entry entry = (Entry)EntryRepository.getInstance().getData(new StringBuilder("Id=").append(entry_id).toString()).get(0);
		entry_title.setText(getResources().getString(
						(entry.getType() == 1 ? R.string.entry_daily_expense_title
								: R.string.entry_daily_income_title)).replace(
						"{0}", Converter.toString(entry.getDate(), "dd/MM/yyyy")));
		EntryDetailRepository.getInstance().updateData(
				new StringBuilder("Entry_Id = ").append(entry_id).toString(), "Category_Id");

		entry_list.removeAllViews();
		for (ArrayList<EntryDetail> array : EntryDetailRepository.getInstance().entries
				.values()) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			entry_list.addView(new EntryDetailCategoryView(this, array), params);
		}
	}

	public void editBtnClicked(View v) {
		Intent edit = new Intent(this, EntryEditActivity.class);
		edit.putExtra("entry_id", entry_id);
		startActivity(edit);
	}
}
