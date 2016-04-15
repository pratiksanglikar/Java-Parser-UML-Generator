package edu.sjsu.cmpe202.pratiksanglikar.beans;

/**
 * This class represents an edge for representing two classes aka {@link Node}.
 * @author pratiksanglikar
 *
 */
public class Edge {
	
	private Node source;
	private Node destination;
	private EdgeType edgeType;
	private String sourceCardinality;
	private String destCardinality;
	
	public Edge(Node source, Node destination, EdgeType edgeType) {
		super();
		this.source = source;
		this.destination = destination;
		this.edgeType = edgeType;
	}
	
	public Node getSource() {
		return source;
	}
	
	public void setSource(Node source) {
		this.source = source;
	}

	public Node getDestination() {
		return destination;
	}
	
	public void setDestination(Node destination) {
		this.destination = destination;
	}
	
	public EdgeType getEdgeType() {
		return edgeType;
	}
	
	public void setEdgeType(EdgeType edgeType) {
		this.edgeType = edgeType;
	}
	
	public String getSourceCardinality() {
		return sourceCardinality;
	}
	
	public void setSourceCardinality(String sourceCardinality) {
		this.sourceCardinality = sourceCardinality;
	}
	
	public String getDestCardinality() {
		return destCardinality;
	}
	
	public void setDestCardinality(String destCardinality) {
		this.destCardinality = destCardinality;
	}
}
