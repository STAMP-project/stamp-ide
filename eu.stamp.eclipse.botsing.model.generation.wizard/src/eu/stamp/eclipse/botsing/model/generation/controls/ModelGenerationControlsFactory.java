package eu.stamp.eclipse.botsing.model.generation.controls;

import java.util.Map;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ModelGenerationControlsFactory {
	
	protected String labelText;
	
	protected String tooltip;
	
	protected Map<String,String> map;
	
	protected String key;
	
	public ModelGenerationControlsFactory() {}
	
	public static ModelGenerationControlsFactory getFactory() { 
		return new ModelGenerationControlsFactory();
	}
	
	public ModelGenerationControlsFactory setLabelText(String labelText) {
		this.labelText = labelText;
		return this;
	}
	
	public ModelGenerationControlsFactory setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}
	
	public ModelGenerationControlsFactory setKey(String key) {
		this.key = key;
		return this;
	}
	
	public ModelGenerationControlsFactory setMap(Map<String,String> map) {
		this.map = map;
		return this;
	}
	
	public Text createText(Composite composite) {
		createLabel(composite);
		int n = ((GridLayout)composite.getLayout()).numColumns - 1;
		Text text = new Text(composite,SWT.BORDER);
		GridDataFactory.fillDefaults().span(n,1).grab(true,false).applyTo(text);
		if(map.get(key) != null) text.setText(map.get(key));
		text.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent event) {
			   map.put(key,text.getText());
			}	
		});
		return text;
	}
	
	private void createLabel(Composite composite) {
		Label label = new Label(composite,SWT.NONE);
		label.setText(labelText);
		if(tooltip != null) label.setToolTipText(tooltip);
	}

}
