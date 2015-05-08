package net.linkedbuildingdata.rdf.modelfactory.stardog;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.clarkparsia.stardog.StardogDBMS;
import com.clarkparsia.stardog.api.Connection;
import com.clarkparsia.stardog.api.ConnectionConfiguration;
import com.clarkparsia.stardog.jena.SDJenaFactory;
import com.hp.hpl.jena.rdf.model.Model;

import net.linkedbuildingdata.rdf.modelfactory.JenaModelFactoryBase;

public class StardogJenaModelFactory extends JenaModelFactoryBase {	
	
	private static final Logger logger = Logger.getLogger(StardogJenaModelFactory.class);
	
	public StardogJenaModelFactory(String name, Properties properties) {
		super(name, properties);
	}

	@Override
	public Model createModel() throws Exception {
		
		logger.info(String.format("Starting embedded server '%s'", getModelId()));

		StardogDBMS.startEmbeddedServer();

		logger.info(String.format("Starting embedded server is completed", getModelId()));

		logger.info(String.format("Clearing model '%s'", getModelId()));

		// first create a temporary database to use (if there is one already,
		// drop it first)
		
		logger.info(String.format("User name '%s' password '%s'", getUserName(), getPassword()));

		StardogDBMS dbms = StardogDBMS.toEmbeddedServer()
			.credentials(getUserName(), getPassword().toCharArray())
			.login();
		
		logger.info(String.format("Model Id '%s'", getModelId()));

		String modelId = getModelId();
		
		if (dbms.list().contains(modelId)) {
			dbms.drop(modelId);
		}
		dbms.createMemory(modelId);
		dbms.logout();
		
		logger.info(String.format("Clearing model '%s' is completed", getModelId()));

		return getModel();		
	}

	@Override
	public Model getModel() throws Exception {
		logger.info(String.format("Getting model '%s'", getModelId()));

		String modelId = getModelId();
		
		Connection connection = ConnectionConfiguration.				
				to(modelId) // the name of
																	// the db to
																	// connect
																	// to
//				.url(getServerUrl())
				.credentials(getUserName(), getPassword()) // the credentials with which to
												// connect
				.connect(); // now open the connection

		// obtain a jena for the specified stardog database. Just creating an
		// in-memory database
		// this is roughly equivalent to ModelFactory.createDefaultModel.
		Model model = SDJenaFactory.createModel(connection);
		
		logger.info(String.format("Getting model '%s' is completed", getModelId()));
		
		return model;
	}

	@Override
	public void release() throws Exception {
		StardogDBMS.stopEmbeddedServer();
	}

}
