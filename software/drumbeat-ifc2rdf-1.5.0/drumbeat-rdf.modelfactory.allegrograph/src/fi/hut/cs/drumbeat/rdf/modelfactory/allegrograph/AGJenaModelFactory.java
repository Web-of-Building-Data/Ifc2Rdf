package fi.hut.cs.drumbeat.rdf.modelfactory.allegrograph;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;

import com.franz.agraph.jena.AGGraph;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGModel;
import com.franz.agraph.repository.AGCatalog;
import com.franz.agraph.repository.AGRepository;
import com.franz.agraph.repository.AGRepositoryConnection;
import com.franz.agraph.repository.AGServer;
import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.rdf.modelfactory.JenaModelFactoryBase;

public class AGJenaModelFactory extends JenaModelFactoryBase implements Cloneable {
	
	private static final Logger logger = Logger.getLogger(AGJenaModelFactory.class);	
	
	//////////////////////////////////////////////
	// STATIC MEMBERS 
	//////////////////////////////////////////////
	
	private AGRepository repository;
	private Model model;

	public AGJenaModelFactory(String factoryName, Properties properties) {
		super(factoryName, properties);
	}
	
	@Override
	public Model createModel() throws Exception {
		model = getModel();
		
		if (!model.isEmpty()) {

			logger.info(String.format("[AG] Clearing model '%s'", getModelId()));
			
			model.removeAll();
			
			logger.info(String.format("[AG] Clearing model '%s' compeleted", getModelId()));
		}
		
		return model;
	}

	@Override
	public Model getModel() throws Exception {
		if (model == null) {
			
			if (getModelId() == null) {
				throw new IllegalArgumentException(String.format("Argument %s is undefined", ARGUMENT_MODEL_ID));
			}
			
			logger.info(String.format("[AG] Getting model <%s>", getModelId()));
	
			AGRepositoryConnection connection = getRepository().getConnection();
			AGGraphMaker graphMaker = new AGGraphMaker(connection);
			
			AGGraph graph = graphMaker.getGraph();
			model = new AGModel(graph);
			
			logger.info(String.format("[AG] Getting model <%s> completed", getModelId()));
		}
		
		return model;
	}

	@Override
	public void release() throws Exception {
		if (repository != null) {
			repository.getConnection().close();
			repository = null;
			model.close();
			model = null;
		}
	}

	private AGRepository getRepository() throws RepositoryException {		
		if (repository == null) {		
			logger.info(String.format("[AG] Connecting to AllegroGraph server <%s>", getServerUrl()));
			
			AGServer server = new AGServer(getServerUrl(), getUserName(), getPassword());
			
			AGCatalog catalog = server.getRootCatalog();
			
			logger.info(String.format("[AG] Creating repository <%s>", getModelId()));

			repository = catalog.createRepository(getModelId());
			repository.initialize();
			
			logger.info(String.format("[AG] Repository <%s> is initialized", getModelId()));
		}
		return repository;
	}
	
}
