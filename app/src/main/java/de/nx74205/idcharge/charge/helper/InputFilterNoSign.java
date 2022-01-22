package de.nx74205.idcharge.charge.helper;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterNoSign implements InputFilter {


    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if ((source.toString() + dest.toString()).contains("-")) {
            return "";
        }
        return null;

    }

}