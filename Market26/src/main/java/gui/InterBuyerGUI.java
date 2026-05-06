package gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle; 
import businessLogic.BLFacade;
import domain.Buyer;
import domain.Seller;
import domain.Sale;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

public class InterBuyerGUI extends JFrame {

	private JPanel contentPane;
	private Buyer currentBuyer;

	
	public InterBuyerGUI(Buyer buyer) {
		this.currentBuyer = buyer;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 480, 250);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 1, 0, 0));
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.Title") + ": " + buyer.getName()); 
        
        JLabel lblSelect = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.Option"));
        lblSelect.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblSelect.setHorizontalAlignment(SwingConstants.CENTER);
        lblSelect.setForeground(new Color(0, 0, 0));
        contentPane.add(lblSelect);
        
        // Botón para comprar
        JButton btnBuy = new JButton(ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.Buy"));
        contentPane.add(btnBuy);
        btnBuy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		AcceptSaleGUI acceptWindow = new AcceptSaleGUI(buyer);
        		acceptWindow.setVisible(true);
        	}
        });
        
        
        // Botón para editar perfil
        JButton btnEdit = new JButton(ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.Edit")); 
        btnEdit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		EditProfileGUI editWindow = new EditProfileGUI(buyer);
        		editWindow.setVisible(true);
        	}
        });
        contentPane.add(btnEdit);

        // Botón para valorar vendedor
        JButton btnRateSeller = new JButton(ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.RateSeller"));
        btnRateSeller.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
			BLFacade facade = MainGUI.getBusinessLogic();
			List<Seller> sellers = facade.getSellerRanking();
			if (sellers == null || sellers.isEmpty()) {
				JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.NoSellers"));
				return;
			}
			String[] options = new String[sellers.size()];
			for (int i = 0; i < sellers.size(); i++) {
				Seller s = sellers.get(i);
				options[i] = s.getName() + " (" + s.getEmail() + ")";
			}
			String selected = (String) JOptionPane.showInputDialog(
					null,
					ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.SelectSeller"),
					ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.RateSeller"),
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			if (selected == null) {
				return;
			}
			int index = java.util.Arrays.asList(options).indexOf(selected);
			if (index < 0) {
				return;
			}
			Seller seller = sellers.get(index);
			String ratingText = JOptionPane.showInputDialog(
					null,
					ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.RatingPrompt"),
					"5");
			if (ratingText == null) {
				return;
			}
			double rating;
			try {
				rating = Double.parseDouble(ratingText);
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.InvalidRating"));
				return;
			}
			if (rating < 0 || rating > 5) {
				JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.InvalidRatingRange"));
				return;
			}
			boolean success = facade.rateSeller(seller.getEmail(), rating);
			if (success) {
				JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.RatingSuccess"));
			} else {
				JOptionPane.showMessageDialog(null, ResourceBundle.getBundle("Etiquetas").getString("InterBuyerGUI.RatingFail"));
			}
        	}
        });
        contentPane.add(btnRateSeller);

	}

}

