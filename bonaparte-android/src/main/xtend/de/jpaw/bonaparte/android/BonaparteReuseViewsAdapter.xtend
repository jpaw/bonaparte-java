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
class BonaparteReuseViewsAdapter<T extends BonaPortable> extends BaseAdapter {
    val List<T> data
    val Context context
    val ViewProvider viewProvider
    val Map<Class<? extends BonaPortable>, List<String>> mapper
    val ReuseViewsComposer delegateComposer
    val MessageComposer<RuntimeException> foldingComposer
    
    new(Context context, ViewProvider viewProvider, List<T> data, List<String> columnNames, ReuseViewsComposer composer) {
        this.data = data
        this.context = context
        this.viewProvider = viewProvider
        val Class<? extends BonaPortable> zz = BonaPortable
        this.mapper = #{ zz -> columnNames}
        this.delegateComposer = composer
        this.foldingComposer = new FoldingComposer(composer, mapper, FoldingStrategy.SUPERCLASS_OR_FULL)
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
        val view = (cv as LinearLayout ?: viewProvider.newView)
        delegateComposer.newView(view)
        foldingComposer.writeRecord(getItem(row))
        return view
    }
}
