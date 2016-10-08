package edu.cmu.old_pact.cmu.uiwidgets;

class MessageString implements SettableText {
	String m_msg;
		
	public MessageString()
	{
		m_msg="";
	}
		
	public MessageString(String text)
	{
		m_msg=text;
	}	
	
	public void setText(String text)
	{
		m_msg=new String (text);
	}
	
	String getText()
	{
		return m_msg;
	}		
}	