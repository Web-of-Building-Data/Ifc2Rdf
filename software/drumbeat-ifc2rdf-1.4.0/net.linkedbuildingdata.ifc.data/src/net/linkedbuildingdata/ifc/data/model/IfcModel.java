package net.linkedbuildingdata.ifc.data.model;

import java.io.Serializable;
import java.util.*;

import net.linkedbuildingdata.ifc.data.schema.IfcLinkInfo;
import net.linkedbuildingdata.ifc.data.schema.IfcSchema;
import net.linkedbuildingdata.ifc.data.schema.IfcSchemaPool;


public class IfcModel implements Serializable { // IRdfEntityModel {
	
	private static final long serialVersionUID = 1L;
	
	private IfcEntity projectEntity;
	private List<IfcEntity> entities = new ArrayList<>(10240);
	private String schemaVersion;
	private String version;
	
	public IfcModel(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	
	public IfcSchema getSchema() {
		return IfcSchemaPool.getSchema(schemaVersion);
	}

	public IfcEntity getProjectEntity() {
		return projectEntity;
	}

	public void setProjectEntity(IfcEntity projectEntity) {
		this.projectEntity = projectEntity;
	}
	
	public Collection<IfcEntity> getEntities() {
		return entities;
	}
	
	public SortedSet<IfcEntity> getNamedEntities() {
		SortedSet<IfcEntity> namedEntities = new TreeSet<>(new Comparator<IfcEntity>() {
			@Override
			public int compare(IfcEntity o1, IfcEntity o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		});
		
		for (IfcEntity entity : entities) {
			if (entity.hasName()) {
				namedEntities.add(entity);
			}
		}
		return namedEntities;
	}
	
	public void addEntity(IfcEntity entity) {
		entities.add(entity);	
	}
	
	public boolean removeEntity(IfcEntity entity) {
		
		if (entities.remove(entity)) {
		
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

//	@Override
//	public Collection<? extends IRdfEntityNode> getEntityNodes(boolean onlyGrounded) {
//		if (onlyGrounded) {
//			return getNamedEntities();			
//		} else {
//			return entities;
//		}
//	}	
}
