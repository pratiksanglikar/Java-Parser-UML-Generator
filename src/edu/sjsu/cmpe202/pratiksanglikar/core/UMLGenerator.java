package edu.sjsu.cmpe202.pratiksanglikar.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import edu.sjsu.cmpe202.pratiksanglikar.beans.Edge;
import edu.sjsu.cmpe202.pratiksanglikar.beans.EdgeType;
import edu.sjsu.cmpe202.pratiksanglikar.beans.Node;
import edu.sjsu.cmpe202.pratiksanglikar.beans.PackageStructure;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import net.sourceforge.plantuml.SourceStringReader;
/**
 * generates UML diagram for given packageStructure.
 * @author pratiksanglikar
 *
 */
public class UMLGenerator {
	
	/**
	 * generates UML for given packageStructure.
	 * @param packageStructure
	 */
	public void generateUML(PackageStructure packageStructure, String fileName){
		if(packageStructure == null){
			System.out.println("Package Structure is null");
		}
		String umlString = "@startuml\n";
		List<Node> nodes = packageStructure.getNodes();
		for (Node node : nodes) {
			umlString += generateNodeString(node);
		}
		List<Edge> edges = packageStructure.getEdges();
		for (Edge edge: edges ){
			umlString += generateEdgeString(edge);
		}
		umlString += "\n@enduml";
		generateDiagram(umlString, fileName);
	}

	/**
	 * generates String for given edge
	 * @param edge
	 * @return string to be appended to nodeString.
	 */
	private String generateEdgeString(Edge edge) {
		String edgeString = "";
		String sourceName = edge.getSource().getTypeName();
		String destinationName = edge.getDestination().getTypeName();
		String edgeType = getEdgeType(edge.getEdgeType());
		if(edge.getEdgeType().equals(EdgeType.COMPOSITON)){
			edgeString += sourceName + getCardinalityString(edge.getSourceCardinality()) + edgeType + getCardinalityString( edge.getDestCardinality()) + destinationName + " \n";
		} else {
			edgeString += sourceName + " " + edgeType + " " + destinationName + " \n";
		}
		 
		/*if(edge.getEdgeType().equals(EdgeType.COMPOSITON)){
			//edgeString += sourceName + getCardinalityString(edge.getSourceCardinality()) + edgeType + getCardinalityString( edge.getDestCardinality()) + destinationName + " \n";
			edgeString += destinationName + getCardinalityString(edge.getDestCardinality()) + edgeType + getCardinalityString( edge.getSourceCardinality()) + sourceName + " \n";
		} else {
			edgeString += sourceName + " " + edgeType + " " + destinationName + " \n";
			edgeString += destinationName + " " + edgeType + " " + sourceName + " \n";
		}*/
		return edgeString;
	}

	/**
	 * returns string representation of the provided edgeType.
	 * @param edgeType
	 * @return
	 */
	private String getEdgeType(EdgeType edgeType) {
		switch (edgeType) {
		case IMPLEMENTS:
			return "..|>";
		case EXTENDS:
			return "--|>";
		case COMPOSITON:
			return "--";
		case ASSOCIATION:
			return "..>";
		}
		return "";
	}

	/**
	 * generates string representation of provided node.
	 * @param node
	 * @return
	 */
	private String generateNodeString(Node node) {
		String nodeString = "";
		String className = node.getTypeName();
		if(node.isInterface()){
			nodeString += "interface " + className + " << interface >> {\n";
		} else {
			nodeString += "class " + className + " {\n";
		}
		List<FieldDeclaration> fields = node.getFields();
		for (FieldDeclaration fieldDeclaration : fields) {
			nodeString += generateFieldString(fieldDeclaration);
		}
		nodeString += "--\n";
		List<MethodDeclaration> methods = node.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			nodeString += generateMethodString(methodDeclaration);
		}
		
		List<ConstructorDeclaration> constructors = node.getConstructors();
		for (ConstructorDeclaration constructorDeclaration : constructors) {
			nodeString += generateConstructorString(constructorDeclaration);
		}
		nodeString += " }\n";
		return nodeString;
	}

	/**
	 * generates the String for constructors of a class.
	 * @param constructorDeclaration
	 * @return
	 */
	private String generateConstructorString(ConstructorDeclaration constructorDeclaration) {
		String constructorString = "";
		if(constructorDeclaration.getModifiers() != 1){
			return constructorString;
		}
		List<Parameter> parameters = constructorDeclaration.getParameters();
		constructorString += "+ " + constructorDeclaration.getName() + "(";
		if (parameters != null) {
			for (Parameter typeParameter : parameters) {
				constructorString += typeParameter.getId() + " : " + typeParameter.getType();
			} 
		}
		constructorString += ")\n";
		return constructorString;
	}

	/**
	 * generates the String for methods in a class.
	 * @param methodDeclaration
	 * @return
	 */
	private String generateMethodString(MethodDeclaration methodDeclaration) {
		String methodString = "";
		int methodModifiers = methodDeclaration.getModifiers();
		if(methodModifiers != 1 && methodModifiers !=  1025 && methodModifiers != 9){
			return methodString;
		}
		String returnType = methodDeclaration.getType().toString();
		String name = methodDeclaration.getName();
		methodString += "+ " + name + "(";
		List<Parameter> parameters = methodDeclaration.getParameters();
		if(parameters != null){
			for (Parameter typeParameter : parameters) {
				methodString += typeParameter.getId() + " : " + typeParameter.getType();
			}
		}
		methodString += " ) : " + returnType + "\n";
		return methodString;
	}

	/**
	 * generates the String for fields in a class.
	 * @param fieldDeclaration
	 * @return
	 */
	private String generateFieldString(FieldDeclaration fieldDeclaration) {
		String fieldString = "";
		String typeName = "";
		if(fieldDeclaration.getModifiers() == 1){ //public
			fieldString += "+";
		} else if(fieldDeclaration.getModifiers() == 2){ //Private
			fieldString += "-";
		} else {
			// Protected: 4
			// Default: 0
			return fieldString;
		}
		Type fieldType = fieldDeclaration.getType();
		if(fieldType instanceof PrimitiveType){
			PrimitiveType type = (PrimitiveType) fieldType;
			typeName = type.toString();
		} else if(fieldType instanceof ReferenceType) {
			typeName = fieldType.toString();
		}
		List<VariableDeclarator> variables = fieldDeclaration.getVariables();
		fieldString += variables.get(0).getId().toString();
		fieldString += " : " + typeName + "\n";
		return fieldString;
	}
	
	/**
	 * generates the png file of the uml using the String
	 * @param umlString
	 * @param packageName
	 */
	private void generateDiagram(String umlString, String packageName) {
		OutputStream png = null;
		try 
		{
			png = new FileOutputStream("../sample_test_cases/" + packageName + ".png");
		}
		catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		SourceStringReader reader = new SourceStringReader(umlString);
		try 
		{
			reader.generateImage(png);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * generates the cardinality String.
	 * @param cardinality
	 * @return
	 */
	private String getCardinalityString(String cardinality){
		if("".equals(cardinality)){
			return "";
		}
		return " \"" + cardinality + "\" ";
	}
}
