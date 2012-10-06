package money.Tracker.presentation.adapters;

import money.Tracker.presentation.activities.R;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

public class ContactsAutoCompleteCursorAdapter extends CursorAdapter implements
		Filterable {
	private TextView mName, mNumber;
	private ContentResolver mContent;

	public ContactsAutoCompleteCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mContent = context.getContentResolver();
	}

	@Override public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{      
		final LayoutInflater mInflater = LayoutInflater.from(context);     
		final View ret = mInflater.inflate(R.layout.contacts_auto_list, null);      
		
		return ret; 
		
	} 

	@Override 
	public void bindView(View view, Context context, Cursor cursor) 
	{
		int nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);		
		int numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		String name = cursor.getString(nameIdx);     		
		String number = cursor.getString(numberIdx);      
		
		mName = (TextView) view.findViewById(R.id.name);     
		mNumber = (TextView) view.findViewById(R.id.phonenum);      
		mName.setText(name);          
		mNumber.setText(number); 
	} 

	@Override
	public String convertToString(Cursor cursor) {
		int nameCol = cursor
				.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		String name = cursor.getString(nameCol);
		return name;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// this is how you query for suggestions
		// notice it is just a StringBuilder building the WHERE clause of a
		// cursor which is the used to query for results
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		return mContent.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				"UPPER(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						+ ") LIKE '%" + constraint.toString().toUpperCase()
						+ "%'", null, ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
	}
}