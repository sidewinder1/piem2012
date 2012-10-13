package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.EntryDetailRepository;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class EntryDetailViewActivity extends Activity {
	int entry_id;
	LinearLayout entry_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_detail_view);

		Bundle extras = getIntent().getExtras();
		entry_id = extras.getInt("entry_id");
		entry_list = (LinearLayout) findViewById(R.id.entry_detail_list_item);
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		EntryDetailRepository.getInstance().updateData(
				new StringBuilder("Entry_Id = ").append(entry_id).toString());

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
