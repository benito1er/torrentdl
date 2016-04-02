package net.myonlinestuff.torrentdl;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import net.myonlinestuff.torrentdl.endpoint.MainEndpoint;

@Component
public class RestConfig extends ResourceConfig {

	public RestConfig() {
		register(MainEndpoint.class);
	}

	
}
