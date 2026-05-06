package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import businessLogic.BLFacade;

/**
 * GUI para enviar emails de contraofertas a los vendedores
 */
public class SendCounterOffersEmailGUI extends JDialog {

	private static final long serialVersionUID = 1L;
	private BLFacade businessLogic;
	private JTextArea resultArea;
	private JButton sendButton;
	private JButton closeButton;

	public SendCounterOffersEmailGUI(JFrame parent) {
		super(parent, true);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("SendCounterOffersEmailGUI.Title"));
		setSize(500, 350);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		businessLogic = MainGUI.getBusinessLogic();

		// Panel principal
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Título
		JLabel titleLabel = new JLabel(
				ResourceBundle.getBundle("Etiquetas").getString("SendCounterOffersEmailGUI.Title"));
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		mainPanel.add(titleLabel, BorderLayout.NORTH);

		// Descripción
		JPanel descPanel = new JPanel(new BorderLayout());
		JLabel descLabel = new JLabel(
				"<html>Este proceso enviará un email a todos los vendedores con sus contraofertas pendientes.<br>"
						+ "Asegúrate de que el servicio de email está configurado correctamente.</html>");
		descPanel.add(descLabel, BorderLayout.NORTH);
		mainPanel.add(descPanel, BorderLayout.WEST);

		// Área de resultados
		resultArea = new JTextArea(10, 40);
		resultArea.setEditable(false);
		resultArea.setLineWrap(true);
		resultArea.setWrapStyleWord(true);
		resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		JScrollPane scrollPane = new JScrollPane(resultArea);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Panel de botones
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

		sendButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("SendCounterOffersEmailGUI.Send"));
		sendButton.addActionListener(e -> sendEmails());
		buttonPanel.add(sendButton);

		closeButton = new JButton(ResourceBundle.getBundle("Etiquetas").getString("SendCounterOffersEmailGUI.Close"));
		closeButton.addActionListener(e -> dispose());
		buttonPanel.add(closeButton);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
	}

	/**
	 * Envía los emails de contraofertas
	 */
	private void sendEmails() {
		try {
			sendButton.setEnabled(false);
			resultArea.setText("");
			resultArea.append("Iniciando envío de emails...\n");
			resultArea.append("================================\n\n");

			// Llamar al método de negocio
			boolean success = businessLogic.sendCounterOffersEmailToSellers();

			if (success) {
				resultArea.append("✅ Proceso completado exitosamente.\n\n");
				resultArea.append("Los vendedores han sido notificados sobre sus contraofertas pendientes.\n");
				JOptionPane.showMessageDialog(this,
						"Emails enviados correctamente",
						"Éxito",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				resultArea.append("❌ Error durante el envío de emails.\n");
				resultArea.append("Verifica la configuración del servicio de email.\n");
				JOptionPane.showMessageDialog(this,
						"Error al enviar emails",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (Exception e) {
			resultArea.append("❌ Error: " + e.getMessage() + "\n");
			System.err.println("Error en SendCounterOffersEmailGUI: " + e.getMessage());
			e.printStackTrace();
		} finally {
			sendButton.setEnabled(true);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SendCounterOffersEmailGUI dialog = new SendCounterOffersEmailGUI(null);
				dialog.setVisible(true);
			}
		});
	}
}
