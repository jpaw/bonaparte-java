package de.jpaw.bonaparte.android;

import de.jpaw.bonaparte.core.BonaPortable;

import android.view.View;

public interface AndroidObjectClickListener {
    public void onClick(View view, BonaPortable object, int row, int column);
}
