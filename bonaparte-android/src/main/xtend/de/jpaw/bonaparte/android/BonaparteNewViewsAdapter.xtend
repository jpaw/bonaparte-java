package de.jpaw.bonaparte.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import de.jpaw.bonaparte.core.BonaPortable
import de.jpaw.bonaparte.core.FoldingComposer
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy
import java.util.List
import java.util.Map
import de.jpaw.bonaparte.core.MessageComposer

/**
 * Adapter for the ListView to create selected fields from an object via a FoldingComposer.
 */
class BonaparteNewViewsAdapter<T extends BonaPortable> extends BaseAdapter {
    val List<T> data
    val Context context
    val Map<Class<? extends BonaPortable>, List<String>> mapper
    val NewViewsComposer delegateComposer
    val MessageComposer<RuntimeException> foldingComposer
    
    new(Context context, List<T> data, List<String> columnNames, NewViewsComposer composer) {
        this.data = data
        this.context = context
        val Class<? extends BonaPortable> zz = BonaPortable
        this.mapper = #{ zz -> columnNames}
        this.delegateComposer = composer
        this.foldingComposer = new FoldingComposer(composer, mapper, FoldingStrategy.FORWARD_OBJECTS)
    }

    override getCount() {
        data.length
    }

    override T getItem(int row) {
        data.get(row)
    }

    override getItemId(int row) {
        return row as long
    }

    override View getView(int row, View cv, ViewGroup root) {
        val view = new LinearLayout(context) => [
            orientation = LinearLayout.HORIZONTAL
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        ]
        delegateComposer.newView(view, row)
        foldingComposer.writeRecord(getItem(row))
        return view
    }
}
