package org.indiarose.net;

import org.codegist.crest.annotate.Consumes;
import org.codegist.crest.annotate.EndPoint;
import org.codegist.crest.annotate.FormParam;
import org.codegist.crest.annotate.POST;
import org.codegist.crest.annotate.Path;
import org.codegist.crest.annotate.PathParam;
import org.codegist.crest.annotate.Produces;

//@EndPoint("http://orleans.miage.fr/indiarose/rest")
@EndPoint("http://julienmialon.com/india")
public interface LoggerService {

	@POST
	@Path("/logs/{email}")
	@Produces("application/json")
	public String envoyer(@PathParam("email") String email, @FormParam("data") String logs);
	
}
