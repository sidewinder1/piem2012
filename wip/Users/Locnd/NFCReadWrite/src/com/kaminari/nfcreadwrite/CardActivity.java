package com.kaminari.nfcreadwrite;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class CardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    Intent intent = getIntent();
	    if(intent.getType() != null && intent.getType().equals("com.kaminari.nfcreadwrite")) {
	       Parcelable[] rawMsgs =
	    getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	        NdefMessage msg = (NdefMessage) rawMsgs[0];
	        NdefRecord cardRecord = msg.getRecords()[0];
	        String consoleName = new String(cardRecord.getPayload());
	        displayCard(consoleName);
	    }

	}

	private void displayCard(String consoleName) {
		// TODO Auto-generated method stub
		Log.d("kaminari.hp.cardactvity", consoleName);
	}
}
