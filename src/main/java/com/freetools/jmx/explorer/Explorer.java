package com.freetools.jmx.explorer;

import com.freetools.jmx.explorer.entities.JmxCommand;
import com.freetools.jmx.explorer.entities.JmxNode;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Date: 1/17/14
 *
 * @author Dima Rassin
 */
public class Explorer {
	private HazelcastInstance hc;
	private Lock lock = new ReentrantLock();
	private Condition gotResult = lock.newCondition();
	private long timeout = 5*1000L;


	public void init(){
		hc = Hazelcast.newHazelcastInstance();
		hc.<String,JmxCommand>getMap("results").addEntryListener(new EntryAdapter<String,JmxCommand>(){
			@Override
			public void entryAdded(EntryEvent<String, JmxCommand> event) {
				gotResult.signalAll();
			}
		}, false);
	}

	public Collection<JmxNode> getNodes(){
		return Collections.unmodifiableCollection(hc.<String,JmxNode>getMap("topology").values());
	}

	public JmxCommand execute(String nodeId, JmxCommand command){
		if (nodeId == null) throw new IllegalArgumentException("Node ID is null");
		if (command == null) throw new IllegalArgumentException("Command is null");
		if (!hc.getMap("topology").containsKey(nodeId)) throw new IllegalArgumentException("Unknown node ID: [" + nodeId + "]");

		hc.getTopic(nodeId).publish(command);

		lock.lock();
		JmxCommand result = null;
		try{
			long t = System.currentTimeMillis();
			while ((result = hc.<String,JmxCommand>getMap("results").remove(command.getId())) == null){
				gotResult.await(100, TimeUnit.MILLISECONDS);
				if (System.currentTimeMillis()-t > timeout) throw new RuntimeException("Command failed on timeout");
			}
		} catch(InterruptedException ex){
			// do nothing
		} finally {
			lock.unlock();
		}
		return result;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
