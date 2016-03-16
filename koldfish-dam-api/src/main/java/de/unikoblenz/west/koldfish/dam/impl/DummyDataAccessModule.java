/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.jena.iri.IRI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefResponse;

/**
 * This is a dummy implementation for the DataAccessModule interface. Can be
 * configured to create random outputs or does nothing.
 * 
 * @author lkastler
 */
public class DummyDataAccessModule implements DataAccessModule {

	private static final Logger log = LogManager
			.getLogger(DummyDataAccessModule.class);

	private final Random random;

	private Thread t;

	private volatile boolean running = false;
	
	private DataAccessModuleListener listener;

	public DummyDataAccessModule() {
		this(null);
	}

	public DummyDataAccessModule(Random random) {
		this.random = random;
		log.debug("created");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unikoblenz.west.koldfish.dam.LifeCycle#isStarted()
	 */
	@Override
	public boolean isStarted() {
		return running;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(org.apache.jena
	 * .iri.IRI)
	 */
	@Override
	public void deref(IRI iri) throws DataAccessModuleException {
		if(!running) {
			throw new DataAccessModuleException("DataAccessModule is not running!");
		}
		log.debug("deref: " + iri.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikoblenz.west.koldfish.dam.DataAccessModule#addListener(de.unikoblenz
	 * .west.koldfish.dam.DataAccessModuleListener)
	 */
	@Override
	public void addListener(DataAccessModuleListener listener) {
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unikoblenz.west.koldfish.dam.LifeCycle#start()
	 */
	@Override
	public void start() throws Exception {
		if (random != null) {
			t = new Thread(createRandomRunnable(random));
		} else {
			t = new Thread(createNoopRunnable());
		}

		t.start();
		
		running = true;
		log.debug("start");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		running = false;
		
		if(t != null) {
			t.interrupt();
			
			log.debug("close");
			t.join();
		}
	}

	/**
	 * creates a dummy runnable that creates random data in random time.
	 * @param random - random number generator for all things random.
	 * @return a dummy runnable that creates random data in random time.
	 */
	private Runnable createRandomRunnable(Random random) {
		return new Runnable() {
			@Override
			public void run() {
				log.debug("started");
				while (!t.isInterrupted() && running) {
					try {
						int size = random.nextInt(100) + 30;

						List<long[]> data = new LinkedList<long[]>();

						for (int i = 0; i < size; ++i) {
							data.add(new long[] { 
								nextLong(random, Long.MAX_VALUE),
								nextLong(random, Long.MAX_VALUE),
								nextLong(random, Long.MAX_VALUE)
							});
						}

						DerefResponse res = new DerefResponseImpl(data);
						
						log.debug("send: " + res);
						
						listener.derefResponse(res);
					
						Thread.sleep((random.nextInt(10) + 1) * 100);
					} catch (InterruptedException e) {
						log.debug(e);
					}
				}
				log.debug("finished");
			}

		};
	}
	
	/**
	 * creates a no operation dummy runnable.
	 * @return a no operation dummy runnable.
	 */
	private Runnable createNoopRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				log.debug("started");
				while (!t.isInterrupted() && running) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						log.debug(e);
					}
				}
				log.debug("finished");
			}

		};
	}
	
	/**
	 * generates a random Long value within the given range 0 to n-1.
	 * @param rng - Random number generator.
	 * @param n - upper boundary for the generated long.
	 * @return a random long between 0 and n-1.
	 */
	private long nextLong(Random rng, long n) {
		// see also: http://stackoverflow.com/a/2546186
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	@Override
	public String toString() {
		return  "DummyDataAccessModule [running=" + running + ", mode=" + ((random != null) ? "random" : "no-op") + "]";
	}
	
	
}
