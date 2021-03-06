/**
 * Copyright 2011 Ioannis Polyzos
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.camel.component.hazelcast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.ObjectHelper;

import com.hazelcast.core.Hazelcast;

/**
 * Hazelcast Component :  implementation of a work queue based on
 * <a href="http://www.hazelcast.com">HazelCast</a> in-memory
 * data grid.
 */
public class HazelcastComponent extends DefaultComponent {

	private final transient Map<String, BlockingQueue> queues = new HashMap<String, BlockingQueue>();

	public HazelcastComponent() {
		super();
	}

	public HazelcastComponent(final CamelContext context) {
		super(context);
	}

	@Override
	protected Endpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) throws Exception {
		final HazelcastConfiguration config = new HazelcastConfiguration();
		setProperties(config, parameters);

		if(ObjectHelper.isEmpty(remaining)){
			throw new IllegalArgumentException("Queue name is missing.");
		}

		config.setQueueName(remaining);

		return new HazelcastEndpoint(uri, this, createQueue(config,parameters), config);
	}

	public synchronized BlockingQueue createQueue(final HazelcastConfiguration config, final Map<String, Object> parameters) {
		final String qName= config.getQueueName();

		if (queues.containsKey(qName)){
			return queues.get(qName);
		}

		final BlockingQueue<Serializable> queue = Hazelcast.getQueue(qName);
		queues.put(qName, queue);
		return queue;
	}

}
