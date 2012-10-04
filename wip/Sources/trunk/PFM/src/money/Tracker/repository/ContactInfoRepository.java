package money.Tracker.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
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
				//ContactsContract.CommonDataKinds.Phone.TYPE,
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
		String addrWhere = ContactsContract.Data._ID + " = ? AND "
				+ ContactsContract.Data.MIMETYPE + " = ?";
		String[] addrWhereParameters = new String[] {
				id,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
		 Cursor addrCur = ctx.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null,
				addrWhere, addrWhereParameters, null);
		
		return addrCur;
	}
	
	public String findContact(String display_name, String phone_number) 
	{      
		ContentResolver contentResolver = ctx.getContentResolver();     
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;     
		String[] projection = new String[] { PhoneLookup._ID };     
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";     
		String[] selectionArguments = {display_name, phone_number};     
		Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);      
		if (cursor != null) 
		{         
			while (cursor.moveToNext()) 
			{             
				return cursor.getString(0);         
			}     
		}     
		return ""; 
	} 
}
