package fi.hut.cs.drumbeat.rdf.modelfactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.string.StringUtils;


public abstract class JenaModelFactoryBase {
	
	public static final String ARGUMENT_SERVER_URL = "ServerUrl";
	public static final String ARGUMENT_USER_NAME = "UserName";
	public static final String ARGUMENT_PASSWORD = "Password";
	public static final String ARGUMENT_MODEL_ID = "ModelId";
	
	public static JenaModelFactoryBase getFactory(ConfigurationItemEx configuration) throws NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String name = configuration.getName();
		String className = configuration.getType();
		Properties properties = configuration.getProperties();		
		
		Class<? extends JenaModelFactoryBase> jenaModelFactoryClass = Class.forName(className).asSubclass(JenaModelFactoryBase.class);
		Constructor<? extends JenaModelFactoryBase> constructor = jenaModelFactoryClass.getConstructor(String.class, Properties.class);
		JenaModelFactoryBase modelFactory = constructor.newInstance(name, properties);
		modelFactory.setServerUrl(properties.getProperty(ARGUMENT_SERVER_URL)); 
		modelFactory.setUserName(properties.getProperty(ARGUMENT_USER_NAME));
		modelFactory.setPassword(properties.getProperty(ARGUMENT_PASSWORD));
		modelFactory.setModelId(properties.getProperty(ARGUMENT_MODEL_ID));
		return modelFactory;
	}
	
	private String serverUrl;
	private String userName;
	private String password;
	private String modelId;
	private Properties properties;
	
	public JenaModelFactoryBase(String name, Properties properties) {
		this.modelId = name;
		this.properties = properties;
	}
	
	public String getName() {
		return modelId;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	protected String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public Properties getProperties() {
		return properties;
	}
	
	protected String getProperty(String key, boolean isMandatory) {
		String value = properties.getProperty(key);
		if (isMandatory && StringUtils.isEmptyOrNull(value)) {
			throw new IllegalArgumentException(String.format("Undefined parameter '%s'", key));
		}
		return value;
	}	
	
	public abstract Model createModel() throws Exception;
	
	public abstract Model getModel() throws Exception;
	
	public abstract void release() throws Exception;
	
}
