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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Implements the output of Bonaparte objects into Excel xlsx format, using streaming (disk swapping).
 *
 * @author Michael Bischoff
 * @version $Revision$
 */

public class ExcelSXComposer extends BaseExcelComposer implements ExcelWriter {

    public ExcelSXComposer() {
        super(new SXSSFWorkbook());
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
        } finally {
            ((SXSSFWorkbook)xls).dispose();
        }
    }
}
