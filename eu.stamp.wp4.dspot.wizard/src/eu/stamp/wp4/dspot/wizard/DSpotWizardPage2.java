package eu.stamp.wp4.dspot.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.widgets.List;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

import eu.stamp.wp4.dspot.dialogs.*;
import eu.stamp.wp4.dspot.wizard.utils.WizardConfiguration;

/**
 * this class describes the second page of the DSpot wizard 
 *
 */
@SuppressWarnings("restriction")
public class DSpotWizardPage2 extends WizardPage {
	
	private boolean[] Comp = {true,false};  // this is to set page complete
	// [0] i : number of iterations, [1] execution test class, [2] Method, [3] test criterion,
	// [4] max test amplified
	private String[] MyStrings = new String[5];
	private boolean verbose = false;  // boolean to activate or not verbose
	private boolean clean = false;
	private boolean pitSelected = false;
	private WizardConfiguration wConf;
	private DSpotWizardPage2 page;
	private String[] amplifiers = {"StringLiteralAmplifier","NumberLiteralAmplifier","CharLiteralAmplifier",
			"BooleanLiteralAmplifier","AllLiteralAmplifiers","MethodAdd","MethodRemove","TestDataMutator",
			"StatementAdd",""};  // the possible amplifiers;
	
	// widgets
	private Text tx1;
	private Spinner spin;
	private Spinner spin1;
	//private Text amplText; 
	private List amplifiersList;
	private Combo combo1;
	private Button button;
	private Button button2;
	
	// Dialogs
	private DspotAdvancedOptionsDialog adv;
	//private CheckingDialog chDiag;
	
	// this is for the advanced dialog
	private String[] testCases;
	private String[] testMethods;
	private int r = 23;
	private int timeOut = 10000;
	private String casesToTest = "";
	private String pathPitResult = "";
	
	public DSpotWizardPage2(WizardConfiguration wConf) {
		super("Second page");
		setTitle("Second page");
		setDescription("Information about the execution");
		this.wConf = wConf;
		testCases = wConf.getTestCases();
		testMethods = wConf.getTestMethods();
		page = this;
		adv = new DspotAdvancedOptionsDialog(new Shell(),pitSelected,wConf.getProjectPath(),testCases,testMethods,this);
	} // end of the constructor
	
