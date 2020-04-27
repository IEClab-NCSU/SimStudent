/**
 * 
 * Curriculum Browser used in SimSt Peer Learning Environment
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


import javax.media.*;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * @author mazda
 *
 */
public class CurriculumBrowser {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
	private CurriculumBrowserView cbView;
	private boolean htmlSet = false;
    
    private WebStartFileDownloader fileFinder;
    
    private boolean end = false;
    private boolean stop = false;
    private final boolean repeat = false;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private Slider volumeSlider ;
    private Slider timeSlider;
    private Duration duration;
    private Label time;
  
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public WebStartFileDownloader getFileFinder() {
		return fileFinder;
	}

	public CurriculumBrowser(Dimension tutorPanelSize) {
        cbView = new CurriculumBrowserView(tutorPanelSize);
        fileFinder = new WebStartFileDownloader();
    }   
    
    public CurriculumBrowser() {
        cbView = new CurriculumBrowserView();
        fileFinder = new WebStartFileDownloader();
    }
    
    public JScrollPane getBrowserPane()
    {
    	JScrollPane panel = new JScrollPane(cbView.getBrowserPane());
    	return panel;
    }
    
    public void setBrowserPane(JEditorPane browserPane) {
    	cbView.setBrowserPane(browserPane);
    }
    
    public Container getVideoPanel()
    {
    	return cbView.getContentPane();
    }
    
    public boolean isHtmlSet()
    {
    	return htmlSet;
    }
    
