# Java-Parser-UML-Generator

Prerequisites –

• Java must be installed on the system.

• GraphViz must be installed on the system.

Instructions to run the program –

• java –jar umlparser.jar <classpath> <outputfilename>
    o classpath - Relative path to the default package folder.
    o outputfilename – filename of the output image (don’t provide extension)
• Output class diagram will be generated in directory where the umlparser.jar file is present.

External Libraries and Tools used – 

1. Javaparser
Javaparser is lightweight and easy to use parser library which parses java code and provides AST (Abstract Syntax Tree). Javaparser uses javacc (Java Compiler Compiler) for generating AST from Java code.
One can analyze code structure, Javadoc or comments using AST created by Javaparser library.

2. GraphViz
GraphViz is open source graph visualization software. GraphViz supports dot(.) notation input for drawing directed graphs. The GraphViz software takes input as simple text file and converts it to diagram. GraphViz provides support for generating output diagram in PDF, Images or SVG format. It has many useful features such as custom coloring, shapes and custom messages.
Limitation: GraphViz application is stand-alone application and can not be integrated in java project without third party library.

3. PlantUML
PlantUML provides a Java based API for using GraphViz software through your Java application. This library is required because GraphViz does not have their Java API exposed which developers can use to integrate GraphViz directly in their application.
As PlantUML uses GraphViz internally, most of the functions that are offered by GraphViz are supported by PlantUML.
