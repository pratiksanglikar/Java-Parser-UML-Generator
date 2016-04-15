package edu.sjsu.cmpe202.pratiksanglikar.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sjsu.cmpe202.pratiksanglikar.beans.Edge;
import edu.sjsu.cmpe202.pratiksanglikar.beans.EdgeType;
import edu.sjsu.cmpe202.pratiksanglikar.beans.Node;
import edu.sjsu.cmpe202.pratiksanglikar.beans.PackageStructure;
import edu.sjsu.cmpe202.pratiksanglikar.utilities.FileHandler;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

/**
 * This class handles all the parsing part of Java Code Parser.
 * @author pratiksanglikar
 *
 */
public class Parser {

	private HashMap<String, CompilationUnit> compilationsUnits;
	private PackageStructure packageStructure;

	public Parser() {
		compilationsUnits = new HashMap<String, CompilationUnit>();
	}

	/**
	 * parses the complete package and fills a {@link PackageStructure} to be used for generating UML diagrams later.
	 * @param folder
	 * @return
	 */
	public PackageStructure parsePackage(File folder) {
		packageStructure = new PackageStructure();
		packageStructure.setPackageName(folder.getName());
		List<File> javaFiles = FileHandler.getAllFilesInDirectory(folder);
		for (File file : javaFiles) {
			parseFile(file);
		}
		checkSetterGetters();
		removeImplementedMethods(compilationsUnits, javaFiles);
		for (File file : javaFiles) {
			createEdges(compilationsUnits.get(file.getName()));
		}

		return packageStructure;
	}

