package edu.cmu.old_pact.html.library;

public class AutoWrapper {
	//String title;
	//HTMLElement bodyElement = null;
	//HTMLElement titleElement = null;
	HTMLElement textElement = null;
	//HTMLElement wrappedTextElement = null;
	String bgColor = "#33EEBF";
	String textColor = "#000000";
	String fontSize = "3";   //make default size=12 (instead of 10) "1";
	
	public AutoWrapper(String tit, String bod) {
		//newTitle(tit);
		newBody(bod);
	}
	
	public void delete(){
	/*
		if(titleElement != null)
			titleElement.delete();
		titleElement = null;
		if(bodyElement != null)
			bodyElement.delete();
		bodyElement = null;
	*/
		if(textElement != null)
			textElement.delete();
		textElement = null;
	/*
		if(wrappedTextElement != null)
			wrappedTextElement.delete();
		wrappedTextElement = null;
	*/
	}

	public void newTitle(String tit) {
	/*
		if(titleElement != null)
			titleElement.delete();
		titleElement = null;
		titleElement = new HTMLElement("TITLE", tit);
		title = titleElement.getHTMLText();
	*/
	}
	
	public void newBody(String body) {
		if(textElement != null)
			textElement.delete();
		textElement = null;
		textElement = new HTMLElement("TEXT",body);
		textElement.setAttribute("FONT", "COLOR", textColor);
		textElement.setAttribute("FONT", "SIZE", fontSize);
		//bodyElement = new HTMLElement("BODY", textElement.getHTMLText());
	}
	
	public void setBGColor(String col) {
		bgColor = col;
	}
		
	public void setFGColor(String col) {
		textColor = col;
	}
	
	public void setFontSize(int size){
		int intSize = -1;
		for (int i = 0; i < HtmlPager.sizes.length; i++) {
      		if (size == HtmlPager.sizes[i]) {
      			intSize = i;
      			break;
      		}
      	}
      	intSize = 7-intSize;
		fontSize = String.valueOf(intSize);
	}
	
	public int getHtmlFontSize(){
		return Integer.parseInt(fontSize);
	}

	public String wrappedText(){
	/*
		if(wrappedTextElement != null)
			wrappedTextElement.delete();
		wrappedTextElement = null;
		bodyElement.setAttribute("bgColor", bgColor);
		
		bodyElement.setAttribute("FONT", "COLOR", textColor);
		bodyElement.setAttribute("FONT", "SIZE", fontSize);
		
		String body = bodyElement.getHTMLText();
		wrappedTextElement = new HTMLElement("HTML", title+body);
		return wrappedTextElement.getHTMLText();
		*/
		return textElement.getHTMLText();
	}
	
}
