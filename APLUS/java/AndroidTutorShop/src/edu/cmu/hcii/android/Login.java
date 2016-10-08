/**
 * 
 */
package edu.cmu.hcii.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author sewall
 *
 */
public class Login extends Activity {
	private static final String USERNAME_ID = "username_id";
    private EditText usernameEditText;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        
        setTitle(R.string.login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        Button goButton = (Button) findViewById(R.id.goButton);

        username = (savedInstanceState == null ? null :
        		(String) savedInstanceState.getSerializable(USERNAME_ID));
        if (username == null) {
            Bundle extras = getIntent().getExtras();
            username = extras != null ? extras.getString(USERNAME_ID) : null;
        }

        goButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	username = usernameEditText.getText().toString();
            	if (username == null || username.length() < 1)
            		return;
            	AndroidTutorShop ats = AndroidTutorShop.create(Login.this);
            	System.out.println("Calling AndroidTutorShop.runService("+username+")");
            	ats.runService(username);
            }

        });
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(USERNAME_ID, username);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
