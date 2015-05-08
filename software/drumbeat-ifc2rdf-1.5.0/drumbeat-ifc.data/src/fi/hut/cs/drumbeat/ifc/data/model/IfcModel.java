package fi.hut.cs.drumbeat.ifc.data.model;

import java.io.Serializable;
import java.util.*;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.metamodel.IfcMetaModel;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;


public class IfcModel extends IfcModelBase implements Serializable { // IRdfEntityModel {
	
	private static final long serialVersionUID = 1L;
	
	private IfcMetaModel metaModel;
	private IfcEntity projectEntity;	
	private String version;
	
	public IfcModel(IfcSchema schema, IfcMetaModel metaModel) {
		super(schema);
		this.metaModel = metaModel;
	}
	
	@Override
	protected int getInitialEntitySize() {
		return 10240;
	}

//	public IfcSchema getSchema() throws IfcNotFoundException {
//		if (schema == null) {
//			List<String> schemaVersions = metaModel.getFileSchema().getSchemas();
//			for (String schemaVersion : schemaVersions) {
//				schema = IfcSchemaPool.getSchema(schemaVersion);
//				if (schema != null) {
//					return schema;
//				}
//			}
//		}
//		return schema;
//	}

	public IfcEntity getProjectEntity() {
		if (projectEntity == null) {
			List<IfcEntity> entities;
			try {
				entities = getEntitiesByType(IfcVocabulary.TypeNames.IFC_PROJECT);				
			} catch (IfcNotFoundException e) {
				throw new RuntimeException("Unexpected error: type " + IfcVocabulary.TypeNames.IFC_PROJECT + " not found");
			}
			
			if (!entities.isEmpty()) {
				projectEntity = entities.get(0);
			}
			
			assert(projectEntity != null);
		}
		return projectEntity;
	}

	public SortedSet<IfcEntity> getNamedEntities() {
		SortedSet<IfcEntity> namedEntities = new TreeSet<>(new Comparator<IfcEntity>() {
			@Override
			public int compare(IfcEntity o1, IfcEntity o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		});
		
		for (IfcEntity entity : getEntities()) {
			if (entity.hasName()) {
				namedEntities.add(entity);
			}
		}
		return namedEntities;
	}
	
	@Override
	public boolean removeEntity(IfcEntity entity) {
		
		if (getEntities().remove(entity)) {
		
			for (IfcLink link : entity.getIncomingLinks()) {
				
				IfcEntity source = link.getSource();
				source.getOutgoingLinks().remove(link);
				
			}
			
			for (IfcLink link : entity.getOutgoingLinks()) {
				
				IfcLinkInfo linkInfo = link.getLinkInfo();
				
				for (IfcEntityBase destination : link.getDestinations()) {
					if (destination instanceof IfcEntity) {
						((IfcEntity)destination).getOutgoingLinks().remove(linkInfo);
					}
				}
				
			}
			
			return true;
		}
		
		return false;		
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public IfcMetaModel getMetaModel() {
		return metaModel;
	}

	public void setMetaModel(IfcMetaModel metaModel) {
		this.metaModel = metaModel;
	}


//	@Override
//	public Collection<? extends IRdfEntityNode> getEntityNodes(boolean onlyGrounded) {
//		if (onlyGrounded) {
//			return getNamedEntities();			
//		} else {
//			return entities;
//		}
//	}	
}
