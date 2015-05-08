package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;

public abstract class IfcModelBase {

	private IfcSchema schema;
	private List<IfcEntity> entities;

	public IfcModelBase(IfcSchema schema) {
		this.schema = schema;
		entities = new ArrayList<>(getInitialEntitySize());
	}
	
	protected int getInitialEntitySize() {
		return 10;
	}
	
	public IfcSchema getSchema() {
		return schema;
	}
	
	public List<IfcEntity> getEntities() {
		return entities;
	}
	
	public List<IfcEntity> getEntitiesByType(IfcEntityTypeInfo entityType) {
		List<IfcEntity> selectedEntities = new ArrayList<>();		
		for (IfcEntity entity : entities) {
			if (entity.isInstanceOf(entityType)) {
				selectedEntities.add(entity);
			}
		}
		return selectedEntities;
	}
	
	public List<IfcEntity> getEntitiesByType(String entityTypeName) throws IfcNotFoundException {
		IfcEntityTypeInfo entityType = getSchema().getEntityTypeInfo(entityTypeName);
		return getEntitiesByType(entityType);
	}
	
	public IfcEntity getFirstEntityByType(IfcEntityTypeInfo entityType) throws IfcNotFoundException {
		for (IfcEntity entity : entities) {
			if (entity.isInstanceOf(entityType)) {
				return entity;
			}
		}
		throw new IfcNotFoundException("Entity with type " + entityType + " not found");
	}
	
	public IfcEntity getFirstEntityByType(String entityTypeName) throws IfcNotFoundException {		
		IfcEntityTypeInfo entityType = getSchema().getEntityTypeInfo(entityTypeName);
		return getFirstEntityByType(entityType);
	}

	public void addEntities(Collection<IfcEntity> entities) {
		this.entities.addAll(entities);	
	}

	public void addEntity(IfcEntity entity) {
		entities.add(entity);	
	}
	
	public boolean removeEntity(IfcEntity entity) {		
		return entities.remove(entity);		
	}

}
