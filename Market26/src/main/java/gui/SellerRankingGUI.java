package gui;

import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Seller;

/**
 * GUI para mostrar ranking de vendedores ordenados por valoración
 */
public class SellerRankingGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel tableModel;
	private BLFacade businessLogic;

	public SellerRankingGUI() {
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Title"));
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		businessLogic = MainGUI.getBusinessLogic();

		// Panel principal
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Título
		JLabel titleLabel = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Title"));
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// Crear tabla
		String[] columnNames = { 
			ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Name"),
			ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Email"),
			ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Rating")
		};
		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(25);

		// Hacer la columna de rating alineada al centro
		table.getColumnModel().getColumn(2).setMaxWidth(80);

		JScrollPane scrollPane = new JScrollPane(table);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Panel de botones
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

		JButton refreshButton = new JButton(
				ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Refresh"));
		refreshButton.addActionListener(e -> loadRanking());
		buttonPanel.add(refreshButton);

		JButton closeButton = new JButton(
				ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.Close"));
		closeButton.addActionListener(e -> dispose());
		buttonPanel.add(closeButton);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);

		// Cargar datos iniciales
		loadRanking();
	}

	/**
	 * Carga el ranking de vendedores desde la lógica de negocio
	 */
	private void loadRanking() {
		try {
			// Limpiar tabla
			tableModel.setRowCount(0);

			// Obtener ranking
			List<Seller> sellers = businessLogic.getSellerRanking();

			// Llenar tabla
			int position = 1;
			for (Seller seller : sellers) {
				Object[] row = { 
					position + ". " + seller.getName(), 
					seller.getEmail(),
					String.format("%.2f", seller.getRating())
				};
				tableModel.addRow(row);
				position++;
			}

			// Mensaje si no hay vendedores
			if (sellers.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						ResourceBundle.getBundle("Etiquetas").getString("SellerRankingGUI.NoSellers"),
						"Info", JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error al cargar ranking: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.err.println("Error en SellerRankingGUI: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SellerRankingGUI frame = new SellerRankingGUI();
				frame.setVisible(true);
			}
		});
	}
}
