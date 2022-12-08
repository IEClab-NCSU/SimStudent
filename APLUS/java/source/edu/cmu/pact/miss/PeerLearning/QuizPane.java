package edu.cmu.pact.miss.PeerLearning;


import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXTaskPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SimSt;

public class  QuizPane extends JXTaskPane
	{
		private static final long serialVersionUID = 1L;
		boolean locked = false;
		boolean hasResults = false;
		SimStLogger logger;
		BR_Controller brController;
		protected SimStPLEActionListener actionListener;
		
		 public ImageIcon createImageIcon(String path) {
		    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
		    	URL url = this.getClass().getResource(file);
		    	
		    	return new ImageIcon(url);
		    	
		    }
		 
		public QuizPane(String title,SimStLogger logger,BR_Controller brController,SimStPLEActionListener actionListener)
		{
			super(title);
			this.logger=logger;
			this.brController=brController;
			this.actionListener=actionListener;
	
		}
		
		public void updatePane(boolean lock, boolean finished)
		{
			
			
			locked = lock;
			if(finished)
			{
				 setIcon(createImageIcon("img/medal.png"));
				 hasResults = true;
			}
			else if(locked)
				setIcon(createImageIcon("img/lock.png"));
			else{
				setIcon(createImageIcon("img/nolock.png"));
			}
		}
				
		@Override
		public void setCollapsed(boolean collapsed)
		{	
			if(locked)
				return;

			boolean isQuizButtonEnable = brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getQuizButton().isEnabled();
			if(!hasResults && !collapsed && !brController.getMissController().getSimSt().isSsCogTutorMode() && isQuizButtonEnable )
			//if(!hasResults && !collapsed && !brController.getMissController().getSimSt().isSsCogTutorMode())

			{
				
				int result = JOptionPane.showConfirmDialog(null, "This quiz has not yet been taken.  Do you want "+SimSt.getSimStName()+" to take it now?",
						"Take Quiz?", JOptionPane.YES_NO_OPTION);
				logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.UNTAKEN_QUIZ_EXPAND_ACTION, ""+(result==JOptionPane.YES_OPTION), logger.getCurrentTime());
				if(result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
					return;
				logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.UNTAKEN_QUIZ_INITIATE_ACTION,"", logger.getCurrentTime());
			      if(brController.getMissController() != null && brController.getMissController().getSimSt() != null && brController.getMissController().getSimSt().isSsMetaTutorMode()) {
		        	if(brController.getAmt() != null) {
		        		brController.getAmt().handleInterfaceAction(SimStPLE.QUIZ, "ButtonPressed", "-1");
		        	}
		        }
				actionListener.takeQuiz();
			}					
			
			super.setCollapsed(collapsed);
		}
		
	}
