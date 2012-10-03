package money.Tracker.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactInfoRepository {
	private Context ctx;
	
	public ContactInfoRepository(Context ctx) {
		// TODO Auto-generated constructor stub
		this.ctx = ctx;
	}
	
	public Cursor getContacts2(String where) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.TYPE,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		Cursor people = ctx.getContentResolver().query(
				uri,
				projection,
				null,
				null,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		return people;
	}
}
