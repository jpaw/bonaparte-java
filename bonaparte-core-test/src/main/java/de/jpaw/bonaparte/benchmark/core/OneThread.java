package de.jpaw.bonaparte.benchmark.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import com.google.gson.Gson;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.util.ByteArray;

public class OneThread implements Runnable {
    private final int method;
    private final int callsPerThread;
    private final int initialBufferSize;
    private Date start;
    private Date stop;
    private String methodName = "?";
    private BonaPortable src;
    private byte [] srcdata;
    private byte [] srcExternalized;
    private byte [] srcCompact;
    private String gsondata;
    private Class<? extends BonaPortable> srcClass;  // this is required by Gson

    OneThread(BonaPortable src, int method, int millionCallsPerThread, int initialBufferSize) {
        this.src = src;
        this.method = method;
        this.callsPerThread = millionCallsPerThread * 1000000;
        this.initialBufferSize = initialBufferSize;
    }

    private void createSources() throws IOException {
        // create serialized Bonaparte object
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder(initialBufferSize));
        sbc.writeRecord(src);
        srcdata = sbc.getBytes();

        // create serialized object
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        ObjectOutputStream o = new ObjectOutputStream(fos);
        o.writeObject(src);
        o.flush();
        o.close();
        srcExternalized = fos.toByteArray();

        // create serialized object (compact)
        fos = new ByteArrayOutputStream(1000);
        DataOutputStream o2 = new DataOutputStream(fos);
        CompactComposer cc = new CompactComposer(o2, false);
        cc.writeRecord(src);
        o2.flush();
        o2.close();
        srcCompact = fos.toByteArray();

        // create serialized JSON object for Gson
        Gson gson = new Gson();
        gsondata = gson.toJson(src);
        srcClass = src.getClass();
    }

    // StringBuilder
    private void sbc(boolean retrieveBytes) {
        methodName = "Bonaparte StringBuilder Composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        StringBuilderComposer sbc = new StringBuilderComposer(new StringBuilder(initialBufferSize));
        for (int i = 0; i < callsPerThread; ++i) {
            sbc.reset();
            sbc.writeRecord(src);
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte [] sbcResult = sbc.getBytes();
            }
        }
    }

    private void sbp() throws MessageParserException {
        methodName = "Bonaparte StringBuilder Parser";
        for (int i = 0; i < callsPerThread; ++i) {
            StringBuilder work = new StringBuilder(new String(srcdata, ByteArray.CHARSET_UTF8));
            MessageParser<MessageParserException> w1 = new StringBuilderParser(work, 0, -1);
            @SuppressWarnings("unused")
            BonaPortable dst1 = w1.readRecord();
        }
    }

    // ByteArray
    private void bac(boolean retrieveBytes) {
        methodName = "Bonaparte ByteArray Composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        ByteArrayComposer bac = new ByteArrayComposer();
        for (int i = 0; i < callsPerThread; ++i) {
            bac.reset();
            bac.writeRecord(src);
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte [] bacResult = bac.getBytes();
            }
        }
    }

    // ByteArray
    private void coc(boolean retrieveBytes) throws IOException {
        methodName = "Bonaparte Compact Composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        DataOutputStream o2 = new DataOutputStream(fos);
        CompactComposer cc = new CompactComposer(o2, false);
        for (int i = 0; i < callsPerThread; ++i) {
            cc.reset();
            cc.writeRecord(src);
            o2.flush();
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte [] bacResult = fos.toByteArray();
            }
            fos.reset();
        }
    }

    // ByteArray
    private void cocId(boolean retrieveBytes) throws IOException {
        methodName = "Bonaparte Compact Composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        DataOutputStream o2 = new DataOutputStream(fos);
        CompactComposer cc = new CompactComposer(o2, true);
        for (int i = 0; i < callsPerThread; ++i) {
            cc.reset();
            cc.writeRecord(src);
            o2.flush();
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte [] bacResult = fos.toByteArray();
            }
            fos.reset();
        }
    }

    private void bap() throws MessageParserException {
        methodName = "Bonaparte ByteArray Parser";
        for (int i = 0; i < callsPerThread; ++i) {
            MessageParser<MessageParserException> w2 = new ByteArrayParser(srcdata, 0, -1);
            @SuppressWarnings("unused")
            BonaPortable dst2 = w2.readRecord();
        }
    }

    // Externalizer
    private void extc(boolean retrieveBytes) throws IOException {
        methodName = "Bonaparte Externalizer Composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        for (int i = 0; i < callsPerThread; ++i) {
            ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(src);
            o.close();
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte[] extcResult = fos.toByteArray();
            }
        }
    }

    private void extp() throws MessageParserException, IOException, ClassNotFoundException {
        methodName = "Bonaparte Externalizer Parser";
        for (int i = 0; i < callsPerThread; ++i) {
            ByteArrayInputStream fis = new ByteArrayInputStream(srcExternalized);
            ObjectInputStream in = new ObjectInputStream(fis);
            @SuppressWarnings("unused")
            Object xdst = in.readObject();
        }
    }

    // Gson
    private void toGson(boolean retrieveBytes) {
        methodName = "Gson composer" + (retrieveBytes ? " with byte[] retrieval" : "");
        Gson gson = new Gson();
        for (int i = 0; i < callsPerThread; ++i) {
            String result = gson.toJson(src);
            if (retrieveBytes) {
                @SuppressWarnings("unused")
                byte [] bacResult = result.getBytes();
            }
        }
    }

    private void fromGson() {
        methodName = "Gson Parser";
        Gson gson = new Gson();
        for (int i = 0; i < callsPerThread; ++i) {
            @SuppressWarnings("unused")
            BonaPortable dst = gson.fromJson(gsondata, srcClass); // BonaPortable.class results in an exception
        }
    }



    @Override
    public void run() {
        try {
            createSources();
            start = new Date();

            switch (method) {
            // 0 .. 2 Bonaparte StringBuilder
            case 0:
                sbc(false);
                break;
            case 1:
                sbc(true);
                break;
            case 2:
                sbp();
                break;
            // 10..12 Bonaparte ByteArray
            case 10:
                bac(false);
                break;
            case 11:
                bac(true);
                break;
            case 12:
                bap();
                break;
            // 20..22 Bonaparte Externalizer
            case 20:
                extc(false);
                break;
            case 21:
                extc(true);
                break;
            case 22:
                extp();
                break;
            // 30..32 Bonaparte compact
            case 30:
                coc(false);
                break;
            case 31:
                coc(true);
                break;
            case 32:
                // cop();
                break;
            // 40..42 Bonaparte compact with Ids instead of class names
            case 40:
                cocId(false);
                break;
            case 41:
                cocId(true);
                break;
            case 42:
                // cop();
                break;
            // 100 .. 102 Gson (String)
            case 100:
                toGson(false);
                break;
            case 101:
                toGson(true);
                break;
            case 102:
                fromGson();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + methodName + " did not finish");
            return;
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerMilliSecond = callsPerThread / millis;
        System.out.println("Thread result: "
                + (int)callsPerMilliSecond + " k calls / second for " + methodName
                + " (= " + callsPerThread + " in " + millis + " milliseconds)");
    }
}
