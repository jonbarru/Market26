package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;
import businessLogic.BLFacade;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUI extends JFrame {
	
	private String sellerMail;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;

	private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	
	public static void setBussinessLogic (BLFacade facade){
		appFacadeInterface=facade;
	}
	
	protected JLabel jLabelSelectOption;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JPanel panel;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	// Nuestros nuevos botones
	private JButton btnLogin;
	private JButton btnRegister;
	private JButton btnSellerRanking;
	private JButton btnSendCounterOffersEmail;

	/**
	 * This is the default constructor
	 */
	public MainGUI( String mail) {
		super();

		this.sellerMail=mail;
		
		// Restauramos el tamaño original de la ventana para que quede proporcionada con 6 elementos
		this.setSize(495, 380);
		
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();				
			}
		});
		buttonGroup.add(rdbtnNewRadioButton);
		
		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();				
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);
	
		panel = new JPanel();
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);
		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateSaleGUI(sellerMail);
				a.setVisible(true);
			}
		});
		
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI();
				a.setVisible(true);
			}
		});
		
		// Configuración del botón de Registro
		btnRegister = new JButton();
		btnRegister.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Register"));

		btnRegister.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        RegisterGUI registerWindow = new RegisterGUI();
		        registerWindow.setVisible(true);
		    }
		});

		// Configuración del botón de Login
		btnLogin = new JButton();
		btnLogin.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Login"));
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 LoginGUI loginWindow = new LoginGUI();
				 loginWindow.setVisible(true);
			}
		});
		
		// Configuración del botón de Ranking de Vendedores
		btnSellerRanking = new JButton();
		btnSellerRanking.setText("Ver Ranking de Vendedores");
		btnSellerRanking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SellerRankingGUI rankingWindow = new SellerRankingGUI();
				rankingWindow.setVisible(true);
			}
		});
		
		// Configuración del botón de Envío de Emails
		btnSendCounterOffersEmail = new JButton();
		btnSendCounterOffersEmail.setText("Enviar Emails de Contraofertas");
		btnSendCounterOffersEmail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SendCounterOffersEmailGUI emailWindow = new SendCounterOffersEmailGUI(MainGUI.this);
				emailWindow.setVisible(true);
			}
		});
		
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridLayout(6, 1, 0, 0));
		
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(btnRegister);
		jContentPane.add(btnLogin);
		jContentPane.add(btnSellerRanking);
		jContentPane.add(btnSendCounterOffersEmail);
		jContentPane.add(panel);
		
		setContentPane(jContentPane);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle"));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	
	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		btnRegister.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Register"));
		btnLogin.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Login")); 
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle"));
		
		// ESTO ES LO NUEVO PARA LOS OPTION PANE:
		// Cambia el texto del botón Aceptar
		UIManager.put("OptionPane.okButtonText", ResourceBundle.getBundle("Etiquetas").getString("Accept"));
		// Cambia el título de la ventanita (Message/Mensaje)
		UIManager.put("OptionPane.messageDialogTitle", ResourceBundle.getBundle("Etiquetas").getString("MessageTitle"));
		// Cambia el texto del botón Cancelar
		UIManager.put("OptionPane.cancelButtonText", ResourceBundle.getBundle("Etiquetas").getString("Close"));
	}
}