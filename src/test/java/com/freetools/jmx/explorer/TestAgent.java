package com.freetools.jmx.explorer;

/**
 * Date: 1/18/14
 *
 * @author Dima Rassin
 */
public class TestAgent {
	public static void main(String[] args) throws InterruptedException {
		new ExplorerAgent().register("127.0.0.1:5701");
		Thread.currentThread().join();
	}
}
