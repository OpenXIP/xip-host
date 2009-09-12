package edu.wustl.xipHost.hostControl;

import edu.wustl.xipHost.caGrid.GridLogin;


public class Login {
	
	static GridLogin gridLogin = new GridLogin();
	
	//establishSecureConnection
	
	public static boolean validateGridSecur = false;
	/**
	 * validateUser is a client to middleware authorization and authentication component
	 */		
	public static boolean validateUser(String user, String password){						
		if (user.equals("rater1") && password.equals("Rsn@1Rsn@1")|| user.equals("rater1") && password.equals("123") || user.equals("wustl") && password.equals("erlERL3r()") ||user.equals("wustl") && password.equals("123") ||user.equals("wustl") && password.equals("erl")){
			if(user.equalsIgnoreCase("rater1")){
				password = "Rsn@1Rsn@1";
			}else if(user.equalsIgnoreCase("wustl")){
				password = "erlERL3r()";
			}		
			//set true if security enabled
			if(validateGridSecur){
				if(gridLogin.Login(user, password)){
					userName = user;
					return true;
				}				
			} else {
				userName = user;
				return true;
			}					
		} else {					
			return false;
		}
		return false;
	}
	
	static String userName;
	public String getUserName(){
		return userName;
	}	
	public static void setValidateGridSecur(boolean bln){
		validateGridSecur = bln;
	}
	
	public static void main (String args[]){
		new Login();
	}
}
