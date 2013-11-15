/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.bonaparte.poi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;

// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import javax.xml.bind.DatatypeConverter;
/**
 * Implements the output of Bonaparte objects into Excel formats.
 *
 * For a description of the codes MS Excel wants, please see here:
 * http://office.microsoft.com/assistance/hfws.aspx?AssetID=HA010346351033#BMnumeralshape
 *
 * @author Michael Bischoff
 * @version $Revision$
 */

public class BaseExcelComposer implements MessageComposer<RuntimeException> {
    protected static final int MAX_DECIMALS = 18;
    protected final Workbook xls;
    protected final DataFormat xlsDataFormat;
    protected final CellStyle csLong;
    protected final CellStyle [] csBigDecimal;  // one per number of decimals, cache
    protected final CellStyle csDay;
    protected final CellStyle csTimestamp;
    private final String [] BIGDECIMAL_FORMATS = {
            "#0",
            "#0.#",
            "#0.##",
            "#0.###",
            "#0.####",
            "#0.#####",
            "#0.######",
            "#0.#######",
            "#0.########",
            "#0.#########",
            "#0.##########",
            "#0.###########",
            "#0.############",
            "#0.#############",
            "#0.##############",
            "#0.###############",
            "#0.################",
            "#0.#################",
            "#0.##################"
    };
    private Sheet sheet = null;
    private Row row;
    private int rownum = -1;
    private int column = 0;
    private int sheetNum = 0;

    public BaseExcelComposer(Workbook xls) {
        this.xls = xls;
        // create a few data formats
        xlsDataFormat = xls.createDataFormat();
        csLong = xls.createCellStyle();
        csLong.setDataFormat(xlsDataFormat.getFormat("#,###,###,###,###,###,###,###,###,##0"));
        csBigDecimal = new CellStyle[1 + MAX_DECIMALS];
        csDay = xls.createCellStyle();
        csDay.setDataFormat(xlsDataFormat.getFormat(DateFormatConverter.convert(Locale.JAPANESE, "yyyy mm dd")));
        csTimestamp = xls.createCellStyle();
        csTimestamp.setDataFormat(xlsDataFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
    }

    public void newSheet(String name) {
        sheet = xls.createSheet();
        xls.setSheetName(sheetNum, name);
        rownum = -1;
        ++sheetNum;
    }
    public void closeSheet() {
    }

    private CellStyle getCachedCellStyle(int decimals) {
        if (decimals < 0 || decimals > MAX_DECIMALS)
            return null;  // no application format
        if (csBigDecimal[decimals] == null) {
            CellStyle newStyle = xls.createCellStyle();
            newStyle.setDataFormat(xlsDataFormat.getFormat(BIGDECIMAL_FORMATS[decimals]));
            csBigDecimal[decimals] = newStyle;
            return newStyle;
        } else {
            return csBigDecimal[decimals];
        }
    }
    /**************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/

    protected void writeNull() {
        ++column;   // no output for empty cells, but ensure that everything goes nicely into the correct column
    }
    
    @Override
    public void writeNull(FieldDefinition di) {
        ++column;   // no output for empty cells, but ensure that everything goes nicely into the correct column
    }
    
    @Override
    public void writeNullCollection(FieldDefinition di) {
        ++column;   // no output for empty cells, but ensure that everything goes nicely into the correct column
    }

    @Override
    public void startTransmission() {
        newSheet("Sheet " + (sheetNum+1));
    }
    @Override
    public void terminateTransmission() {
        closeSheet();
    }

    @Override
    public void terminateRecord() {
    }

    @Override
    public void writeSuperclassSeparator() {
    }

    @Override
    public void startRecord() {
        ++rownum;
        column = -1;
        row = sheet.createRow(rownum);
    }

    @Override
    public void writeRecord(BonaPortable o) {
        startRecord();
        addField(o);
        terminateRecord();
    }

    private Cell newCell() {
        ++column;
        return row.createCell(column);
    }

    // create a new cell and apply an existng cell style to it
    private Cell newCell(CellStyle cs) {
        Cell cell = newCell();
        if (cs != null)
            cell.setCellStyle(cs);
        return cell;
    }

    private void newStringCell(String s) {
        ++column;
        if (s != null)
            row.createCell(column).setCellValue(s);
    }

    // field type specific output functions

    // character
    @Override
    public void addField(char c) {
        newCell().setCellValue(String.valueOf(c));
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        newStringCell(s);
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        if (n != null) {
            newCell(getCachedCellStyle(n.scale())).setCellValue(n.doubleValue());
        } else {
            writeNull();
        }
    }

    // byte
    @Override
    public void addField(byte n) {
        newCell().setCellValue((double)n);
    }
    // short
    @Override
    public void addField(short n) {
        newCell().setCellValue((double)n);
    }
    // integer
    @Override
    public void addField(int n) {
        newCell().setCellValue((double)n);
    }

    // int(n)
    @Override
    public void addField(NumericElementaryDataItem di, Integer n) {
        if (n != null) {
            newCell().setCellValue(n.doubleValue());
        } else {
            writeNull();
        }
    }

    // long
    @Override
    public void addField(long n) {
        newCell(csLong).setCellValue((double)n);
    }

    // boolean
    @Override
    public void addField(boolean b) {
        newCell().setCellValue(b);
    }

    // float
    @Override
    public void addField(float f) {
        newCell().setCellValue((double)f);
    }

    // double
    @Override
    public void addField(double d) {
        newCell().setCellValue(d);
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        if (n != null) {
            newCell().setCellValue(n.toString());
        } else {
            writeNull();
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length() * 2) + 4, null);
            Base64.encodeToByte(tmp, b.getBytes(), 0, b.length());
            newCell().setCellValue(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        } else {
            writeNull();
        }
    }

    // raw
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length * 2) + 4, null);
            Base64.encodeToByte(tmp, b, 0, b.length);
            newCell().setCellValue(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        } else {
            writeNull();
        }
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) {
        if (t != null) {
            newCell(csTimestamp).setCellValue(t);
        } else {
            writeNull();
        }
    }
    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        if (t != null) {
            newCell(csDay).setCellValue(t.toDate());
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        if (t != null) {
            newCell(csTimestamp).setCellValue(t.toDate());
        } else {
            writeNull();
        }
    }

    @Override
    public void startMap(int currentMembers, int indexID) {
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) {
    }

    @Override
    public void terminateArray() {
    }

    @Override
    public void terminateMap() {
    }

    @Override
    public void startObject(BonaPortable obj) {
    }

    /** Adding objects will lead to column misalignment if the objects itself are null. */
    @Override
    public void addField(BonaPortable obj) {
        if (obj != null) {
            // do all fields (now includes terminator)
            obj.serializeSub(this);
        }
    }
    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, NumericElementaryDataItem ord, Enum<?> n) {
        if (n == null)
            writeNull(ord);
        else
            addField(ord, n.ordinal());
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) {
        addField(token, n.getToken());
    }
}