    public void setVideoSource(File f)
    {
    	if(trace.getDebugCode("miss"))trace.out("miss", "setVideoSource file: " + f);
    	try {
			Player p = Manager.createRealizedPlayer(f.toURI().toURL());
	        cbView.setLayout(new BorderLayout());
			cbView.getContentPane().add(p.getVisualComponent(), BorderLayout.CENTER);
			cbView.getContentPane().add(p.getControlPanelComponent(),BorderLayout.NORTH);
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	/*
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    	}
    	catch(IOException e)
    	{
        	e.printStackTrace();
    	}*/
    }
    
    public void setVideoSource(String src)
    {
    	String fileName = this.getFileFinder().findFile(src);
    	if(trace.getDebugCode("miss"))trace.out("miss", "setVideoSource fileName: " + fileName);
    	File f = new File(fileName);
    	JFXPanel panel = new JFXPanel();

    	Media media = new Media(f.toURI().toString());
	    mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);
		
		HBox videoPanel = new HBox(mediaView);
    	videoPanel.setPrefHeight(cbView.getContentPane().getHeight());
    	videoPanel.setPrefWidth(cbView.getContentPane().getWidth());
    	videoPanel.setAlignment(Pos.CENTER);
         playButton = new Button(">>");
	
		playButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				Status status = mediaPlayer.getStatus();
				
				if(status == Status.UNKNOWN || status == Status.HALTED)
					 return;
				else if(status == Status.PAUSED || status == Status.READY || status == Status.STOPPED){
					if(end){
						mediaPlayer.seek(mediaPlayer.getStartTime());
						end = false;
					}
					mediaPlayer.play();
				}
				else
					mediaPlayer.pause();
					
			}
		});
		
		mediaPlayer.currentTimeProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable o){
				updateValues();
			}
		});
		mediaPlayer.setOnPlaying(new Runnable(){
			public void run(){
				if(stop){
					mediaPlayer.pause();
					stop = false;
				}
				else
					playButton.setText("||");
			}
		});
		
		mediaPlayer.setOnPaused(new Runnable(){
			public void run(){
				playButton.setText(">>");
			}
		});
		
		
		mediaPlayer.setOnReady(new Runnable(){
			public void run(){
				duration = mediaPlayer.getMedia().getDuration();
				updateValues();
			}
		});
		mediaPlayer.setOnEndOfMedia(new Runnable(){
			public void run(){
				if(!repeat){
					playButton.setText(">>");
					stop = true;
					end = true;
				}
			}
		});
		
    	HBox mediaBar = new HBox();
    	mediaBar.setAlignment(Pos.CENTER);
    	mediaBar.setPadding(new Insets(2,10,5,10));
    	mediaBar.getChildren().add(playButton);
    	
    	
    	Label spacer = new Label("  ");
    	mediaBar.getChildren().add(spacer);
    	
    	timeSlider = new Slider();
    	HBox.setHgrow(timeSlider, Priority.ALWAYS);
    	timeSlider.setMinWidth(50);
    	timeSlider.setMaxWidth(Double.MAX_VALUE);
    	mediaBar.getChildren().add(timeSlider);
    	
    	
    	
    	time = new Label("");
    	time.setPrefWidth(130);
    	time.setMinWidth(50);
    	mediaBar.getChildren().add(time);
    	
    	
    	Label volumeLabel = new Label("Volume: ");
    	mediaBar.getChildren().add(volumeLabel);
    	
    	volumeSlider =  new Slider();
    	volumeSlider.setPrefWidth(70);
    	volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
    	volumeSlider.setMinWidth(30);
    	mediaBar.getChildren().add(volumeSlider);
    	
    	
    	VBox vpanel = new VBox(2,videoPanel,mediaBar);
    	Scene scene = new Scene(vpanel, cbView.getContentPane().getWidth(), cbView.getContentPane().getHeight());
    	panel.setScene(scene);
		cbView.getContentPane().add(panel);

		
		
		timeSlider.valueProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable o){
				if(timeSlider.isValueChanging()){
					mediaPlayer.seek(duration.multiply(timeSlider.getValue()/100.0));
				}
			}
		});
		
		
		
		
		volumeSlider.valueProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable o){
				if(volumeSlider.isValueChanging())
					mediaPlayer.setVolume(volumeSlider.getValue()/100.0);
			}
		});
    }
    
    
    protected void updateValues(){
    	if(time != null && timeSlider != null && volumeSlider != null){
    		Platform.runLater(new Runnable(){
    			public void run(){
    				Duration current = mediaPlayer.getCurrentTime();
    				time.setText(formatTime(current, duration));
    				timeSlider.setDisable(duration.isUnknown());
    				if(!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging())
    					timeSlider.setValue(current.divide(duration).toMillis()* 100);
    				if(!volumeSlider.isValueChanging())
    					volumeSlider.setValue((int)Math.round(mediaPlayer.getVolume() 
    			                  * 100));
    			}
    		});
    	}
    }
    
    
    private static String formatTime(Duration elapsed, Duration duration) {
    	   int intElapsed = (int)Math.floor(elapsed.toSeconds());
    	   int elapsedHours = intElapsed / (60 * 60);
    	   if (elapsedHours > 0) {
    	       intElapsed -= elapsedHours * 60 * 60;
    	   }
    	   int elapsedMinutes = intElapsed / 60;
    	   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
    	                           - elapsedMinutes * 60;
    	 
    	   if (duration.greaterThan(Duration.ZERO)) {
    	      int intDuration = (int)Math.floor(duration.toSeconds());
    	      int durationHours = intDuration / (60 * 60);
    	      if (durationHours > 0) {
    	         intDuration -= durationHours * 60 * 60;
    	      }
    	      int durationMinutes = intDuration / 60;
    	      int durationSeconds = intDuration - durationHours * 60 * 60 - 
    	          durationMinutes * 60;
    	      if (durationHours > 0) {
    	         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
    	            elapsedHours, elapsedMinutes, elapsedSeconds,
    	            durationHours, durationMinutes, durationSeconds);
    	      } else {
    	          return String.format("%02d:%02d/%02d:%02d",
    	            elapsedMinutes, elapsedSeconds,durationMinutes, 
    	                durationSeconds);
    	      }
    	      } else {
    	          if (elapsedHours > 0) {
    	             return String.format("%d:%02d:%02d", elapsedHours, 
    	                    elapsedMinutes, elapsedSeconds);
    	            } else {
    	                return String.format("%02d:%02d",elapsedMinutes, 
    	                    elapsedSeconds);
    	            }
    	        }
    	    }
    public void setHtmlSource(String src)
    {
    	
    	
    	if(trace.getDebugCode("miss"))trace.out("miss", "Inside setHtmlSource with fileName: " + src);
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		if(SimSt.WEBSTARTENABLED)
    			cbView.getBrowserPane().read(new FileReader(this.fileFinder.findFile(src)), cbView.getBrowserPane().getDocument());
    		else
    				cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    		htmlSet = true;
    	}
    	catch(IOException e)
    	{
    		if(trace.getDebugCode("miss"))trace.out("miss", "Error setting page of curriculum browser "+e.getMessage());
        	e.printStackTrace();
    	}
    }
    
    
    public void setTitle(String title)
    {
    	cbView.setTitle(title);
    }
    
    public String getTitle()
    {
    	return cbView.getTitle();
    }
    
    public void showCB()
    {
    	cbView.setVisible(true);
    	cbView.requestFocus();
    }

}
