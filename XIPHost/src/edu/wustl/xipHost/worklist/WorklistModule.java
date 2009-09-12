package edu.wustl.xipHost.worklist;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class WorklistModule implements Module{
	public void configure(Binder binder) {
	    binder.bind(Worklist.class).to(XMLWorklistImpl.class).in(Scopes.SINGLETON);
	  }
}
