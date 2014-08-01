package com.scxj.utils;

import java.util.Stack;

import android.app.Activity;
  
public class ScreenManager {   
    private static Stack<Activity> activityStack;   
    private static ScreenManager instance;   
    private  ScreenManager(){   
    }   
    public static ScreenManager getScreenManager(){   
        if(instance==null){   
            instance=new ScreenManager();   
        }   
        return instance;   
    }   
    public void popActivity(){   
        Activity activity=activityStack.lastElement();   
        if(activity!=null){  
        	if (!activity.isFinishing()) {
        		 activity.finish();   
			}
            activity=null;   
        }   
    }   
    public void popActivity(Activity activity){   
        if(activity!=null){   
        	if (!activity.isFinishing()) {
        		activity.finish();
			}
            activityStack.remove(activity);   
            activity=null;   
        }   
    }   
    public Activity currentActivity(){   
    	if(activityStack == null || activityStack.isEmpty()){
    		return null;
    	}
        Activity activity=activityStack.lastElement();   
        return activity;   
    }   
    
    public void pushActivity(Activity activity){   
        if(activityStack==null){   
            activityStack=new Stack<Activity>();   
        }  
        
       for (int i = 0; i < activityStack.size(); i++) {
		if (activityStack.get(i).equals(activity)) {
			popActivity(activityStack.get(i));
		}
	}
        
        activityStack.add(activity);   
    }   
       
    public void popAllActivityExceptOne(Class<? extends Activity> cls){   
        while(true){   
            Activity activity=currentActivity();   
            if(activity==null){   
                break;   
            }   
            if(activity.getClass().equals(cls) ){   
                break;   
            }   
            popActivity(activity);   
        }   
    }   
    public void popAllActivity(){   
        while(true){   
            Activity activity=currentActivity();   
            if(activity==null){   
                break;   
            }   
            popActivity(activity);   
        }   
    } 
}  
