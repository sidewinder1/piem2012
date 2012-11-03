package money.Tracker.common.utilities;

import java.io.UnsupportedEncodingException;

import android.nfc.NdefRecord;

public class NfcHelper {
	  private final String mLanguageCode;

	    private final String mText;

	    private NfcHelper(String languageCode, String text) {
	            mLanguageCode = languageCode;
	            mText = text;
	    }

	    public String getText() {
	            return mText;
	    }

	    /**
	     * Returns the ISO/IANA language code associated with this text element.
	     */
	    public String getLanguageCode() {
	            return mLanguageCode;
	    }

	    // TODO: deal with text fields which span multiple NdefRecords
	    public static NfcHelper parse(NdefRecord record) {
	            try {
	                    byte[] payload = record.getPayload();

	                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
	                                    : "UTF-16";
	                    int languageCodeLength = payload[0] & 0077;
	                    String languageCode = new String(payload, 1, languageCodeLength,
	                                    "US-ASCII");
	                    String text = new String(payload, languageCodeLength + 1,
	                                    payload.length - languageCodeLength - 1, textEncoding);

	                    return new NfcHelper(languageCode, text);
	            } catch (UnsupportedEncodingException e) {
	                    // should never happen unless we get a malformed tag.
	                    throw new IllegalArgumentException(e);
	            }
	            catch(Exception x){
	                    throw new IllegalArgumentException("Error parsing as a TextRecord: "+x.getMessage());
	            }
	    }

	    public static boolean isText(NdefRecord record) {
	            try {
	                    parse(record);
	                    return true;
	            } catch (IllegalArgumentException e) {
	                    return false;
	            }
	    }

	    public String getTag() {
	            return getText();
	    }
}
