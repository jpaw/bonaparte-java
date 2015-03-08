package de.jpaw.bonaparte.android;

import java.util.List;

import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.ui.UIColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewViewsComposer extends LinearLayoutComposer {
    static private final Logger LOG = LoggerFactory.getLogger(NewViewsComposer.class);
    static private final int HEIGHT_IN_DP = 48;

    protected LinearLayout rowWidget;

    private final Context context;
    private final List<UIColumn> columns;
    private final Density density;           // for scaling
    private final int height_in_px;

    public NewViewsComposer(Context context, Density density, List<UIColumn> columns) {
        this.context = context;
        this.columns = columns;
        this.density = density;
        this.rowWidget = null;
        column = -1;
        rownum = 0;
        height_in_px = density.dp2px(HEIGHT_IN_DP);
    }

    // creates a new composer, with support of widget auto-creation

    @Override
    public void newView(final LinearLayout rowWidget, int row) {
        LOG.debug("newView called");
        this.rowWidget = rowWidget;
        this.rownum = row;
        rowWidget.removeAllViews();
        column = -1;
    }


    @Override
    protected CheckBox needCheckBox(FieldDefinition di) {
        LOG.debug("needCheckBox() called for {} for column {}", di.getName(), column+1);
        UIColumn c = columns.get(++column);
        CheckBox v = new CheckBox(context);
        v.setHeight(height_in_px);
        v.setWidth(density.dp2px(c.getWidth()));
        v.setId(getId());
        v.setGravity(Gravity.CENTER);
        rowWidget.addView(v);
        return v;
    }


    @Override
    protected TextView needTextView(FieldDefinition di) {
        LOG.debug("needTextView() called for {} for column {}", di.getName(), column+1);
        UIColumn c = columns.get(++column);
        TextView v = new TextView(context);
        v.setHeight(height_in_px);
        v.setWidth(density.dp2px(c.getWidth()));
        v.setId(getId());
        v.setPadding(4,2,4,2); // left top right bottom
        v.setSingleLine(true);
        v.setEllipsize(TextUtils.TruncateAt.END);
        if (di instanceof BasicNumericElementaryDataItem)
            v.setGravity(Gravity.RIGHT);
        rowWidget.addView(v);
        return v;
    }

    @Override
    protected Button needButton(FieldDefinition di) {
        LOG.debug("needButton() called for {} for column {}", di.getName(), column+1);
        UIColumn c = columns.get(++column);
        Button v = new Button(context);
        v.setHeight(height_in_px);
        v.setWidth(density.dp2px(c.getWidth()));
        v.setId(getId());
        v.setPadding(4,2,4,2); // left top right bottom
        v.setGravity(Gravity.CENTER);
        v.setOnClickListener(this);
        rowWidget.addView(v);
        return v;
    }


    @Override
    protected ImageView needImageView(FieldDefinition di) {
        LOG.debug("needImageView() called for {} for column {}", di.getName(), column+1);
        UIColumn c = columns.get(++column);
        ImageView v = new ImageView(context);
        v.setMaxHeight(height_in_px);
        v.setMaxWidth(density.dp2px(c.getWidth()));
        v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        v.setId(getId());
        rowWidget.addView(v);
        return v;
    }


    @Override
    protected View needAny(FieldDefinition di) {
        LOG.debug("needAny() called for {} for column {}", di.getName(), column+1);
        switch (di.getDataCategory()) {
        case OBJECT:
            return needButton(di);  // this should allow a drill into the object
        case MISC:
            if (di.getDataType().toLowerCase().equals("boolean"))
                return needCheckBox(di);
            else
                return needTextView(di);
        case BINARY:
            if (binaryAsBitmap)
                return needImageView(di);
            else
                return needTextView(di);
        default:
            return needTextView(di);
        }
    }

}
