package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.Locale;

import java.time.ZoneId;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** text based composer which emits the fields in stringified form into an abstract method.
 * This composer is intended to be used by UI adaptions such as Android or ZK. */
public abstract class AbstractUIComposer extends CSVComposer2 {
    public final static CSVConfiguration UI_DEFAULT_CONFIGURATION = CSVConfiguration.CSV_DEFAULT_CONFIGURATION.builder()
            .usingGrouping(true)
            .forLocale(Locale.getDefault())
            .forTimeZone(ZoneId.systemDefault())
            .build();

    protected final StringBuilder buffer;

    private AbstractUIComposer(StringBuilder buffer, CSVConfiguration cfg) {
        super(buffer, cfg);
        this.buffer = buffer;
    }

    public AbstractUIComposer(CSVConfiguration cfg) {
        this(new StringBuilder(250), cfg);
    }
    public AbstractUIComposer() {
        this(UI_DEFAULT_CONFIGURATION);
    }
    public AbstractUIComposer(Locale locale, ZoneId zone) {
        this(UI_DEFAULT_CONFIGURATION.builder()
                .forLocale(locale == null ? Locale.getDefault() : locale)
                .forTimeZone(zone == null ? ZoneId.systemDefault() : zone)
                .build());
    }

    protected abstract void emit(String text);

    @Override
    protected void writeSeparator() {   // use this as an indicator that a new field has been started
        buffer.setLength(0);
    }
    @Override
    protected void terminateField() {   // a field has been rendered
        emit(buffer.toString());
    }


    @Override
    public void writeNull(FieldDefinition di) throws RuntimeException {
        emit(null);
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws RuntimeException {
    }
    @Override
    public void terminateMap() throws RuntimeException {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws RuntimeException {
    }
    @Override
    public void terminateArray() throws RuntimeException {
    }

    @Override
    public void terminateRecord() throws RuntimeException {
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws RuntimeException {
    }
    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws RuntimeException {
    }

    // field type specific output functions

    // character: No escaping for UI output
    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        if (c < ' ')
            emit("^" + String.valueOf((char)(c + '@')));
        else
            emit(String.valueOf(c));
    }

    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        emit(s);
    }
}
