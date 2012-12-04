package money.Tracker.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.EditText;

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

	public Cursor getContactAddress(String id) {
		String[] projection = new String[] { ContactsContract.CommonDataKinds.StructuredPostal.STREET };
		// ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME};
		Log.d("Contact", id);
		Cursor returnCursor = ctx.getContentResolver().query(
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
				projection, null, null, null);
		Log.d("Contact", "Get contact completed");
		return returnCursor;
	}

	public String getAddress(String _name) {
		String returnNameAddress = "";

		ContentResolver cr = ctx.getContentResolver();

		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (_name.equals(name)) {
					// Get Postal Address....
					String addrWhere = ContactsContract.Data.CONTACT_ID
							+ " = ? AND " + ContactsContract.Data.MIMETYPE
							+ " = ?";
					String[] addrWhereParams = new String[] {
							id,
							ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
					// Cursor addrCur =
					// cr.query(ContactsContract.Data.CONTENT_URI,
					// null, null, null, null);
					Cursor addrCur = cr.query(
							ContactsContract.Data.CONTENT_URI, null, addrWhere,
							addrWhereParams, null);

					while (addrCur.moveToNext()) {
						String street = addrCur
								.getString(addrCur
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
						String city = addrCur
								.getString(addrCur
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
						String state = addrCur
								.getString(addrCur
										.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));

						// Do something with these....
						if (street == null) {
							street = "";
						}

						if (city == null) {
							city = "";
						} else {
							city = ", " + city;
						}

						if (state == null) {
							state = "";
						} else {
							state = ", " + state;
						}

						returnNameAddress = street + city + state;

					}
					addrCur.close();
				}
			}
		}

		return returnNameAddress.trim();
	}
}
