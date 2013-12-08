package de.jpaw.bonaparte.android

import android.widget.LinearLayout
import android.content.Context
import java.util.List
import de.jpaw.bonaparte.pojos.ui.UIColumn
import android.widget.TextView
import de.jpaw.bonaparte.pojos.ui.LayoutHint
import android.widget.ImageView
import android.widget.CheckBox

interface ViewProvider {
    def LinearLayout newView()
}

public class IdCounter {
    private int ctr = -1
    def public int getId() { ctr }
    def public int incrementAndGet() { ctr = ctr + 1 }
}


class DefaultViewProvider implements ViewProvider {
    
    private final Context context
    private final List<UIColumn> columns
    private final extension Density density           // for scaling
    
    new(Context context, Density density, List<UIColumn> columns) {
        this.context = context
        this.columns = columns
        this.density = density
    }
    
    override public LinearLayout newView() {
        val ctr = new IdCounter
        val myView = new LinearLayout(context) => [
            orientation = LinearLayout.HORIZONTAL
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            for (e : columns) {
                val viewType = e.layoutHint ?: LayoutHint.TEXT
                switch (viewType) {
                    case LayoutHint.IMAGE:
                        addView(new ImageView(context) => [
                            maxHeight = 48.dp2px
                            maxWidth = e.width.dp2px
                            scaleType = ImageView.ScaleType.CENTER_INSIDE
                            id = 424200000 + ctr.incrementAndGet
                        ])
                    case LayoutHint.CHECKBOX:
                        addView(new CheckBox(context) => [
                            height = 48.dp2px
                            width = e.width.dp2px
                            id = 424200000 + ctr.incrementAndGet
                        ])
                    default:
                        addView(new TextView(context) => [
                            height = 48.dp2px
                            width = e.width.dp2px
                            id = 424200000 + ctr.incrementAndGet
                        ])
                }
            }
        ]
        return myView
    }
}