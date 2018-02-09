package com.dspot.menu.wizard;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;


@SuppressWarnings("restriction")
public class MyHan extends AbstractHandler {
	

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        WizardDialog wizDiag = new WizardDialog(HandlerUtil.getActiveShell(event),new DspotWiz());
        wizDiag.open();
        return null;
    }
    
    public static String getProjectPath() {  // it returns the relative path to the project root from dspot project

        IJavaProject jproject = MyHan.getPro();   // Obtain the javaProject
        IProject project = jproject.getProject(); // Convert to project
        IPath pa = project.getLocation();         // get it's absolute path
	    String p = pa.toString();      // put it into a string
	    return p;
    }  

    public static IJavaProject getPro() {  // it returns the java project that has been used to open the wizard


		ISelection selection = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService().getSelection();
        IJavaProject jproject = null;    
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    

              if (element instanceof IJavaElement) {    
                jproject= ((IJavaElement)element).getJavaProject();
                //project = jProject.getProject();    
            }    
        }  
        return jproject;
      }  // end of the method
              
      public static String[] findSour() {
    	  
    	  ArrayList<String> MyS = new ArrayList<String>(1);
    	  IJavaProject jproject = MyHan.getPro();  // The Java Project
    	  IPackageFragmentRoot[] packs;
		try {
			packs = jproject.getAllPackageFragmentRoots();
    	  for(IPackageFragmentRoot p : packs) {
    		  if(p.getKind() == IPackageFragmentRoot.K_SOURCE && p.hasChildren()) {   			  
    			  boolean isComp = false;
    			  for(IJavaElement jE: getFinalChildren(p)) {  // without this, source folders without compilation units would generate an ArrayOutofBounds error when comparing				                                    
    				 if(jE.getElementType() == IJavaElement.COMPILATION_UNIT) {isComp = true; break; } // the string array sour to the boolean array isTest in Dspa1 line 81
    			  } // end of the for
    			  if(isComp) {
    			  MyS.add(p.getPath().makeRelativeTo(jproject.getPath()).toString());}
    		  }
    	  }  // end of the for
  	  } catch (JavaModelException e) {
			e.printStackTrace();
		}
  		  return MyS.toArray(new String[MyS.size()]);
    	  
      } 
      
      private static IJavaElement[] getFinalChildren(IParent p) {
    	  
    	ArrayList <IJavaElement> myJEList  = new ArrayList<IJavaElement>(1);
    	try {
			IJavaElement[] ProvisionalArray = p.getChildren();
			for(IJavaElement jE: ProvisionalArray) {  
				if(jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT || jE.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT) {  // is not a final children
					IJavaElement[] ProvisionalArray2 = getFinalChildren((IParent)jE);
					for(int i = 0; i < ProvisionalArray2.length; i++) {
						myJEList.add(ProvisionalArray2[i]);
					}
				} else { myJEList.add(jE); }  // is a final children
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
    	  
    	return myJEList.toArray(new IJavaElement[myJEList.size()]); 
    	  
      } // end of the method
	  
}
          
      
      