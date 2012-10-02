package money.Tracker.common.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {
    public EditText mEditText;

    public CustomTextWatcher(EditText e) {
        mEditText = e;
    }
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
    public void afterTextChanged(Editable s) {
    }
}
