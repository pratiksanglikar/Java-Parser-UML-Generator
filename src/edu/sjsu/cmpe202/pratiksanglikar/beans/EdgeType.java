package edu.sjsu.cmpe202.pratiksanglikar.beans;

/**
 * This enum represents various types of edges that can be drawn in a class diagram
 * @author pratiksanglikar
 *
 */
public enum EdgeType {
	EXTENDS, // TheEconomy extends ConcreteSubject => ConcreteSubject <|-- TheEconomy
	IMPLEMENTS, // ConcreteSubject implements Subject =>  Subject <|.. ConcreteSubject
	COMPOSITON, // ConcreteSubject has collection<Observer> => ConcreteSubject 1 - * Observer
	ASSOCIATION // ..>
}
