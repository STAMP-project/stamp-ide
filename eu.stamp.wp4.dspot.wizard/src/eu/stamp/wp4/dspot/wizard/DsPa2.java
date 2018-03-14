package eu.stamp.wp4.dspot.wizard;

import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

import eu.stamp.wp4.dspot.dialogs.*;

/**
 * this class describes the second page of the DSpot wizard
 *
 */
@SuppressWarnings("restriction")
public class DsPa2 extends WizardPage {
	
	private boolean[] Comp = {true,false};  // this is to set page complete
	// [0] i : number of iterations, [1] execution test class, [2] Method, [3] test criterion,
	// [4] max test amplified
	private String[] MyStrings = new String[5];
	private boolean verbose = false;  // boolean to activate or not verbose
	private boolean clean = false;
	private boolean pitSelected = false;
	private WizardConf wConf;
	
	// Dialogs
	private AdvancedDialog adv;
	private CheckingDialog chDiag;
	//private ElementTreeSelectionDialog selDiag;
	
	// this is for the advanced dialog
	private String[] testCases;
	private String[] testMethods;
	
	public DsPa2(WizardConf wConf) {
		super("Second page");
		setTitle("Second page");
		setDescription("Information about the execution");
		this.wConf = wConf;
		testCases = wConf.getTestCases();
		testMethods = wConf.getTestMethods();
		adv = new AdvancedDialog(new Shell(),pitSelected,wConf.getProjectPath(),testCases,testMethods);
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
		
		Spinner spin = new Spinner(composite,SWT.NONE); // A spinner in  (1,2)
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
				
		Text tx1 = new Text(composite,SWT.BORDER);  // A text in (2,2) for the execution classes
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
     
     // preparing the file dialog
     /*
     String[] directions = wConf.getSources();
     boolean[] theTest = wConf.getIsTest();
     String testDirection = wConf.getProjectPath();
     for(int i = 0; i < directions.length; i++) {
    	 if(theTest[i]) {
     testDirection = testDirection+"/"+directions[i]; 
     break;  // very important
     }} // end of the for 
     final String testDirectionf = testDirection;*/
     
	 
     Button fileButton = new Button(composite,SWT.PUSH); // A button in (2,3), it opens the file dialog
     fileButton.setText("Select tests");
     fileButton.addSelectionListener(new SelectionAdapter() {
    	 @Override
    	 public void widgetSelected(SelectionEvent e) {
    		 
 			try {
				String selection = showElementTreeSelectionDialog2(wConf.getPro(),wConf.getTheWindow());
				if(tx1.getText()==null||tx1.getText()=="") {
					tx1.setText(selection);
				}else { tx1.setText(tx1.getText()+";"+selection); }
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			} 
    		 

    	 }
     }); // end of the selection listener
     
     		
		// Third row (3,x) Method
		Label lb3 = new Label(composite,SWT.NONE);   // A label in (3,1)
		lb3.setText("Amplifier :  ");
		
		String[] amplifiers = {"StringLiteralAmplifier","NumberLiteralAmplifier","CharLiteralAmplifier",
				"BooleanLiteralAmplifier","AllLiteralAmplifiers","MethodAdd","MethodRemove","TestDataMutator",
				"StatementAdd",""};  // the possible amplifiers
		
		Combo combo = new Combo(composite,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.horizontalSpan = n-1;
		combo.setLayoutData(gd);
		for(int i = 0; i < amplifiers.length -1; i++) {
			combo.add(amplifiers[i]);
		}

	    // fourth row
	    Label lb4 = new Label(composite,SWT.NONE);
	    lb4.setText("use several amplifiers : ");
	    
	    Text amplText = new Text(composite,SWT.BORDER);
	    amplText.setText("");
	    GridData amplTextData = new GridData(SWT.FILL,SWT.NONE,true,false);
	    amplTextData.horizontalSpan = n-2;
	    amplText.setLayoutData(amplTextData);
	    amplText.setEnabled(false);
	    
	    combo.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		
	    		MyStrings[2] = combo.getText();
	    		amplText.setText("");
	    		amplText.setEnabled(false);
	    	}
	    }); // end of the selection listener
	    
	    chDiag = new CheckingDialog(new Shell(),amplifiers," Select amplifiers ");  // preparing the dialog to select the amplifiers
	    
	    Button addAmplBt = new Button(composite,SWT.PUSH); 
	    addAmplBt.setText("Add");
	    addAmplBt.addSelectionListener(new SelectionAdapter(){
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		chDiag.open();
	    		String sr = chDiag.getSelection();
	    		if(sr != "") {
	    			amplText.setText(sr);
	    			MyStrings[2] =sr;
	    			combo.setText("");
	    			amplText.setEnabled(true);
	    		}
	    	}
	    });
	    
	    // row five (5,x)
	    Label lb5 = new Label(composite,SWT.NONE); // A label in (5,1)
	    lb5.setText("Test Criterion : ");
	    
	    Combo combo1 = new Combo(composite,SWT.BORDER);  // combo for the test criterion in (4,2)
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
	    
	    Spinner spin1 = new Spinner(composite,SWT.BORDER);
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
	    
	    Button button = new Button(composite,SWT.CHECK);  // check button in (6,2)
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
	    		adv = new AdvancedDialog(new Shell(),pitSelected,wConf.getProjectPath(),testCases,testMethods);
	    		adv.open();
	    	}
	    }); // end of the selection listener
	    
	    // seventh row (7,x)
	    Label lb8 = new Label(composite,SWT.NONE); // A label in (6,1)
	    lb8.setText("clean ");
	    
	    Button button2 = new Button(composite,SWT.CHECK);  // check button in (6,2)
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
	
	/*
	private void createTreeDialog() {
		  
		LabelProvider myLabelProvider = new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof IResource) { IResource resource = (IResource)element;
				return resource.getName();}
	        return element.toString();
			}
		};
		selDiag = new ElementTreeSelectionDialog(new Shell(),myLabelProvider,new BaseWorkbenchContentProvider());	
		selDiag.setInput(ResourcesPlugin.getWorkspace().getRoot());
		selDiag.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
	            if(element instanceof org.eclipse.core.internal.resources.File ) {
	      org.eclipse.core.internal.resources.File f =  (org.eclipse.core.internal.resources.File)element;
	      return f.getName().contains(".java");
	            }
	            if(element instanceof Project) {
	            	Project pro = (Project)element; 
	            	try {  IResource[] childs = pro.members();
	            	for(IResource myR: childs) { if(myR.getName().contains("src")) { return true; } }
					} catch (CoreException e) {e.printStackTrace(); return false;}
	            }
	            if(element instanceof Folder) {
	            Folder f = (Folder)element; 
	            if(f.getName().contains("src") || !(f.getParent() instanceof Project)) { return true; };          
	            }
	            return false;
			}
		});
		selDiag.setTitle("Select a test");
		selDiag.setAllowMultiple(true);
	}
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
        dialog.setTitle(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_title);
        dialog.setMessage(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_description);
        dialog.addFilter(filter);
        dialog.setInput(JavaCore.create(fWorkspaceRoot));
        dialog.setInitialSelection(initElement);
        dialog.setHelpAvailable(false);
    
        if (dialog.open() == Window.OK) {
            Object element= dialog.getFirstResult();
            Object[] results = dialog.getResult();
            for(Object ob : results) {
            	if(ob instanceof ICompilationUnit) { 
             return wConf.getqName(((ICompilationUnit)ob).getElementName()); }
            }
        }
        return "";
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
	 @Override
	 public void performHelp() {
		 String[] myText = {"First spinner, number of amplification iterations : A larger number may help to kill more mutants but it has an impact on the execution time",
		"First text, test-classes to use","the option verbose prints more information about the process in the console",
		"the option clean removes the out dirctory if exists, else it will append the results to the exist files",
		"","The link DSpot advanced options opens a dialog to set the time value of degenerated test (ms), the randomSeed, the MAVEN_HOME ",
		"and the path to the .csv of the original result of Pit test, (this only avaiable if the test criterion is PitMutantScoreSelector)",""};
		 BigDialog info = new BigDialog(new Shell()," This page contains the information to execute DSpot ",myText);
		 info.open();
	 }
	 
}
