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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import javax.xml.bind.DatatypeConverter;
/**
 * Implements the output of Bonaparte objects into Excel formats.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 */

public class ExcelComposer extends BaseExcelComposer implements ExcelWriter {
    
    public ExcelComposer() {
        super(new HSSFWorkbook());
    }

    /** Write the current state of the Workbook onto a stream. */
    public void write(OutputStream os) throws IOException {
        xls.write(os);
    }

    public void writeToFile(String filename) throws IOException {
        FileOutputStream out = new FileOutputStream(filename);
        write(out);
        out.close();
    }

}
