package io.denery;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

class ClassTest {

    @Test
    void test() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> cftc = Class.forName("io.denery.transformers.CompareMethodsAndTransform")
                .getDeclaredConstructor(Class.class, Class.class);
        ClassFileTransformer cft = (ClassFileTransformer) createObject(cftc, Integer.class, Byte.class);
        System.out.println(cft);
    }

    public static Object createObject(Constructor<?> constructor,
                                      Object... arguments) {
        Object object = null;
        try {
            object = constructor.newInstance(arguments);
            return object;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create object from its name!", e);
        }
    }
}
