package io.denery;

import io.denery.sampleClasses.Bebra;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[Agent] In main method");
        System.out.println();
        Bebra bebra = new Bebra();
        System.out.println("Second execution: " + bebra.getAbobification() + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        Thread.sleep(20000);
        //traceClass("io.denery.sampleClasses.Bebra");
    }
}