	@Override
	public void createControl(Composite parent) {
		
		// create the composite
		Composite composite = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();    // the layout of the composite
		int n = 4;
		layout.numColumns = n;
		composite.setLayout(layout);
		
		// First row (1,x) number of iterations
		Label lb1 = new Label(composite,SWT.NONE);  // A label in (1,1)
		lb1.setText("Number of iterations :  ");
		
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 20;
		gd.horizontalSpan = 2;
		
		spin = new Spinner(composite,SWT.NONE); // A spinner in  (1,2)
		spin.setMinimum(1);                             // for the number of iterations i
		spin.setLayoutData(gd);
		spin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Comp[0] = spin.getSelection() > 0;  // we need a positive number of iterations
				MyStrings[0] = (new Integer(spin.getSelection())).toString();
				setPageComplete(Comp[0] && Comp[1]);				
				
			}
		});
		
		Label space1 = new Label(composite,SWT.NONE);    // (1,3)
		space1.setText("");
		gd = new GridData(SWT.FILL,SWT.FILL,false,false);
		space1.setLayoutData(gd);
		
		// Second row (2,x) execution classes
		Label lb2 = new Label(composite,SWT.NONE);   // A label in (2,1)
		lb2.setText("Execution classes :  ");
				
		tx1 = new Text(composite,SWT.BORDER);  // A text in (2,2) for the execution classes
		tx1.setText("");
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.verticalIndent = 8;
		gd.horizontalSpan = 2;
		tx1.setLayoutData(gd);
		tx1.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
			Comp[1] = !tx1.getText().isEmpty();	  // look at the "!"
			MyStrings[1] = tx1.getText();
			setPageComplete(Comp[0] && Comp[1]);
			}
		});   // end of the Key listener
		
     tx1.addSegmentListener(new SegmentListener() {  // if the user copy-pastes the key listener dosn't detect it
    	 @Override
    	 public void getSegments(SegmentEvent e) {
 			Comp[1] = !tx1.getText().isEmpty();	  // look at the "!"
 			MyStrings[1] = tx1.getText();
 			setPageComplete(Comp[0] && Comp[1]); 
    	 }
     });  // end of the segment listener
     
	 
     Button fileButton = new Button(composite,SWT.PUSH); // A button in (2,3), it opens the file dialog
     fileButton.setText("Select tests");
     fileButton.addSelectionListener(new SelectionAdapter() {
    	 @Override
    	 public void widgetSelected(SelectionEvent e) {
    		 
 			try {
				String selection = showElementTreeSelectionDialog2(wConf.getPro(),wConf.getTheWindow());
				if(tx1.getText()==null||tx1.getText().isEmpty()) {
					if(!tx1.getText().contains(selection)||!selection.contains(tx1.getText())) tx1.setText(selection);  // this if is to avoid repetitions
				}else if(!selection.isEmpty()) { // if !selection.isEmpty() to avoid put ; or : while the dialog is canceled
					if(!tx1.getText().contains(selection)||!selection.contains(tx1.getText())) {
						tx1.setText(tx1.getText()+WizardConfiguration.getSeparator()+selection); }
					}
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			} 
    		 

    	 }
     }); // end of the selection listener
     
     		
		// Third row (3,x) Method
		Label lb3 = new Label(composite,SWT.NONE);   // A label in (3,1)
		lb3.setText("Amplifier :  ");
		
		amplifiersList = new List(composite,SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
	    gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = n-1;
		amplifiersList.setLayoutData(gd);
		for(int i = 0; i < amplifiers.length -1; i++) {
			amplifiersList.add(amplifiers[i]);
		}
		
		
	    amplifiersList.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		String[] selection = amplifiersList.getSelection();
	    		MyStrings[2] = selection[0];
	    		for(int i = 1; i < selection.length; i++) {
	    			MyStrings[2] = MyStrings[2] + WizardConfiguration.getSeparator() + selection[i];
	    		}
	    	}
	    });
	    
	    // row five (5,x)
	    Label lb5 = new Label(composite,SWT.NONE); // A label in (5,1)
	    lb5.setText("Test Criterion : ");
	    
	    combo1 = new Combo(composite,SWT.BORDER);  // combo for the test criterion in (4,2)
	    gd = new GridData(SWT.FILL,SWT.FILL,true,false);
	    gd.horizontalSpan = n-1;
	    combo1.setLayoutData(gd);  // setting the criterions in the combo
	    combo1.add("PitMutantScoreSelector"); combo1.add("ExecutedMutantSelector");
	    combo1.add("CloverCoverageSelector"); combo1.add("BranchCoverageTestSelector");
	    combo1.add("JacocoCoverageSelector"); combo1.add("TakeAllSelector");
	    combo1.add("ChangeDetectorSelector"); combo1.add("");
	    combo1.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		
	    		MyStrings[3] = combo1.getText();
	   
	    		
	    	}
	    }); // end of the selection listener
	    
	    // five row (5,x)
	    Label lb6 = new Label(composite,SWT.NONE);  // A label in (5,1)
	    lb6.setText("Max test amplified : ");
	    
	    spin1 = new Spinner(composite,SWT.BORDER);
	    spin1.setMinimum(50); spin1.setIncrement(50); spin1.setMaximum(4000); spin1.setSelection(200);
	    spin1.setLayoutData(gd);
	    spin1.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		
	    		MyStrings[4] = (new Integer(spin1.getSelection())).toString();
	    		
	    	}
	    }); // end of the selection listener

	    // sixth row (6,x)
	    Label lb7 = new Label(composite,SWT.NONE); // A label in (6,1)
	    lb7.setText("Verbose ");
	    
	    button = new Button(composite,SWT.CHECK);  // check button in (6,2)
	    button.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		verbose = button.getSelection();
	    	}
	    }); // end of the selection listener
	    
	    Label space = new Label(composite,SWT.NONE);
	    space.setText("");
	    
	    
	    Link link = new Link(composite,SWT.NONE);  // this link in (6,4) open the dialog with the advanced options
	    link.setText("<A>Dspot advanced options</A>");
	    link.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		pitSelected = combo1.getText() == "" || combo1.getText().contains("PitMutantScoreSelector");
	    		adv = new DspotAdvancedOptionsDialog(new Shell(),pitSelected,wConf.getProjectPath(),testCases,testMethods,page);
	    		adv.open();
	    	}
	    }); // end of the selection listener
	    
	    // seventh row (7,x)
	    Label lb8 = new Label(composite,SWT.NONE); // A label in (6,1)
	    lb8.setText("clean ");
	    
	    button2 = new Button(composite,SWT.CHECK);  // check button in (6,2)
	    button2.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		clean = button.getSelection();
	    	}
	    }); // end of the selection listener
	    
		// required to avoid an error in the System
		setControl(composite);
		setPageComplete(false);	
	}  // end of create Control
	/**
	 * Method to create and show a dialog to select test classes
	 * @param jProject : the selected project
	 * @param window : the active workbench window
	 * @return a string with the class selected by the user
	 * @throws JavaModelException
	 */
    private String showElementTreeSelectionDialog2(IJavaProject jProject, IWorkbenchWindow window) throws JavaModelException {
        Class<?>[] acceptedClasses= new Class[] { IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class };
        TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, true) {
            @Override
            public boolean isSelectedValid(Object element) {
                
                    if (element instanceof ICompilationUnit) {
                        return true;
                    }
                    return false;
                
            }
        };

        acceptedClasses= new Class[] { IJavaModel.class, IPackageFragmentRoot.class, IJavaProject.class, IJavaElement.class, ICompilationUnit.class };
        ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
            @Override
            public boolean select(Viewer viewer, Object parent, Object element) {
                if (element instanceof IJavaProject) {
                    if (!((IJavaProject) element).getElementName().equals(jProject.getElementName())) {
                        return false;
                    }
                }
                if (element instanceof IPackageFragment) {
                    try {
                        return containsTestClasses ((IPackageFragment)element);
                    } catch (JavaModelException e) {
                        JavaPlugin.log(e.getStatus()); // just log, no UI in validation
                        return false;
                    }
                }
                if (element instanceof JarPackageFragmentRoot) {
                    return false;
                }
                return super.select(viewer, parent, element);
            }

            private boolean containsTestClasses(IPackageFragment element) throws JavaModelException {
                boolean result = false;
                if (!(element instanceof JarPackageFragmentRoot)) {
                    for (IJavaElement child:element.getChildren()) {
                        if (child instanceof ICompilationUnit && isTestClass (child)) {
                            result = true;
                            break;
                        }
                    }
                }
                return result;
            }

            private boolean isTestClass(IJavaElement child) {
                // Detect test class by inspecting JUnit annotations
            	try {
					return wConf.lookForTest(wConf.getqName(child.getElementName()));
				} catch (JavaModelException e) {
					e.printStackTrace(); return child.getElementName().toLowerCase().contains("test");
				}
            }
        };
        
        IWorkspaceRoot fWorkspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
        IJavaElement initElement = jProject.getPackageFragmentRoots()[0];
        
        StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
        ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
        ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(window.getShell(), labelProvider, provider);
        dialog.setValidator(validator);
        dialog.setComparator(new JavaElementComparator());
        dialog.setTitle(" Select a test-class ");
        dialog.setMessage(" Select a class file ");
        dialog.addFilter(filter);
        dialog.setInput(JavaCore.create(fWorkspaceRoot));
        dialog.setInitialSelection(initElement);
        dialog.setHelpAvailable(false);
        String selection = "";
        if (dialog.open() == Window.OK) {
            Object[] results = dialog.getResult();
            for(Object ob : results) {
            	if(ob instanceof ICompilationUnit) { 
            		if(!selection.isEmpty()) {
             selection = selection + WizardConfiguration
            		 .getSeparator() + wConf.getqName(((ICompilationUnit)ob).getElementName());}
            		else{ selection = wConf.getqName(((ICompilationUnit)ob).getElementName()); }}
            }
        }
        return selection;
    }

    public void refresh() {
	 ILaunchConfiguration config = wConf.getCurrentConfiguration();
	 String argument = null;
	 try {
	 argument = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,"");
	} catch (CoreException e) {
		e.printStackTrace();
	}
   if(!argument.isEmpty()) {
   	String myFragment = argument
   			.substring(argument.indexOf("-i ")+3,argument.indexOf(" -t"));
   	spin.setSelection(Integer.parseInt(myFragment));
   	myFragment = argument
   			.substring(argument.indexOf("-t ")+3,argument.indexOf(" -a"));
   	tx1.setText(myFragment);
   	myFragment = argument
   			.substring(argument.indexOf("-a ")+3,argument.indexOf(" -s"));
   	ArrayList<Integer> indices = new ArrayList<Integer>(1);
   	for(int i = 0; i < amplifiers.length; i++) {
   		if(myFragment.contains(amplifiers[i])) {
   			indices.add(new Integer(i));
   		}
   	}
   	int[] theIndices = new int[indices.size()];
   	for(int i = 0; i < indices.size(); i++) {
   		theIndices[i] = indices.get(i).intValue();
   	}
   	amplifiersList.setSelection(theIndices);;
   	//amplText.setText(myFragment);
   	myFragment = argument
   			.substring(argument.indexOf("-s ")+3,argument.indexOf(" -g"));
   	combo1.setText(myFragment);
   	myFragment = argument.substring(argument.indexOf("-g ")+3);
   	if(myFragment.contains("-")) {
   	myFragment = myFragment.substring(0,myFragment.indexOf("-"));
   	} 
   	myFragment = myFragment.replaceAll(" ", "");
   	spin1.setSelection(Integer.parseInt(myFragment));
   	if(argument.contains("-r ")) {
      myFragment = argument.substring(argument.indexOf("-r ")+3);
      myFragment = myFragment.substring(0,myFragment.indexOf("-"));
      myFragment = myFragment.replaceAll(" ", "");
      r = Integer.parseInt(myFragment);
   	}
   	if(argument.contains("-v ")) {
        myFragment = argument.substring(argument.indexOf("-v ")+3);
        myFragment = myFragment.substring(0,myFragment.indexOf("-"));
        myFragment = myFragment.replaceAll(" ", "");
        timeOut = Integer.parseInt(myFragment);
     	}
   	if(argument.contains("-c ")) {
        myFragment = argument.substring(argument.indexOf("-c ")+3);
        myFragment = myFragment.substring(0,myFragment.indexOf("-"));
        casesToTest = myFragment;
     	}
   	if(argument.contains("-m ")) {
        myFragment = argument.substring(argument.indexOf("-m ")+3);
        myFragment = myFragment.substring(0,myFragment.indexOf("-"));
        pathPitResult = myFragment;
     	}
   	button.setSelection(argument.contains("--verbose"));
   	button2.setSelection(argument.contains("--clean"));
   }
    }
	
	/*
	 *  public methods to return the information set by the user
	 */

	/**
	 * getMyStrings
	 * @return a String array with the information set by the user
	 */
	public String[] getMyStrings() {
		if(MyStrings[0] == null) {   // if the spinner hasn't be touch
			MyStrings[0] = "1"; // then use the default value
		}
		if(MyStrings[2] == null) {
			MyStrings[2] = "None";
		}
		if(MyStrings[3] == null || MyStrings[3] == "") {
			MyStrings[3] = "PitMutantScoreSelector"; // default
		}
		if(MyStrings[4] == null) {
			MyStrings[4] = "200";
		}
		return MyStrings;
	}
	
	/**
	 * getVerbose
	 * @return a boolean true if the verbose check button is selected
	 */
	public boolean getVerbose() {
		return verbose;
	}
	
	/**
	 * getClean
	 * @return a boolean true if the clean check button is selected
	 */
	public boolean getClean() {
		return clean;
	}
	
	/**
	 * getAdvparameters
	 * @return a string array with the information set in the advanced options dialog
	 */
	public String[] getAdvparameters() {
		return adv.getAdvParameters();
	}
	public int getRandomSeed() {
		return r;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public String getCases() {
		return casesToTest;
	}
	public String getPathPitResult() {
		return pathPitResult;
	}
	 @Override
	 public void performHelp() {
		 String[] myText = {"First spinner, number of amplification iterations : A larger number may help to kill more mutants but it has an impact on the execution time",
		"First text, test-classes to use","the option verbose prints more information about the process in the console",
		"the option clean removes the out dirctory if exists, else it will append the results to the exist files",
		"","The link DSpot advanced options opens a dialog to set the time value of degenerated test (ms), the randomSeed, the MAVEN_HOME ",
		"and the path to the .csv of the original result of Pit test, (this only avaiable if the test criterion is PitMutantScoreSelector)",""};
		 DspotWizardHelpDialog info = new DspotWizardHelpDialog(new Shell()," This page contains the information to execute DSpot ",myText);
		 info.open();
	    
	 }
	 
}