	/**
	 * If certain method is implemented from an interface, it removes it's declaration from class.
	 * @param compilationsUnits
	 * @param javaFiles
	 */
	private void removeImplementedMethods(HashMap<String, CompilationUnit> compilationsUnits, List<File> javaFiles) {
		for (File file : javaFiles) {
			CompilationUnit cu = compilationsUnits.get(file.getName());
			List<TypeDeclaration> types = cu.getTypes();
			for (TypeDeclaration typeDeclaration : types) {
				if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
					ClassOrInterfaceDeclaration type = (ClassOrInterfaceDeclaration) typeDeclaration;
					if (!type.isInterface()) {
						List<MethodDeclaration> methodsToRemove = new ArrayList<MethodDeclaration>(0);
						List<MethodDeclaration> classMembers = packageStructure.getNodeByName(type.getName())
								.getMethods();
						List<ClassOrInterfaceType> implementsList = type.getImplements();
						if (implementsList != null) {
							for (BodyDeclaration classMember : classMembers) {
								for (ClassOrInterfaceType interfaceN : implementsList) {
									List<MethodDeclaration> interfaceMembers = packageStructure
											.getNodeByName(interfaceN.getName()).getMethods();
									for (MethodDeclaration interfaceMethod : interfaceMembers) {
										if (interfaceMethod.getName()
												.equals(((MethodDeclaration) classMember).getName())) {
											methodsToRemove.add((MethodDeclaration) classMember);
										}
									}
								}
							}
						}

						for (MethodDeclaration methodToRemove : methodsToRemove) {
							classMembers.remove(methodToRemove);
						}
					}
				}
			}
		}
	}

	/**
	 * For each node in the package, checks if any private variable has public setters and getters.
	 * if any private variable has public setters and getters, the variable is made public.
	 */
	private void checkSetterGetters() {
		List<Node> nodes = packageStructure.getNodes();
		for (Node node : nodes) {
			List<MethodDeclaration> methodsToRemove = new ArrayList<MethodDeclaration>(0);
			List<FieldDeclaration> fields = node.getFields();
			List<MethodDeclaration> methods = node.getMethods();
			for (FieldDeclaration field : fields) {
				boolean hasGetter = false, hasSetter = false;
				if (field.getModifiers() == 2) {
					String fieldName = field.getVariables().get(0).getId().toString();
					for (MethodDeclaration methodDeclaration : methods) {
						String methodName = methodDeclaration.getName();
						String getterName = "get" + fieldName;
						String setterName = "set" + fieldName;
						if (methodName.equalsIgnoreCase(setterName)) {
							hasSetter = true;
							methodsToRemove.add(methodDeclaration);
						}
						if (methodName.equalsIgnoreCase(getterName)) {
							hasGetter = true;
							methodsToRemove.add(methodDeclaration);
						}
					}
					if (hasSetter && hasGetter) {
						field.setModifiers(1);
						for (MethodDeclaration methodDeclaration : methodsToRemove) {
							methods.remove(methodDeclaration);
						}
					}
				}
			}
		}
	}

	/**
	 * parses the given file using {@link JavaParser} library and creates {@link CompilationUnit}
	 * for each {@link TypeDeclaration} in the file.
	 * @param file to be parsed.
	 */
	private void parseFile(File file) {
		CompilationUnit cu = getCompilationUnit(file);
		compilationsUnits.put(file.getName(), cu);
		createNodes(cu);
	}

	/**
	 * creates all types of edges for given {@link CompilationUnit}
	 * @param cu
	 */
	private void createEdges(CompilationUnit cu) {
		List<TypeDeclaration> td = cu.getTypes();
		for (TypeDeclaration typeDeclaration : td) {
			createExtendsImplementsEdges((ClassOrInterfaceDeclaration) typeDeclaration);
			createAssociationEdges((ClassOrInterfaceDeclaration) typeDeclaration);
			List<BodyDeclaration> members = typeDeclaration.getMembers();
			for (BodyDeclaration member : members) {
				if (member instanceof FieldDeclaration) {
					FieldDeclaration field = (FieldDeclaration) member;
					if (isReferenceType(field.getType())) {
						createCompositionEdge(typeDeclaration, field);
					}
				}
			}
		}
	}

	/**
	 * creates EdgeType.ASSOCIATION relationships for methods which have parameter of some other ReferenceType for given TypeDeclaration.
	 * @param typeDeclaration
	 */
	private void createAssociationEdges(ClassOrInterfaceDeclaration typeDeclaration) {
		createAssociationEdgeForConstructor(typeDeclaration);
		Node node = packageStructure.getNodeByName(typeDeclaration.getName());
		List<BodyDeclaration> methods = typeDeclaration.getMembers();
		// List<MethodDeclaration> removeMethods = new
		// ArrayList<MethodDeclaration>(0);
		for (BodyDeclaration bodyDeclaration : methods) {
			MethodDeclaration methodDeclaration = null;
			if (bodyDeclaration instanceof MethodDeclaration) {
				methodDeclaration = (MethodDeclaration) bodyDeclaration;
				if(methodDeclaration.getName().equals("main")){
					packageStructure.getEdges().add(new Edge(node, packageStructure.getNodeByName("Component"), EdgeType.ASSOCIATION));
				}
				List<Parameter> parameters = methodDeclaration.getParameters();
				if (parameters != null) {
					for (Parameter parameter : parameters) {
						if (isReferenceType(parameter.getType())) {
							Node refNode = packageStructure.getNodeByName(parameter.getType().toString());
							if (!typeDeclaration.isInterface() && refNode.isInterface()) {
								if (packageStructure.getEdge(typeDeclaration.getName(), refNode.getTypeName(),
										EdgeType.ASSOCIATION) == null) {
									packageStructure.getEdges().add(new Edge(node, refNode, EdgeType.ASSOCIATION));
								}
								// removeMethods.add(methodDeclaration);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * creates the EdgeType.ASSOCIATION relationships for parameters in constructors.
	 * @param typeDeclaration typeDeclaration that needs to be processed.
	 */
	private void createAssociationEdgeForConstructor(ClassOrInterfaceDeclaration typeDeclaration) {
		Node node = packageStructure.getNodeByName(typeDeclaration.getName());
		List<BodyDeclaration> methods = typeDeclaration.getMembers();
		for (BodyDeclaration bodyDeclaration : methods) {
			ConstructorDeclaration methodDeclaration = null;
			if (bodyDeclaration instanceof ConstructorDeclaration) {
				methodDeclaration = (ConstructorDeclaration) bodyDeclaration;

				List<Parameter> parameters = methodDeclaration.getParameters();
				if (parameters != null) {
					for (Parameter parameter : parameters) {
						if (isReferenceType(parameter.getType())) {
							Node refNode = packageStructure.getNodeByName(parameter.getType().toString());
							if (!typeDeclaration.isInterface() && refNode.isInterface()) {
								if (packageStructure.getEdge(typeDeclaration.getName(), refNode.getTypeName(),
										EdgeType.ASSOCIATION) == null) {
									packageStructure.getEdges().add(new Edge(node, refNode, EdgeType.ASSOCIATION));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * creates the EdgeType.COMPOSITION relationships for given {@link CompilationUnit} and given {@link FieldDeclaration}
	 * @param typeDeclaration typeDeclaration that needs to be processed.
	 * @param field field of typeDeclaration that needs to be processed. 
	 */
	private void createCompositionEdge(TypeDeclaration typeDeclaration, FieldDeclaration field) {
		String sourceCardinality = "", destinationCardinality = "";
		Node source = packageStructure.getNodeByName(typeDeclaration.getName());
		String destinationTypeName = field.getType().toString();
		if (destinationTypeName.contains("Collection")) {
			destinationCardinality = "*";
			destinationTypeName = destinationTypeName.replace("Collection", "");
			destinationTypeName = destinationTypeName.replace("<", "");
			destinationTypeName = destinationTypeName.replace(">", "");
		}
		Node destination = packageStructure.getNodeByName(destinationTypeName);
		Edge edge = packageStructure.getEdge(destinationTypeName, source.getTypeName(), EdgeType.COMPOSITON);
		if (edge != null) {
			if (edge.getDestCardinality().equals("")) {
				edge.setDestCardinality(sourceCardinality);
			}
		} else {
			edge = new Edge(source, destination, EdgeType.COMPOSITON);
			edge.setSourceCardinality(sourceCardinality);
			edge.setDestCardinality(destinationCardinality);
			packageStructure.getEdges().add(edge);
		}
	}

	/**
	 * creates the edges for EdgeType.EXTENDS and EdgeType.IMPLEMENTS relationships for given {@link TypeDeclaration} 
	 * @param typeDeclaration
	 */
	private void createExtendsImplementsEdges(ClassOrInterfaceDeclaration typeDeclaration) {
		List<ClassOrInterfaceType> extendsList = typeDeclaration.getExtends();
		if (extendsList != null) {
			for (ClassOrInterfaceType classOrInterfaceType : extendsList) {
				Node source = packageStructure.getNodeByName(typeDeclaration.getName());
				Node destination = packageStructure.getNodeByName(classOrInterfaceType.getName());
				packageStructure.getEdges().add(new Edge(source, destination, EdgeType.EXTENDS));
			}
		}
		List<ClassOrInterfaceType> implementsList = typeDeclaration.getImplements();
		if (implementsList != null) {
			for (ClassOrInterfaceType classOrInterfaceType : implementsList) {
				Node source = packageStructure.getNodeByName(typeDeclaration.getName());
				Node destination = packageStructure.getNodeByName(classOrInterfaceType.getName());
				packageStructure.getEdges().add(new Edge(source, destination, EdgeType.IMPLEMENTS));
			}
		}
	}

	/**
	 * creates the nodes for each {@link TypeDeclaration} in given {@link CompilationUnit} and fills the 
	 * {@link PackageStructure}.
	 * @param cu - compilationUnit to be processed.
	 */
	private void createNodes(CompilationUnit cu) {
		List<TypeDeclaration> td = cu.getTypes();
		for (TypeDeclaration typeDeclaration : td) {
			List<BodyDeclaration> members = typeDeclaration.getMembers();
			boolean isInterface = ((ClassOrInterfaceDeclaration) typeDeclaration).isInterface();
			Node node = new Node(typeDeclaration.getName(), isInterface);
			for (BodyDeclaration member : members) {
				if (member instanceof FieldDeclaration) {
					FieldDeclaration fieldMember = (FieldDeclaration) member;
					Type type = fieldMember.getType();
					if (!isReferenceType(type)) {
						node.getFields().add(fieldMember);
					}
				} else if (member instanceof MethodDeclaration) {
					node.getMethods().add((MethodDeclaration) member);
				} else if (member instanceof ConstructorDeclaration) {
					node.getConstructors().add((ConstructorDeclaration) member);
				}
			}
			packageStructure.getNodes().add(node);
		}
	}

	/**
	 * Checks if the given type is ReferenceType.<br>
	 * <B> Note: </B>certain ReferenceTypes - {String, array} are treated as PrimitiveTypes.
	 * @param type to be checked
	 * @return  <B>'true'</B> if the type is ReferenceType<br>
	 * 			<B>'false'</B> if the type is not ReferenceType.
	 */
	private boolean isReferenceType(Type type) {
		if ((type instanceof PrimitiveType) || (type instanceof ReferenceType
				&& (type.toString().equals("String") || type.toString().contains("[]")))) {
			return false;
		}
		return true;
	}

	/**
	 * Returns an object of compilation unit for given file.
	 * @param file
	 * @return compilationUnit
	 */
	private CompilationUnit getCompilationUnit(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		CompilationUnit cu = null;
		try {
			cu = JavaParser.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cu;
	}
}
