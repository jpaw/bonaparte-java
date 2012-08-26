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

import de.jpaw.bonaparte.pojos.tests1.PrimitiveArrays;

public class FillPrimitiveArrays {

	static public PrimitiveArrays test1() {
		PrimitiveArrays x = new PrimitiveArrays();
		
		byte [] bytea = new byte [950];
		Arrays.fill(bytea, (byte)'X');
		bytea[666] = (byte)66;
		x.setByte1(bytea);
		
		short [] shorta = new short [195];
		Arrays.fill(shorta, (short)4242);
		shorta[88] = 1354;
		x.setShort1(shorta);
		
		boolean [] booleana = new boolean[1001];
		for (int i = 0; i < 1001; ++i) booleana[i] = i%1 == 0;
		x.setBoolean1(booleana);
		
		char [] chara = new char [333];
		Arrays.fill(chara, (char)'Ä');
		chara[88] = '€';
		x.setChar1(chara);
		
		char [] charb = new char [55];
		Arrays.fill(charb, (char)'\n');
		charb[12] = '\r';
		x.setChar2(charb);
		
		double [] dbl = new double [888];
		dbl[0] = 3.14159;
		for (int i = 1; i < 888; ++i) dbl[i] = 3 * dbl[i-1] * 0.5;
		x.setDouble1(dbl);
		
		float [] flt = new float [188];
		flt[0] = 2.71828f;
		for (int i = 1; i < 188; ++i) flt[i] = 3.0f * flt[i-1] * 0.5f;
		x.setFloat1(flt);
		
		int [] inta = new int [8765];
		Arrays.fill(inta, 42424242);
		inta[8687] = 2424242;
		x.setInt1(inta);
		
		int [] intb = new int [33];
		Arrays.fill(intb, 142424242);
		intb[12] = 82424242;
		x.setInt2(intb);
		
		long [] longa = new long [66];
		Arrays.fill(longa, 4242424242424242L);
		longa[44] = 3333332424242L;
		x.setLong1(longa);
		return x;
	}
}
