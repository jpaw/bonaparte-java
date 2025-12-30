package de.jpaw.bonaparte.poi.tests;

import java.io.IOException;

import java.time.Instant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.jpaw.bonaparte.poi.ExcelComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

public class TestPoiMeta {

    public static void main(String[] args) throws IOException {
        ClassDefinition cd = new ClassDefinition(TestPoiMeta.class, true, false, "myName", null, "bundle",  Instant.now(), null, null, null,
                "Rev", 87264821612983L, 0, ImmutableList.<FieldDefinition>of(), ImmutableMap.<String,String>of(), false, 42, 13, 13, false, false);

        try (ExcelComposer ec = new ExcelComposer()) {
            ec.newSheet("Tabelle Nummer 1");
            ec.writeRecord(cd);
            ec.closeSheet();

            ec.startTransmission();
            ec.writeRecord(cd.ret$BonaPortableClass().getMetaData());
            ec.closeSheet();

            ec.writeToFile("test.xls");
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
