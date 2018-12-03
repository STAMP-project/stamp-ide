package eu.stamp.eclipse.botsing.launch.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationDialog;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.richclientgui.toolbox.validation.IFieldErrorMessageHandler;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

import eu.stamp.eclipse.botsing.constants.BotsingPluginConstants;
import eu.stamp.eclipse.botsing.invocation.InputManager;
import eu.stamp.eclipse.botsing.launch.BotsingLaunchInfo;
import eu.stamp.eclipse.botsing.launch.BotsingPartialInfo;
import eu.stamp.eclipse.botsing.listeners.IBotsingPropertyListener;
import eu.stamp.eclipse.botsing.properties.AbstractBotsingProperty;
import eu.stamp.eclipse.botsing.properties.BotsingSpinnerProperty;
import eu.stamp.eclipse.botsing.properties.ClassPathProperty;
import eu.stamp.eclipse.botsing.properties.StackTraceProperty;
import eu.stamp.eclipse.botsing.properties.TestDirectoryProperty;
import eu.stamp.eclipse.text.validation.StampTextFieldErrorHandler;
import eu.stamp.eclipse.general.validation.IValidationPage;

@SuppressWarnings("restriction")
public class BotsingLaunchConfigurationTab 
             extends AbstractLaunchConfigurationTab implements IValidationPage {

	private final List<AbstractBotsingProperty> botsingProperties;
	
	private boolean dirty;
	private boolean save;
	
	BotsingLaunchConfigurationTab() {
		super();
		botsingProperties = 
				new LinkedList<AbstractBotsingProperty>();
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite composite = new Group(parent,SWT.BORDER);
		setControl(composite);
		
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(composite);
		
		// Validation
		IFieldErrorMessageHandler errorHandler = 
				new StampTextFieldErrorHandler(this);
		StringValidationToolkit kit = 
				new StringValidationToolkit(SWT.LEFT | SWT.TOP,1,true);
		kit.setDefaultErrorMessageHandler(errorHandler);
		
		//Field for the tests directory
		TestDirectoryProperty testDirectory = 
				new TestDirectoryProperty("","-Dtest_dir","Test directory : ",kit);
		testDirectory.createControl(composite,false); // no only read
		botsingProperties.add(testDirectory);
		
		// Field for the log directory
		StackTraceProperty stackProperty = 
				new StackTraceProperty("","-Dcrash_log","Log file : ",kit);
		stackProperty.createControl(composite);
		botsingProperties.add(stackProperty);
		
		// Spinner for the frame level
		BotsingSpinnerProperty frameLevel = 
	    new BotsingSpinnerProperty("2","-Dtarget_frame","Frame level : ",true);
		frameLevel.createControl(composite);
		botsingProperties.add(frameLevel);
	    
		// Field for the classpath
		ClassPathProperty classPathProperty = 
				new ClassPathProperty("","-projectCP","Class Path : ",kit);
		classPathProperty.createControl(composite);
		botsingProperties.add(classPathProperty);
		
		addListenerToAll();
	}
	
	private void addListenerToAll() {
		
		IBotsingPropertyListener listener =
				new IBotsingPropertyListener(){
			@Override
			public void activate() {
				dirty = true;
				save = true;
				LaunchConfigurationDialog.
				getCurrentlyVisibleLaunchConfigurationDialog().
				updateButtons();
			}
		};
		
		for(AbstractBotsingProperty property : botsingProperties)
			property.addPropertyListener(listener);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// there is not a default configuration	
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		ILaunchConfigurationWorkingCopy copy;
		try {
			copy = configuration.getWorkingCopy();
			for(AbstractBotsingProperty property : botsingProperties)
				property.load(copy);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, 
				BotsingPluginConstants.BOTSING_MAIN);
		
		for(AbstractBotsingProperty property : botsingProperties)
			property.appendToConfiguration(configuration);
		
		List<AbstractBotsingProperty> defaultProperties =
				getDefaultProperties();
		
        for(AbstractBotsingProperty property : defaultProperties)
        	property.appendToConfiguration(configuration);
        
        List<BotsingPartialInfo> infos = 
        		new LinkedList<BotsingPartialInfo>();
        infos.add(new BotsingPartialInfo(botsingProperties));
        infos.add(new BotsingPartialInfo(defaultProperties));
        
        BotsingLaunchInfo info = new BotsingLaunchInfo(infos);
        String[] command = info.getCommand();
        
        String userDir = "";
		for(String sr : command)if(sr.contains("crash_log")) {
			userDir = sr.substring(sr.lastIndexOf("=")+1);
			break;
		}
		// Windows (a path can contain both \\ and /)
		if(userDir.contains("\\")) {
			int n = userDir.lastIndexOf("\\");
			if(userDir.contains("/"))
				if(userDir.lastIndexOf("/") > n) n = userDir.lastIndexOf("/");
			userDir = userDir.substring(0,n);
		}
		else if(userDir.contains("/")) userDir = userDir.substring(0,userDir.lastIndexOf("/"));
		
		/*
		 *  Avoid file not found exception
		 */
		//System.out.println(userDir);
		File config = new File(userDir +
				"/src/main/java/eu/stamp/botsing");
		
		if(!config.exists()) config.mkdirs();
		config = new File(config.getAbsolutePath()
				+  "/" + "config.properties");
		if(!config.exists())
			try {
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				userDir);
		
		// TODO check
		String line = (new InputManager(command).serializeToString());
		
		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, 
				line);		
	}

	@Override
	public String getName() {
		return "Botsing launch configuration tab";
	}
	
	@Override
	public boolean canSave() {
		return save;
	}
	@Override
	public boolean isDirty() {
		return dirty;
	}
    
	private List<AbstractBotsingProperty> getDefaultProperties() {
		 
		 List<AbstractBotsingProperty> defaultProperties =
				 new LinkedList<AbstractBotsingProperty>();
		 
		 addDefaultProperty("100","-Dpopulation","Population : ",10,20,4000,defaultProperties);
         addDefaultProperty("1800","-Dsearch_budget","Search Budget : ",100,800,80000,defaultProperties);
         addDefaultProperty("30","-Dmax_recursion","Max recursion : ",5,5,1000,defaultProperties);
	
         return defaultProperties;
	}
	
	private void addDefaultProperty(String defaultValue,String key,
    		String name,int step,int minimun,int maximun,
    		List<AbstractBotsingProperty> list) {
		
    	BotsingSpinnerProperty property = 
    			new BotsingSpinnerProperty(defaultValue,
                          key,name,step,minimun,maximun,false);
    	
    	list.add(property);	
	}

	@Override
	public void error(String arg) { setErrorMessage(arg); }

	@Override
	public void message(String arg, int arg1) {
		
	}

	@Override
	public void cleanError() {
		save = true;
	}
}
