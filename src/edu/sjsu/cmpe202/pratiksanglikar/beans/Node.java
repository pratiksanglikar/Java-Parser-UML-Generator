package edu.sjsu.cmpe202.pratiksanglikar.beans;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

/**
 * This class represents one Class of the Java source code. 
 * @author pratiksanglikar
 *
 */
public class Node {
	private List<FieldDeclaration> fields;
	private List<MethodDeclaration> methods;
	private List<ConstructorDeclaration> constructors;
	private String typeName;
	private boolean isInterface;
	
	public Node(String typeName, boolean isInterface) {
		fields = new ArrayList<FieldDeclaration>(0);
		methods = new ArrayList<MethodDeclaration>(0);
		constructors = new ArrayList<ConstructorDeclaration>(0);
		this.typeName = typeName;
		this.isInterface = isInterface;
	}

	public List<FieldDeclaration> getFields() {
		return fields;
	}

	public void setFields(List<FieldDeclaration> fields) {
		this.fields = fields;
	}

	public List<MethodDeclaration> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodDeclaration> methods) {
		this.methods = methods;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<ConstructorDeclaration> getConstructors() {
		return constructors;
	}

	public void setConstructors(List<ConstructorDeclaration> constructors) {
		this.constructors = constructors;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
}