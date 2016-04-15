package edu.sjsu.cmpe202.pratiksanglikar.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents structure of the package including nodes, edges and packageName.
 * @author pratiksanglikar
 *
 */
public class PackageStructure {
	private String packageName;
	private List<Node> nodes;
	private List<Edge> edges;
	
	public PackageStructure() {
		nodes = new ArrayList<Node>(0);
		edges = new ArrayList<Edge>(0);
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * returns an object of {@link Node} for given nodeName;
	 * @param name - typeName
	 * @return  <b>node</b> of provided name
	 * 			<b>null</b> if node with given name is not present in the packageStructure.
	 */
	public Node getNodeByName(String name) {
		for (Node node : nodes) {
			if(node.getTypeName().equals(name)){
				return node;
			}
		}
		return null;
	}
	
	/**
	 * returns edge of edgeType between provided source and destination if present.
	 * @param source
	 * @param destination
	 * @param edgeType
	 * @return 	<b>edge</b> between source and destination of type edgeType.
	 * 			<b>'null'</b> if edge with provided specifications is not present in the packageStructure.
	 */
	public Edge getEdge(String source, String destination, EdgeType edgeType){
		for (Edge edge : edges) {
			if(edge.getSource().getTypeName().equals(source) &&
					(edge.getDestination().getTypeName().equals(destination)) &&
					edge.getEdgeType().equals(edgeType)){
				return edge;
			}
		}
		return null;
	}
}
