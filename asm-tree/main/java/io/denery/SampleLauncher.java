package io.denery;

import com.sun.tools.attach.VirtualMachine;
import io.denery.util.TransformUtil;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.util.Optional;

public class SampleLauncher {
    public static void main(String[] args) throws URISyntaxException {
        new SampleAgent().run(args);
        run(args);
    }

    public static void run(String[] args) {
        SampleClass sc = new SampleClass();
        System.out.println("Second execution: " + sc.compareTo(new SampleClass()) + " <<<<<<<<<<<<<<<<<<<<<<<<<\n\n");
    }
}
