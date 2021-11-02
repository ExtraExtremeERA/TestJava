package io.denery.util;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class ServerAgent {
    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        //hellish Shitty args parser cuz I am lazy to find how to do String array in agentmain args.
        final char[] ch = agentArgs.toCharArray();
        boolean toggle = false;
        int cftNameLength = 0;
        int classNameLength = 0;
        for (char c : ch) {
            if (c == '-') {toggle ^= true; continue;}
            if (toggle) {
                cftNameLength++;
            } else {
                classNameLength++;
            }
        }
        char[] cftNameCharArr = new char[cftNameLength];
        char[] classNameCharArr = new char[classNameLength];
        boolean toggle1 = false;
        for (int i = 0; i < ch.length; i++) {
            final char c = ch[i];
            if (c == '-') {toggle1 ^= true; continue;}
            if (toggle1) {
                cftNameCharArr[i] = c;
            } else {
                classNameCharArr[i] = c;
            }
        }

        String classFileTransformerName = String.valueOf(cftNameCharArr);
        ClassFileTransformer cft = (ClassFileTransformer) createObject(Class.forName(classFileTransformerName)
                .getDeclaredConstructor(Class.class, Class.class), );
        String className = String.valueOf(classNameCharArr);
        Class<?> targetCls = Class.forName(className);

        ModificationUtils.transform(targetCls, cft, inst);
    }

    public static Object createObject(Constructor<?> constructor, Object... arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create object from name!", e);
        }
    }
}
