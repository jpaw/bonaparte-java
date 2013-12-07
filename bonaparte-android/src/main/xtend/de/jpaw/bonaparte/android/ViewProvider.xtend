package de.jpaw.bonaparte.android

import android.widget.LinearLayout

interface ViewProvider {
    def LinearLayout newView()
}