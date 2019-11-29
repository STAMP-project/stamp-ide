/*******************************************************************************
 * Copyright (c) 2018 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ricardo José Tejada García (Atos) - main developer
 * Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.botsing.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Spinner;

/**
 * This implementation of Botsing property represent properties of
 * kind integer, the associated widget is a spinner
 */
public class BotsingSpinnerProperty extends AbstractBotsingProperty {
	
	private Spinner spinner;
	
	private final int step,minimun,digits;
	private int maximun;
	
	
	public BotsingSpinnerProperty(String defaultValue,
			String key,String name,boolean compulsory) {
		this(defaultValue,key,name,1,1,0,compulsory);
	}
	
	public BotsingSpinnerProperty(String defaultValue,
			String key,String name,int step,int minimun,
			int maximun,boolean compulsory) {
		this(defaultValue,key,name,step,minimun,maximun,compulsory,0);
	}
	
	public BotsingSpinnerProperty(String defaultValue,
			String key,String name,int step,int minimun,
			int maximun,boolean compulsory,int digits) {
		
		super(defaultValue,key,name,compulsory,true);
		this.step = step;
		this.minimun = minimun;
		this.maximun = maximun;
		this.digits = digits;
	}

	@Override
	public String getData() { return data; }

	@Override
	protected void setData(String data) {
		super.data = data;
		if(spinner == null) return;
		if(spinner.isDisposed()) return;
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				   String processedData = data.replaceAll(",","\\.").replaceAll("0.","").replaceAll("\\.",""); // TODO check
				   spinner.setSelection(Integer.parseInt(processedData));
			}
		});
		
	}
	@Override
	public void createControl(Composite composite) {
        
        super.createControl(composite);
        
        spinner = new Spinner(composite,SWT.BORDER);
        spinner.setMinimum(minimun);
        if(maximun > minimun + 1) spinner.setMaximum(maximun);
        spinner.setIncrement(step);
        spinner.setSelection(Integer.parseInt(data));
        if(digits != 0) spinner.setDigits(digits);
        
        GridData gridData = 
        		new GridData(SWT.FILL,SWT.FILL,true,false);
        int n = ((GridLayout)composite.getLayout()).numColumns;
        gridData.horizontalSpan = n -1;
       spinner.setLayoutData(gridData);

       spinner.addSelectionListener(new SelectionAdapter() {
    	   @Override
    	   public void widgetSelected(SelectionEvent e) {
    		   spinnerSelected();
    	   }
       });
       spinner.notifyListeners(SWT.Selection,new Event());
	}
	
	public void setMaximun(int maximun) {
		this.maximun = maximun;
		if(spinner != null)if(!spinner.isDisposed()) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if(spinner.getSelection() > maximun) 
						spinner.setSelection(maximun);
					spinner.setMaximum(maximun);
				}
			});
		}
	}
	
	protected void spinnerSelected() {
		double number = new Double(spinner.getSelection());
		number = number/(Math.pow(10,digits));
		setData(String.valueOf(number).replaceAll(",","\\."));
		callListeners();
	}
	public Spinner getSpinner() {
		return spinner;
	}
}
