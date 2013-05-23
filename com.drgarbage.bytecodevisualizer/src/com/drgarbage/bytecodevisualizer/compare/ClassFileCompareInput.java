package com.drgarbage.bytecodevisualizer.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.internal.CompareContentViewerSwitchingPane;
import org.eclipse.compare.internal.CompareMessages;
import org.eclipse.compare.internal.CompareUIPlugin;
import org.eclipse.compare.internal.ViewerDescriptor;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PlatformUI;

import com.drgarbage.bytecodevisualizer.BytecodeVisualizerConstants;
import com.drgarbage.bytecodevisualizer.BytecodeVisualizerPlugin;
import com.drgarbage.core.img.CoreImg;

/**
 * A compare operation input which can present its results in a compare editor.
 * 
 * @author Sergej Alekseev
 * @version $Revision$
 * $Id$
 */
public class ClassFileCompareInput extends CompareEditorInput {

    /** 
     * The element displayed on the left side of the viewer. 
     */
    protected CompareElement left;
    
    /** 
     * The element displayed on the right side of the viewer. 
    */
    protected CompareElement right;

    /**
     * Reference to the editor instance. 
     */
    protected IReusableEditor compareEditor;


    /**
     * @param left the element displayed on the left side of the viewer.
     * @param right the element displayed on the right side of the viewer.
     * @param cc compare configuration
     */
    public ClassFileCompareInput(final CompareElement left, final CompareElement right, CompareConfiguration cc) {
    	super(cc);
        this.left = left;
        this.right = right;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.compare.CompareEditorInput#getTitleImage()
     */
    public Image getTitleImage() {
    	ImageRegistry reg = BytecodeVisualizerPlugin.getDefault().getImageRegistry();
    	if(reg == null){
    		return super.getTitleImage();
    	}

    	ImageDescriptor descr = reg.getDescriptor(BytecodeVisualizerConstants.IMG16E_COMPARE_ACTION);
    	if(descr == null){
    		return super.getTitleImage();
    	}
    	return descr.createImage();
    }

    /**
     * Switch viewer of the editor.
     * @param contentType
     */
    protected void switchViewer(String contentType) {

        left.discardBuffer();
        left.setType(contentType);

        right.discardBuffer();
        right.setType(contentType);

        CompareConfiguration cc = new CompareConfiguration();
        CompareUI.reuseCompareEditor(new ClassFileCompareInput(left, right, cc), compareEditor);

    }
    
    /* (non-Javadoc)
     * @see org.eclipse.compare.CompareEditorInput#prepareInput(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected Object prepareInput(final IProgressMonitor monitor)
        throws InterruptedException {
        if (right == null || left == null) {
            return null;
        }

        try {
            CompareConfiguration cc = getCompareConfiguration();
            cc.setLeftLabel(left.getName());
            cc.setRightLabel(right.getName());

            setTitle(ClassFileMergeViewer.CLASS_FILE_MERGEVIEWER_TITLE
                + left.getElementName()+ " - " + right.getElementName()); //$NON-NLS-1$
            Differencer differencer = new Differencer();
            monitor.beginTask("Class file comparing...", 30); //$NON-NLS-1$
            IProgressMonitor sub = new SubProgressMonitor(monitor, 10);
            try {
                sub.beginTask("Class File comparing...", 100); //$NON-NLS-1$

                return differencer.findDifferences(false, sub, null, null, left, right);
            } finally {
                sub.done();
            }
        } catch (OperationCanceledException e) {
            throw new InterruptedException(e.getMessage());
        } finally {
            monitor.done();
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.compare.CompareEditorInput#findContentViewer(org.eclipse.jface.viewers.Viewer, 
     * 		org.eclipse.compare.structuremergeviewer.ICompareInput, org.eclipse.swt.widgets.Composite)
     */
	@SuppressWarnings("restriction")
    public Viewer findContentViewer(Viewer newViewer, ICompareInput input, Composite parent) {
    	Viewer viewer = null;    		
    	
    	if(left.getType().equals("class") && 
    			right.getType().equals("class")){

			ViewerDescriptor[] descriptors = 
    				CompareUIPlugin.getDefault().findContentViewerDescriptor(newViewer, input, getCompareConfiguration());

    		for(ViewerDescriptor vd: descriptors){
    			String s = vd.getExtension();
    			if(s.equals("class, class_drgarbage")){ //$NON-NLS-1$
    				setContentViewerDescriptor(vd);	
    				break;
    			}
    		}
    		viewer = super.findContentViewer(newViewer, input, parent);
    	}
    	else{
    		viewer = super.findContentViewer(newViewer, input, parent);
    	}

    	return viewer;
    }
 
    /* (non-Javadoc)
     * @see org.eclipse.compare.CompareEditorInput#createContents(org.eclipse.swt.widgets.Composite)
     */
    public Control createContents(final Composite parent) {
        Object obj = parent.getData();
        if(obj == null) {
            obj = parent.getParent().getData();
        }

        if(obj instanceof IReusableEditor){
            compareEditor = (IReusableEditor)obj;
        }

        /* create default content of the dialog */
        Control control = super.createContents(parent);
        
        /* modify content and add own elements */
        createOwnContent(control);
        
        return control;
    }
    
    private void createOwnContent(Control control){
        if(control instanceof Splitter){
        	Splitter sp = (Splitter) control;
        	Control[] children = sp.getChildren();
        	
        	final CompareContentViewerSwitchingPane ccvsp = (CompareContentViewerSwitchingPane)children[1];
        	Composite topLeft1 = (Composite)ccvsp.getTopLeft();
        	
        	Composite topCenter =(Composite) ccvsp.getTopCenter();
        	Composite topRight =(Composite) ccvsp.getTopRight();
        	
        	/* make the menu invisible */
        	topLeft1.getChildren()[0].setVisible(false);
        	topLeft1.getChildren()[1].setVisible(false);

//        	cp.getChildren()[0].dispose();
//        	cp.getChildren()[1].dispose();

        	/* create own menu */
        	ToolBarManager toolBarManager2 = CompareViewerPane
        			.getToolBarManager(ccvsp);

        	toolBarManager2.add(new Separator());        	
        	toolBarManager2.add(new SwitchAction(ccvsp.getShell()));

        	toolBarManager2.update(true);
        }
    }
    
    /**
     * Simple switch action.
     */
    class SwitchAction extends Action{
    	Shell shell;
    	
    	SwitchAction(Shell s){
    		shell = s;
    		//TODO: new icon
    		setImageDescriptor(PlatformUI.getWorkbench().
        			getSharedImages().getImageDescriptor("IMG_LCL_VIEW_MENU"));
    	}
    	
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run(){
			showMenu(shell, null);
		}
	}
    
    boolean menuShowing = false;
    
	private void showMenu(Shell shell, ToolBar toolBar) {
		if (menuShowing)
			return;
		menuShowing= true;
		

		// 1. create
		final Menu menu = new Menu(shell, SWT.POP_UP);

		// add default
		String label = CompareMessages.CompareContentViewerSwitchingPane_defaultViewer;
		MenuItem defaultItem = new MenuItem(menu, SWT.RADIO);
		defaultItem.setText(label);
		defaultItem.addSelectionListener(createSelectionListener());

		new MenuItem(menu, SWT.SEPARATOR);
		
		// add others
		itemJavaSourceCompare = new MenuItem(menu, SWT.RADIO);
		final SelectionListener selListJavaSource = createSelectionListener();
		itemJavaSourceCompare.addSelectionListener(selListJavaSource);
		itemJavaSourceCompare.setText("Java Source Compare");
		Image img = JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT);
		itemJavaSourceCompare.setImage(img);
		
		
		itemClassFileCompare = new MenuItem(menu, SWT.RADIO);
		final SelectionListener selListClassFile = createSelectionListener();
		itemClassFileCompare.addSelectionListener(selListClassFile);
		itemClassFileCompare.setText(ClassFileMergeViewer.CLASS_FILE_MERGEVIEWER_TITLE);
		itemClassFileCompare.setImage(CoreImg.classfile_compare_16x16.createImage());
		
		if(left.getType().equals(CompareElement.TYPE_BYTECODE)){
			itemClassFileCompare.setEnabled(false);
			itemClassFileCompare.setSelection(true);
		}
		else{
			itemJavaSourceCompare.setEnabled(false);
			itemJavaSourceCompare.setSelection(true);
		}
		
		// 2. show
		menu.setVisible(true);
		
		// 3. dispose on close
		menu.addMenuListener(new MenuAdapter() {
			public void menuHidden(MenuEvent e) {
				menuShowing= false;
				e.display.asyncExec(new Runnable() {
					public void run() {
						itemJavaSourceCompare.removeSelectionListener(selListJavaSource);
						itemClassFileCompare.removeSelectionListener(selListClassFile);
						menu.dispose();
					}
				});
			}
		});
	}
    
	MenuItem itemJavaSourceCompare;
	MenuItem itemClassFileCompare;
	
	private SelectionListener createSelectionListener() {
		return new SelectionListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				MenuItem mi = (MenuItem) e.widget;
				if (mi.getSelection()) {
					
					if(mi.getText().equals(CompareMessages.CompareContentViewerSwitchingPane_defaultViewer)){
						if(left.getType().equals(CompareElement.TYPE_BYTECODE)){
							switchViewer(CompareElement.TYPE_JAVA);	
						}
						return;
					}

					if(mi.getText().equals(ClassFileMergeViewer.CLASS_FILE_MERGEVIEWER_TITLE)){
						switchViewer(CompareElement.TYPE_BYTECODE);
					}
					else{
						switchViewer(CompareElement.TYPE_JAVA);
					}
				}
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent arg0) {
				/* nothing to do */
			}
		};
	}
 
}
