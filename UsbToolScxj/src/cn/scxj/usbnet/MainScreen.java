package cn.scxj.usbnet;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.scxj.utils.SWTResourceManager;
import cn.scxj.utils.Utils;

public class MainScreen {

	private Shell shlusb;
	
	private static org.eclipse.swt.widgets.List loglist;
	private Button connectButton;
	private Button disconnectButton;

	private static MainScreen mainScreen;
	private Text portText;
	public static int port = 10086;
	private Composite advComposite;
	private boolean advmode = false; 
	private Composite baseComposite;
	private CLabel statusLabel;
	private CLabel statusImg;
	private MainScreen() {
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			MainScreen window = new MainScreen();
			MainScreen window = MainScreen.getMainScreen();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlusb.open();
		shlusb.layout();
		while (!shlusb.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static MainScreen getMainScreen() {
		if (mainScreen == null) {
			mainScreen = new MainScreen();
		}
		return mainScreen;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlusb = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN);
		shlusb.setSize(400, 360);
		shlusb.setText("USB共享上网工具");
		shlusb.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
            	MessageManager.stopManager();
            	MessageManager.stopadb();
            	System.exit(0);
            }
		});
		
		connectButton = new Button(shlusb, SWT.NONE);
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int tmpport = Integer.parseInt(portText.getText());
				if (tmpport > 65535 || tmpport <= 0) {
					Logger.error("端口号错误，请重新输入端口号");
					return;
				}
				port = tmpport;
				MessageManager.startManager();
			}
		});
		connectButton.setBounds(10, 284, 180, 38);
		connectButton.setText("启动连接");
		
		disconnectButton = new Button(shlusb, SWT.NONE);
		disconnectButton.setBounds(204, 284, 180, 38);
		disconnectButton.setText("关闭连接");
		disconnectButton.setEnabled(false);
		disconnectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageManager.stopManager();
			}
		});
				
		final Button modButton = new Button(shlusb, SWT.NONE);
		modButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!advmode) {
					modButton.setText("切换基本模式");
				}
				else {
					modButton.setText("切换高级模式");
				}
				advComposite.setVisible(!advmode);
				baseComposite.setVisible(advmode);
				advmode = !advmode;
			}
		});
		modButton.setBounds(304, 10, 80, 27);
		modButton.setText("切换高级模式");
		
		baseComposite = new Composite(shlusb, SWT.NONE);
		baseComposite.setBounds(10, 43, 374, 235);
		
		statusLabel = new CLabel(baseComposite, SWT.NONE);
		statusLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		statusLabel.setAlignment(SWT.CENTER);
		statusLabel.setFont(SWTResourceManager.getFont("微软雅黑", 20, SWT.NORMAL));
		statusLabel.setLocation(0, 191);
		statusLabel.setSize(374, 34);
		statusLabel.setText("请连接设备");
		
		statusImg = new CLabel(baseComposite, SWT.NONE);
		statusImg.setAlignment(SWT.CENTER);
		statusImg.setImage(SWTResourceManager.getImage(MainScreen.class, "/imgs/Cancel2.png"));
		statusImg.setBounds(10, 10, 354, 175);
		statusImg.setText("");
		
		advComposite = new Composite(shlusb, SWT.NONE);
		advComposite.setBounds(10, 43, 374, 235);
		advComposite.setVisible(false);
			
		loglist = new org.eclipse.swt.widgets.List(advComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		loglist.setBounds(0, 0, 374, 196);
		
		Label lblNewLabel = new Label(advComposite, SWT.NONE);
		lblNewLabel.setBounds(77, 210, 53, 17);
		lblNewLabel.setText("监听端口");
		
		portText = new Text(advComposite, SWT.BORDER | SWT.RIGHT);
		portText.setBounds(136, 207, 66, 23);
		portText.setText(Integer.toString(port));
		portText.setTextLimit(5);
		
		Button clearLog = new Button(advComposite, SWT.NONE);
		clearLog.setBounds(208, 205, 80, 27);
		clearLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loglist.removeAll();
			}
		});
		clearLog.setText("清空日志");
		
		Button exportLog = new Button(advComposite, SWT.NONE);
		exportLog.setBounds(294, 205, 80, 27);
		exportLog.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDlg = new FileDialog(shlusb, SWT.SAVE);
				fileDlg.setFileName("log.txt"); 
				fileDlg.setFilterExtensions(new String[]{"txt"});
			    String filename = fileDlg.open();
			    File file = Utils.newFile(filename);
			    if (file != null) {
			    	StringBuffer sb = new StringBuffer();
			    	for (String line : loglist.getItems()) {
			    		sb.append(line).append("\r\n");
			    	}
			    	try {
						Utils.writeFile(file, sb.toString().getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
						MessageBox messageBox = 
								  new MessageBox(shlusb, SWT.OK);
						messageBox.setText("日志文件导出失败");
					}
			    }
				
			}
		});
		exportLog.setText("导出日志");
		portText.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				boolean b = "0123456789".indexOf(e.text) >= 0 ;  
				e.doit = b;
			}
		});
	}

	public void addLog(final String log) {
		Display.getDefault().syncExec(new Runnable() {
		    @Override
			public void run() {
				if (loglist != null) {
					if (loglist.getItemCount() >= 102400) {
						loglist.remove(0);
					}
					loglist.add(log);
					loglist.setSelection(loglist.getItemCount() - 1);
				}
		    }
	    });
	}
	
	public void setConnectState(final boolean enabled) {
		Display.getDefault().syncExec(new Runnable() {
		    @Override
			public void run() {
				if (connectButton != null) {
					connectButton.setEnabled(!enabled);
				}
				if (disconnectButton != null) {
					disconnectButton.setEnabled(enabled);
				}
		    }
	    });
	}
	
	public void setConnectState(final boolean connenabled, final boolean disconnenabled) {
		Display.getDefault().syncExec(new Runnable() {
		    @Override
			public void run() {
				if (connectButton != null) {
					connectButton.setEnabled(connenabled);
				}
				if (disconnectButton != null) {
					disconnectButton.setEnabled(disconnenabled);
				}
		    }
	    });
	}
	
	public void setStatus(final Status status, final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				statusLabel.setFont(SWTResourceManager.getFont("宋体", 20, SWT.NORMAL));
				// 未连接
				if (status == Status.STOP) { 
					statusLabel.setText("请连接设备");
					statusLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
					statusImg.setImage(SWTResourceManager.getImage(MainScreen.class, "/imgs/Cancel2.png"));
				}
				// 等待连接
				else if (status == Status.WAIT) {
					statusLabel.setText("等待连接设备");
					statusLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
					statusImg.setImage(SWTResourceManager.getImage(MainScreen.class, "/imgs/Wait.png"));
				}
				// 程序异常
				else if (status == Status.ERROR) {
					statusLabel.setText("程序异常：" + text);
					statusLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
					statusLabel.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
					statusImg.setImage(SWTResourceManager.getImage(MainScreen.class, "/imgs/ErrorCircle.png"));
				}
				else if (status == Status.USING) {
					statusLabel.setText("使用中");
					statusLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					statusImg.setImage(SWTResourceManager.getImage(MainScreen.class, "/imgs/OKShield.png"));
				}
			}
		});
	}
	
//	public static void main(String[] args) {
//		Map<String, Object> params = new LinkedHashMap<String, Object>();
//		params.put("temp", "李朋");
//		String checkVesion = "";
//		params.put("checkConfigUID", checkVesion);
////		String a = WSUtils.getDownloadInfo(params, "getPatrol_check",
////				"getPatrol_checkReturn", "http://10.180.7.5:9080/maximo/services/bdxsNService?wsdl");
////		System.out.println(a.length());
////		System.out.println(a);
//		String pa = new String(Utils.convertMapToByte(params, "|", "@"));
//		try {
//			MessageManager.getDownloadInfo(pa, "getPatrol_check",
//					"getPatrol_checkReturn", "http://10.180.7.5:9080/maximo/services/bdxsNService?wsdl");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
