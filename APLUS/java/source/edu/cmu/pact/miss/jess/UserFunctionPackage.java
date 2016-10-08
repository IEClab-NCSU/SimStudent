package edu.cmu.pact.miss.jess;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jess.Rete;
import jess.Userfunction;
import jess.Userpackage;
import edu.cmu.pact.Utilities.trace;

public class UserFunctionPackage implements Userpackage {

	private List funcList = new ArrayList();
	
	public UserFunctionPackage(String[] classNames, ModelTracer amt) {
		
		Class[] amtParameter = {ModelTracer.class};
		Class[] noAMTParameter = new Class[0];
		Object[] amtArgument = {amt};
		Object[] noAMTArgument = new Object[0];
		Class cls;
		Constructor ctor;
		for(int i=0; i< classNames.length; i++){
			try {
				cls = Class.forName(classNames[i]);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Object clsInstance = null;
			for(int j=0; clsInstance == null && j < 2; ++j) {
				Class[] formalArgs = (j < 1? amtParameter : noAMTParameter);
				Object[] actualArgs = (j < 1? amtArgument : noAMTArgument);
				try {
					ctor = cls.getConstructor(formalArgs);
					clsInstance = ctor.newInstance(actualArgs);
					String signature = classNames[i]+"(" + (j < 1 ? "APlusModelTracing" : "")+")";
				} catch(NoSuchMethodException e) {
					e.printStackTrace();
					continue;
				} catch(Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			if(clsInstance instanceof Userfunction) {
				funcList.add(clsInstance);
			}
			else
				trace.err("Can't load " + classNames[i] + " : class does not implement Userfunction");
		}
	}
	
	@Override
	public void add(Rete engine) {
		
		Iterator itr;
		itr = funcList.iterator();
		while(itr.hasNext())
			engine.addUserfunction((Userfunction) itr.next());
	}

	public int size(){
		return funcList.size();
	}
}
