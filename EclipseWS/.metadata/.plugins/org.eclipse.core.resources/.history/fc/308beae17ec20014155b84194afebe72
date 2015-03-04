package org.indiarose.net;

import java.util.List;

import org.codegist.crest.annotate.Consumes;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.PUT;
import org.codegist.crest.annotate.Path;
import org.indiarose.lib.ActionLog;

@EndPoint("http://orleans.miage.fr/elasticsearch")

public interface LoggerService {

	@PUT
	@Path("/logs/log/")
	@Consumes("application/json")
	public String envoyer(List<ActionLog> logs);
	
}
