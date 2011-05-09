/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.hostControl;

import java.util.EventListener;

/**
 * @author Jaroslaw Krych
 *
 */
public interface NewUserListener extends EventListener{
	public boolean validateNewUser(NewUserEvent e, String user, String password);
}
