package gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle; 
import businessLogic.BLFacade;
import domain.Seller;
import domain.Sale;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

public class InterSellerGUI extends JFrame {

	private JPanel contentPane;
	private Seller currentSeller;

	
	public InterSellerGUI(Seller seller) {
		this.currentSeller = seller;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 480, 250);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 1, 0, 0));
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("InterSellerGUI.Title") + ": " + seller.getName()); 
        
        JLabel lblSelect = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("InterSellerGUI.Option"));
        lblSelect.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblSelect.setHorizontalAlignment(SwingConstants.CENTER);
        lblSelect.setForeground(new Color(0, 0, 0));
        contentPane.add(lblSelect);
        
        // Botón para comprar
        JButton btnBuy = new JButton(ResourceBundle.getBundle("Etiquetas").getString("InterSellerGUI.Sales"));
        contentPane.add(btnBuy);
        btnBuy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ViewAcceptedSalesGUI acceptWindow = new ViewAcceptedSalesGUI(seller);
        		acceptWindow.setVisible(true);
        	}
        });
        
        
        // Botón para editar perfil
        JButton btnEdit = new JButton(ResourceBundle.getBundle("Etiquetas").getString("InterSellerGUI.Edit")); 
        btnEdit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		EditProfileGUI editWindow = new EditProfileGUI(seller);
        		editWindow.setVisible(true);
        	}
        });
        contentPane.add(btnEdit);

	}

}
