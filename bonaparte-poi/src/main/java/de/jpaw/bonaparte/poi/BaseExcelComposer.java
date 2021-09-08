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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.AbstractMessageComposer;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.XEnum;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.IntegralLimits;

// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import jakarta.xml.bind.DatatypeConverter;
/**
 * Implements the output of Bonaparte objects into Excel formats.
 *
 * For a description of the codes MS Excel wants, please see here:
 * http://office.microsoft.com/assistance/hfws.aspx?AssetID=HA010346351033#BMnumeralshape
 *
 * @author Michael Bischoff
 * @version $Revision$
 */

public class BaseExcelComposer extends AbstractMessageComposer<RuntimeException> implements ExcelWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseExcelComposer.class);

    protected static final int MAX_DECIMALS = 18;
    protected static final LocalDate EXCEL_EPOCH = LocalDate.of(1900, 1, 1);

    protected final Workbook xls;
    protected final DataFormat xlsDataFormat;
    protected final CellStyle csLong;
    protected final CellStyle [] csBigDecimal;  // one per number of decimals, cache
    protected final CellStyle csDay;
    protected final CellStyle csTime;
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
        csDay.setDataFormat(xlsDataFormat.getFormat("yyyy-mm-dd"));
        csTime = xls.createCellStyle();
        csTime.setDataFormat(xlsDataFormat.getFormat("hh:mm:ss"));
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

    /** Write the current state of the Workbook onto a stream. */
    @Override
    public void write(OutputStream os) throws IOException {
        xls.write(os);
    }

    @Override
    public void writeToFile(String filename) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            write(out);
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte [] result = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(50000)) {
            write(out);
            out.flush();
            result = out.toByteArray();
        }
        return result;
    }

    protected void setFieldWidth(FieldDefinition di) {
        int gap = 3;        // addon for graphical reasons (border dist)
        int width = 8;  // this is the xls default
        switch (di.getDataCategory()) {
        case BASICNUMERIC:
        case NUMERIC:
            BasicNumericElementaryDataItem ni = (BasicNumericElementaryDataItem)di;
            width = ni.getTotalDigits() + (ni.getIsSigned() ? 1 : 0) + (ni.getDecimalDigits() > 0 ? 1 : 0);  // allow 1 for sign and decimal point)
            break;
        case BINARY:
            break;
        case ENUM:
            width = 3;      // small number
            break;
        case ENUMALPHA:
            width = ((XEnumDataItem)di).getBaseXEnum().getBaseEnum().getMaxTokenLength();
            break;
        case ENUMSET:
            break;
        case ENUMSETALPHA:
            break;
        case MISC:
            break;
        case OBJECT:
            break;
        case STRING:
            int len = ((AlphanumericElementaryDataItem)di).getLength();
            width = len > 32 ? 32 : len;
            break;
        case TEMPORAL:
            width = 20; // 10 for date, 8 for time
            break;
        case XENUM:
            width = ((XEnumDataItem)di).getBaseXEnum().getBaseEnum().getMaxTokenLength();
            break;
        case XENUMSET:
            break;
        default:
            break;
        }
        LOGGER.debug("Setting width of column {} ({}); to {}", column, di.getName(), width);
        sheet.setColumnWidth(column, (width + gap) * 256);
    }

    /**************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/

//    protected void writeNull() {
//        ++column;   // no output for empty cells, but ensure that everything goes nicely into the correct column
//    }

    @Override
    public void writeNull(FieldDefinition di) {
        ++column;   // no output for empty cells, but ensure that everything goes nicely into the correct column
        if (rownum == 0)
            setFieldWidth(di);
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

    private Cell newCell(FieldDefinition di) {
        ++column;
        if (rownum == 0)
            setFieldWidth(di);
        return row.createCell(column);
    }

    // create a new cell and apply an existng cell style to it
    private Cell newCell(FieldDefinition di, CellStyle cs) {
        Cell cell = newCell(di);
        if (cs != null)
            cell.setCellStyle(cs);
        return cell;
    }

    // field type specific output functions

    // character
    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        newCell(di).setCellValue(String.valueOf(c));
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        if (s != null)
            newCell(di).setCellValue(s);
        else
            writeNull(di);
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        if (n != null) {
            newCell(di, getCachedCellStyle(n.scale())).setCellValue(n.doubleValue());
        } else {
            writeNull(di);
        }
    }

	@Override
	public <F extends FixedPointBase<F>> void addField(BasicNumericElementaryDataItem di, F n) throws RuntimeException {
        if (n != null) {
            newCell(di, getCachedCellStyle(n.scale())).setCellValue(n.doubleValue());
        } else {
            writeNull(di);
        }
	}

    // output a non-null number which was stored with possibly implicit fixed point
    private void addScaledNumber(BasicNumericElementaryDataItem di, double n) {
        int fractionalDigits = di.getDecimalDigits();
        if (fractionalDigits > 0)
            newCell(di, getCachedCellStyle(fractionalDigits)).setCellValue(n * IntegralLimits.IMPLICIT_SCALES[fractionalDigits]);
        else
            newCell(di, csLong).setCellValue(n);
    }

    // byte
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        addScaledNumber(di, n);
    }
    // short
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        addScaledNumber(di, n);
    }
    // integer
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        addScaledNumber(di, n);
    }

    // int(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) {
        if (n != null) {
            newCell(di).setCellValue(n.doubleValue());
        } else {
            writeNull(di);
        }
    }

    // long
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        addScaledNumber(di, n);
    }

    // boolean
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        newCell(di).setCellValue(b);
    }

    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        newCell(di).setCellValue(f);
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        newCell(di).setCellValue(d);
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        if (n != null) {
            newCell(di).setCellValue(n.toString());
        } else {
            writeNull(di);
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length() * 2) + 4, null);
            Base64.encodeToByte(tmp, b.getBytes(), 0, b.length());
            newCell(di).setCellValue(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        } else {
            writeNull(di);
        }
    }

    // raw
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length * 2) + 4, null);
            Base64.encodeToByte(tmp, b, 0, b.length);
            newCell(di).setCellValue(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        } else {
            writeNull(di);
        }
    }

    // converters for DAY and TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        if (t != null) {
            newCell(di, csDay).setCellValue(t);
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        if (t != null) {
            newCell(di, csTimestamp).setCellValue(t);
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
        if (t != null) {
            newCell(di, csTime).setCellValue(t.atDate(EXCEL_EPOCH));
        } else {
            writeNull(di);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
        if (t != null) {
            newCell(di, csTimestamp).setCellValue(Date.from(t));
        } else {
            writeNull(di);
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) {
    }

    @Override
    public void terminateArray() {
    }

    @Override
    public void terminateMap() {
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) {
    }

    /** Adding objects will lead to column misalignment if the objects itself are null. */
    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        if (obj != null) {
            obj.serializeSub(this);  // no start and stop for now...
        }
    }
    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) {
        if (n == null)
            writeNull(ord);
        else
            addField(ord, n.ordinal());
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) {
        return false;       // perform conversion by default
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) {
        if (obj == null)
            writeNull(di);
        else
            newCell(di).setCellValue(BonaparteJsonEscaper.asJson(obj));
    }

    @Override
    public void addField(ObjectReference di, List<Object> obj) throws RuntimeException {
        if (obj == null)
            writeNull(di);
        else
            newCell(di).setCellValue(BonaparteJsonEscaper.asJson(obj));
    }

    @Override
    public void addField(ObjectReference di, Object obj) {
        if (obj == null)
            writeNull(di);
        else
            newCell(di).setCellValue(BonaparteJsonEscaper.asJson(obj));
    }
}
