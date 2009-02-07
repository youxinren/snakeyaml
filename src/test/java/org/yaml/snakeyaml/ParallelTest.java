/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Constructor;

/**
 * Test that Yaml instances are independent and can be used in multiple threads.
 */
public class ParallelTest extends TestCase {
    private int progress = 0;
    private int MAX = 5;

    public void testPerfomance() throws IOException {
        String doc = Util.getLocalResource("specification/example2_27.yaml");
        for (int i = 0; i < MAX; i++) {
            Worker worker = new Worker(i, doc);
            Thread thread = new Thread(worker);
            thread.start();
        }
        while (progress < MAX - 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private class Worker implements Runnable {
        private int id;
        private String doc;

        public Worker(int id, String doc) {
            this.id = id;
            this.doc = doc;
        }

        public void run() {
            System.out.println("Started: " + id);
            Loader loader = new Loader(new Constructor(Invoice.class));
            Yaml yaml = new Yaml(loader);
            long time1 = System.currentTimeMillis();
            int cycles = 200;
            for (int i = 0; i < cycles; i++) {
                Invoice invoice = (Invoice) yaml.load(doc);
                assertNotNull(invoice);
            }
            long time2 = System.currentTimeMillis();
            float duration = (time2 - time1) / (float) cycles;
            System.out.println("Duration of " + id + " was " + duration + " ms/load.");
            progress++;
        }

    }
}