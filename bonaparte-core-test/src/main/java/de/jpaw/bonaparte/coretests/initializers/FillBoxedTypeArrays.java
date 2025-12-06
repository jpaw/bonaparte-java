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

package de.jpaw.bonaparte.coretests.initializers;

import java.util.Arrays;

import de.jpaw.bonaparte.pojos.tests1.BoxedTypeArrays;

public class FillBoxedTypeArrays {

    static public BoxedTypeArrays test1() {
        BoxedTypeArrays x = new BoxedTypeArrays();

        Byte [] bytea = new Byte [950];
        Arrays.fill(bytea, new Byte((byte)'X'));
        bytea[666] = (byte)66;
        x.setByte1(bytea);

        Short [] shorta = new Short [195];
        Arrays.fill(shorta, new Short((short)4242));
        shorta[88] = 1354;
        x.setShort1(shorta);

        Boolean [] booleana = new Boolean[1001];
        for (int i = 0; i < 1001; ++i) booleana[i] = Boolean.valueOf((i & 1) == 0);
        x.setBoolean1(booleana);

        Character [] chara = new Character [333];
        Arrays.fill(chara, new Character('Ä'));
        chara[88] = '€';
        x.setChar1(chara);

        Character [] charb = new Character [55];
        Arrays.fill(charb, new Character('\n'));
        charb[12] = '\r';
        x.setChar2(charb);

        Double [] dbl = new Double [888];
        dbl[0] = 3.14159;
        for (int i = 1; i < 888; ++i) dbl[i] = 3 * dbl[i-1] * 0.5;
        x.setDouble1(dbl);

        Float [] flt = new Float [188];
        flt[0] = 2.71828f;
        for (int i = 1; i < 188; ++i) flt[i] = 3.0f * flt[i-1] * 0.5f;
        x.setFloat1(flt);

        Integer [] inta = new Integer [8765];
        Arrays.fill(inta, 42424242);
        inta[8687] = 2424242;
        x.setInt1(inta);

        Integer [] intb = new Integer [33];
        Arrays.fill(intb, 142424242);
        intb[12] = 82424242;
        x.setInt2(intb);

        Long [] longa = new Long [66];
        Arrays.fill(longa, 4242424242424242L);
        longa[44] = 3333332424242L;
        x.setLong1(longa);
        return x;
    }
}
