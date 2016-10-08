package tracer;
import java.util.Iterator;

import jess.Fact;
import jess.JessEvent;
import jess.JessException;
import jess.JessListener;
import jess.Rete;
import jess.WorkingMemoryMarker;


public class MTTester {
		
	public static void main(String[] args) throws JessException {
		MTSolver tracer = new MTSolver("initWmeTest.clp", "wmeTypesTest.clp", "productionTest.pr");
		
		
		MTSAI sai1 = new MTSAI("dorminTable3_C1R1", "UpdateTable", "add 7x");
		MTSAI sai2 = new MTSAI("dorminTable1_C1R2", "UpdateTable", "15x-10");
		MTSAI sai3 = new MTSAI("dorminTable2_C1R2", "UpdateTable", "9-7x");
		MTSAI sai4 = new MTSAI("dorminTable3_C1R2", "UpdateTable", "add 8x");
		MTSAI sai5 = new MTSAI("dorminTable1_C1R3", "UpdateTable", "15x");
		MTSAI sai6 = new MTSAI("dorminTable2_C1R3", "UpdateTable", "19");
		MTSAI sai7 = new MTSAI("dorminTable3_C1R3", "UpdateTable", "divide 15");
		MTSAI sai8 = new MTSAI("dorminTable1_C1R4", "UpdateTable", "x");
		MTSAI sai9 = new MTSAI("dorminTable2_C1R4", "UpdateTable", "19/15");
		MTSAI sai10 = new MTSAI("dorminTable2_C1R4", "UpdateTable", "191/15");
		
//		tracer.printActivations();
		

		
//		tracer.printFacts();
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());
//		tracer.printFacts();
//
//		System.out.println(tracer.sendSAI(sai1));
//		System.out.println(tracer.sendSAI(sai2));
//		tracer.printActivations();
//		System.out.println(tracer.sendSAI(sai3));

//		tracer.printFacts();

		MTSAI buggysai = new MTSAI("dorminTable3_C1R1", "UpdateTable", "subtract -10");
		System.out.println(tracer.sendSAI(buggysai));

		
//		tracer.printActivations();
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());
//
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());
//
//		System.out.println(tracer.sendSAI(sai4));
//		System.out.println(tracer.sendSAI(sai5));
//		System.out.println(tracer.sendSAI(sai6));
//		System.out.println(tracer.sendSAI(sai7));
//		System.out.println(tracer.sendSAI(sai3));
//
//		System.out.println(tracer.sendSAI(sai8));
//		System.out.println(tracer.sendSAI(sai9));
//		System.out.println(tracer.sendSAI(sai10));
//		tracer.printActivations();
		
//		tracer.printFacts();
		
		
		
//		SAI sai5 = new SAI("product-num", "UpdateTextArea", "35");
//		SAI sai2 = new SAI("sum-num", "UpdateTextArea", "7");
//		SAI sai3 = new SAI("product-num", "UpdateTextArea", "9");
//		SAI sai4 = new SAI("product-num", "UpdateTextArea", "35");

//		tracer.printActivations();
//		System.out.println(tracer.getHint());
//
//		System.out.println(tracer.getHint());
//
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());
//
//		System.out.println(tracer.getHint());
//		System.out.println(tracer.getHint());


//		System.out.println(tracer.sendSAI(sai1));
//		tracer.printFacts();
//		System.out.println(tracer.sendSAI(sai5));
//		System.out.println(tracer.sendSAI(sai2));
//		System.out.println(tracer.sendSAI(sai3));
//		System.out.println(tracer.sendSAI(sai4));



	}
	
}
