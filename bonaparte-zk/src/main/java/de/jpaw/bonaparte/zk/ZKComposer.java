package de.jpaw.bonaparte.zk;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;

import de.jpaw.bonaparte.core.AbstractUIComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;

/** Emits the fields into a Listitem or Row or other Component.
 * This composer is normally the delegate composer of a FoldableComposer, which selects the columns to be displayed.
 *
 *  Essential fields of the CSVConfiguration include TimeZone, Locale, boolean true/false texts.
 *   */
public class ZKComposer extends AbstractUIComposer {
    public final static CSVConfiguration CSV_ZK_DEFAULT_CONFIGURATION = CSVConfiguration.CSV_DEFAULT_CONFIGURATION.builder().usingGrouping(true).build();

    protected Component renderTarget = null;

    public ZKComposer(CSVConfiguration cfg) {
        super(cfg);
    }
    public ZKComposer() {
        super();
    }

    public void setRenderTarget(Component renderTarget) {
        this.renderTarget = renderTarget;
    }

    @Override
    protected void emit(String contents) {
        new Listcell(contents == null ? "" : contents).setParent(renderTarget);
    }

    // to display a checkbox is UI specifc. Prefer the box in favor of a translated Y / N
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        Listcell lc = new Listcell();
        Checkbox cb = new Checkbox();
        cb.setParent(lc);
        cb.setChecked(b);
        lc.setParent(renderTarget);
    }
}
