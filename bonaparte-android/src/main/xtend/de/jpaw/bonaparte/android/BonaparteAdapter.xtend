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

/**
 * Adapter for the ListView to create selected fields from an object via a FoldingComposer.
 */
class BonaparteAdapter<T extends BonaPortable> extends BaseAdapter {
    val List<T> data
    val Context context
    val ViewProvider viewProvider
    val Map<Class<? extends BonaPortable>, List<String>> mapper

    new(Context context, ViewProvider viewProvider, List<T> data, List<String> columnNames) {
        this.data = data
        this.context = context
        this.viewProvider = viewProvider
        val Class<? extends BonaPortable> zz = BonaPortable
        mapper = #{ zz -> columnNames}
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

//    override getView(int row, View cv, ViewGroup root) {
//        val view = cv as LinearLayout ?: viewProvider.newView
//        val i = getItem(row)
//        val linearLayoutComposer = new LinearLayoutComposer(view)
//        val foldingComposer = new FoldingComposer(linearLayoutComposer, mapper, FoldingStrategy.SUPERCLASS_OR_FULL)
//        foldingComposer.writeRecord(i)
//        view
//    }

    // single liner: This is the beauty of xtend!
    override View getView(int row, View cv, ViewGroup root) {
        (cv as LinearLayout ?: viewProvider.newView) => [
            new FoldingComposer(new LinearLayoutComposer(it), mapper, FoldingStrategy.SUPERCLASS_OR_FULL).writeRecord(getItem(row))
        ]
    }

}
