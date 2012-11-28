package org.jboss.forge.test.parser.java;

import java.util.List;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.ValuePair;
import org.junit.Test;

public class FlowerAnnotationTest {

	@Test
	public void test() {
		JavaClass javaClass = JavaParser.create(JavaClass.class);
		javaClass.setPackage("it.coopservice.test");
		javaClass.setName("SimpleClass");
		Method<JavaClass> method = javaClass.addMethod("public void test(){ }");
		String name = "Flower";
		String destinationType = "/queue/topic";
		String destinationName = "topic";
		Annotation<JavaClass> annotation = method.addAnnotation("Mdb");
		annotation
				.setLiteralValue("name", "\"" + name + "\"")
				.setLiteralValue(
						"activationConfig",
						"{@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \""
								+ destinationType
								+ "\"), "
								+ "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \""
								+ destinationName + "\")" + "}");
		method.addAnnotation("Value").setLiteralValue("name", "33");
		method.addAnnotation("NoValue");
		method.addAnnotation("Composite").setLiteralValue("nome", "fiorenzo")
				.setLiteralValue("cognome", "pizza");
		for (Annotation anno : method.getAnnotations()) {
			System.out.println("TO_STR: " + anno.toString());
			System.out.println("NAME: " + anno.getName());
			System.out.println("LV: " + anno.getLiteralValue());
			System.out.println("LV WITH NAME:" + anno.getLiteralValue("name"));
			List<ValuePair> list = anno.getValues();
			for (ValuePair val : list) {
				System.out.println("--------------------------");
				System.out.println(val.getName());
				System.out.println(val.getStringValue());
				System.out.println("--------------------------");
			}
			System.out.println("IS MARKER: " + anno.isMarker());
			System.out.println("IS N: " + anno.isNormal());
			System.out.println("IS SV: " + anno.isSingleValue());
			System.out.println("***************");
		}

		System.out.println(javaClass.toString());
	}
}
