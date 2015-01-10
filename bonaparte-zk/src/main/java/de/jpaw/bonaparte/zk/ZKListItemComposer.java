package de.jpaw.bonaparte.zk;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import de.jpaw.bonaparte.core.AbstractUIComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;

/** Emits the fields into a ListItem row.
 * This composer is normally the delegate composer of a FoldableComposer, which selects the columns to be displayed.
 * 
 *  Essential fields of the CSVConfiguration include TimeZone, Locale, boolean true/false texts.
 *   */
public class ZKListItemComposer extends AbstractUIComposer {
    public final static CSVConfiguration CSV_ZK_DEFAULT_CONFIGURATION = CSVConfiguration.CSV_DEFAULT_CONFIGURATION.builder().usingGrouping(true).build();

    protected Listitem renderTarget = null;
    
    public ZKListItemComposer(CSVConfiguration cfg) {
        super(cfg); 
    }
    public ZKListItemComposer() {
        super();
    }
    
    public void setRenderTarget(Listitem renderTarget) {
        this.renderTarget = renderTarget;
    }
    
    @Override
    protected void emit(String contents) {
        new Listcell(contents == null ? "" : contents).setParent(renderTarget);
    }
}
