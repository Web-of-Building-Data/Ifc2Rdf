package net.linkedbuildingdata.ifc.util.versioning.structuredmsg;

import java.io.IOException;

import net.linkedbuildingdata.ifc.util.decomposing.msg.RdfMsgExporter;
import net.linkedbuildingdata.rdf.data.msg.RdfMsgContainer;


public class StructuredMsgContainerUpdateExporter {
	
	private StructuredMsgContainerUpdateExporter() {
	}
	
	public static void export(StructuredMsgContainerUpdate structuredMsgContainerUpdate, String filePathFormat) throws IOException {
		new StructuredMsgContainerUpdateExporter().exportStructureMsgContainerUpdate(structuredMsgContainerUpdate, filePathFormat);
	}

	private void exportStructureMsgContainerUpdate(StructuredMsgContainerUpdate structuredMsgContainerUpdate, String filePathFormat) throws IOException {
		
		for (UpdateType updateType : UpdateType.values()) {
			
			String filePathFormatWithUpdateType = String.format(filePathFormat, updateType); 
			RdfMsgContainer container = structuredMsgContainerUpdate.getStructuredMsgContainerByUpdateType(updateType);
			RdfMsgExporter.export(container, filePathFormatWithUpdateType);
		}
		
	}	

}
