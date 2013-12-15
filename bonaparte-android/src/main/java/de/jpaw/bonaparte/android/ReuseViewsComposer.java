package de.jpaw.bonaparte.android;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReuseViewsComposer extends LinearLayoutComposer {

    protected LinearLayout rowWidget;
    protected int initialChilds = 0;
    protected int column = 0;

    public ReuseViewsComposer() {
        this.rowWidget = null;
        column = -1;
    }
   
    // creates a new composer, with support of widget auto-creation
    
    public void newView(final LinearLayout rowWidget) {
        this.rowWidget = rowWidget;
        initialChilds = rowWidget.getChildCount();
        column = -1;
    }


    @Override
    CheckBox needCheckBox(FieldDefinition di) {
        return (CheckBox)rowWidget.getChildAt(++column);
    }


    @Override
    TextView needTextView(FieldDefinition di) {
        return (TextView)rowWidget.getChildAt(++column);
    }


    @Override
    ImageView needImageView(FieldDefinition di) {
        return (ImageView)rowWidget.getChildAt(++column);
    }


    @Override
    View needAny(FieldDefinition di) {
        View v = rowWidget.getChildAt(++column);
        // clear it!
        if (v instanceof TextView)
            ((TextView)v).setText("");
        else if (v instanceof CheckBox)
            ((CheckBox)v).setChecked(false);
        else if (v instanceof ImageView)
            ((ImageView)v).setImageResource(0); // undocumented but working according to http://stackoverflow.com/questions/2859212/how-to-clear-an-imageview-in-android
        return v;
    }

}
