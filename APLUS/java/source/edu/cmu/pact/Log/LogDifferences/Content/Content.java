package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.Iterator;

public interface Content extends Iterable<ContentCell>{
	public Iterator<ContentCell> iterator();
}