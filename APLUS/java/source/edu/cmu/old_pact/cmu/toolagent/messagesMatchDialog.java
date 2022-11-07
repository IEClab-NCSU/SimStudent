//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/messagesMatchDialog.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.pact.Utilities.trace;

/*a pop-up a dialog asking the user whether the given messages are "the same"*/

public class messagesMatchDialog extends Dialog{
    private Button yes,no,cancel;
    private int answer;
    private Label prompt,suppliedPrompt;
    private TextArea msgs;
    private char ptrs[];

    public messagesMatchDialog(Frame parent,String m1,String m2,String text){
        super(parent);
        answer = 0;
        setTitle("Message comparison query");

        prompt = new Label("The system is unable to determine if these messages match.  Do they?");
        suppliedPrompt = new Label(text);

        yes = new Button("Yes");
        no = new Button("No");
        cancel = new Button("Cancel all pending sends");

	Font areaFont = new Font("Courier", Font.PLAIN, 12);
        msgs = new TextArea(3,60);
        msgs.setFont(areaFont);
        msgs.setEditable(false);

        /*ptrs will be used to construct a string which will display 
          asterisks at the location(s) where m1 and m2 differ*/
        if(m1.length() > m2.length()){
            ptrs = new char[m1.length()];
        }
        else{
            ptrs = new char[m2.length()];
        }

        for(int i=0;i<ptrs.length;i++){
            if((i >= m1.length()) ||
               (i >= m2.length())){
                ptrs[i] = '.';
            }
            else{
                if(m1.charAt(i) != m2.charAt(i)){
                    ptrs[i] = '*';
                }
                else{
                    ptrs[i] = ' ';
                }
            }
        }

        msgs.append(m1);
        msgs.append("\n");
        msgs.append(new String(ptrs));
        msgs.append("\n");
        msgs.append(m2);

        yes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    answer = 1;
                    setVisible(false);
                }
            });

        no.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    answer = 0;
                    setVisible(false);
                }
            });

        cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    answer = -1;
                    setVisible(false);
                }
            });

        setLayout(new GridBagLayout());
        
        GridbagCon.viewset(this,prompt, 0,0, 1,1, 5,5,0,5);
        GridbagCon.viewset(this,suppliedPrompt, 0,1, 1,1, 5,5,0,5);
        GridbagCon.viewset(this,msgs, 0,2, 1,1, 5,5,0,5);
        GridbagCon.viewset(this,yes, 0,3, 1,1, 5,5,0,5);
        GridbagCon.viewset(this,no, 0,4, 1,1, 5,5,0,5);
        GridbagCon.viewset(this,cancel, 0,5, 1,1, 5,5,0,5);

        pack();
    }

    /*returns:
      0 => no
      1 => yes
      -1 => cancel*/
    public int getAnswer(){
        setLocation(75,75);
        this.setModal(true);
       setVisible(true);

        trace.out("returning " + answer);
        return answer;
    }
}
