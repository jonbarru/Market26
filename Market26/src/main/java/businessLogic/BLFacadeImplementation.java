package businessLogic;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import dataAccess.DataAccess;
import domain.Sale;
import domain.Seller;
import domain.CounterOffer;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * It implements the business logic as a web service.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 private static final int baseSize = 160;

		private static final String basePath="src/main/resources/images/";
	DataAccess dbManager;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		dbManager=new DataAccess();		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dbManager=da;		
	}
    

	/**
	 * {@inheritDoc}
	 */
   @WebMethod
	public Sale createSale(String title, String description,int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		dbManager.open();
		Sale product=dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file);		
		dbManager.close();
		return product;
   };
	
   /**
    * {@inheritDoc}
    */
	@WebMethod 
	public List<Sale> getSales(String desc){
		dbManager.open();
		List<Sale>  rides=dbManager.getSales(desc);
		dbManager.close();
		return rides;
	}
	
	/**
	    * {@inheritDoc}
	    */
		@WebMethod 
		public List<Sale> getPublishedSales(String desc, Date pubDate) {
			dbManager.open();
			List<Sale>  rides=dbManager.getPublishedSales(desc,pubDate);
			dbManager.close();
			return rides;
		}
	/**
	    * {@inheritDoc}
	    */
	@WebMethod public BufferedImage getFile(String fileName) {
		return dbManager.getFile(fileName);
	}

    
	public void close() {
		DataAccess dB4oManager=new DataAccess();
		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    @WebMethod	
	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}
    /**
	 * {@inheritDoc}
	 */
    @WebMethod public Image downloadImage(String imageName) {
        File image = new File(basePath+imageName);
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //
    public boolean registerSeller(String name, String email, String password) {
		dbManager.open();
		boolean res = dbManager.registerSeller(name, email, password);
		dbManager.close();
		return res;
	}

	public boolean registerBuyer(String name, String email, String password) {
		dbManager.open();
		boolean res = dbManager.registerBuyer(name, email, password);
		dbManager.close();
		return res;
	}

	public Object login(String email, String password) {
		dbManager.open();
		Object user = dbManager.login(email, password);
		dbManager.close();
		return user;
	}

	public boolean acceptSale(String buyerEmail, Integer saleNumber) {
		dbManager.open();
		boolean res = dbManager.acceptSale(buyerEmail, saleNumber);
		dbManager.close();
		return res;
	}

	public List<Sale> getAcceptedSales(String sellerEmail) {
		dbManager.open();
		List<Sale> sales = dbManager.getAcceptedSales(sellerEmail);
		dbManager.close();
		return sales;
	}
	
	public List<Sale> getActiveSalesByTitle(String title) {
		dbManager.open(); 
		List<Sale> list = dbManager.getActiveSalesByTitle(title);
		dbManager.close();
		return list;
	}
	
	// --- 1. Guardar Contraoferta ---
	public boolean makeCounterOffer(String buyerEmail, Integer saleNumber, float offeredPrice) {
		dbManager.open(); // Recuerda usar tu versión de open
		boolean res = dbManager.makeCounterOffer(buyerEmail, saleNumber, offeredPrice);
		dbManager.close();
		return res;
	}

	// --- 2. Obtener Contraofertas Pendientes ---
	public List<CounterOffer> getPendingCounterOffers(String sellerEmail) {
		dbManager.open();
		List<CounterOffer> list = dbManager.getPendingCounterOffers(sellerEmail);
		dbManager.close();			
		return list;
	}

	// --- 3. Aceptar o Rechazar Contraoferta ---
	public boolean resolveCounterOffer(Integer counterOfferId, boolean accept) {
		dbManager.open();
		boolean res = dbManager.resolveCounterOffer(counterOfferId, accept);
		dbManager.close();
		return res;
	}
		
	// Edit profile
	public boolean editName(String currentMail, String newName) {	
		dbManager.open();
		boolean res = dbManager.editName(currentMail, newName);
		return res;
		
	}
	
	public boolean editMail(String currentMail, String newMail) {
		dbManager.open();
		boolean res = dbManager.editMail(currentMail, newMail);
		return true;
	}
	
	public boolean editPassword(String currentMail, String newPass) {
		dbManager.open();
		boolean res = dbManager.editPassword(currentMail, newPass);
		return true;
	}
	
	// --- Métodos para ranking de vendedores y envío de emails ---
	
	public List<Seller> getSellerRanking() {
		dbManager.open();
		List<Seller> ranking = dbManager.getSellerRanking();
		dbManager.close();
		return ranking;
	}

	public boolean rateSeller(String sellerEmail, double rating) {
		dbManager.open();
		dbManager.updateSellerRating(sellerEmail, rating);
		dbManager.close();
		return true;
	}
	
	public boolean sendCounterOffersEmailToSellers() {
		dbManager.open();
		List<CounterOffer> counterOffers = dbManager.getCounterOffersSummary();
		dbManager.close();
		
		// Agrupar contraofertas por vendedor
		java.util.Map<String, java.util.List<CounterOffer>> sellerOffers = 
			new java.util.HashMap<String, java.util.List<CounterOffer>>();
		
		for (CounterOffer offer : counterOffers) {
			String sellerEmail = offer.getSale().getSeller().getEmail();
			if (!sellerOffers.containsKey(sellerEmail)) {
				sellerOffers.put(sellerEmail, new java.util.ArrayList<CounterOffer>());
			}
			sellerOffers.get(sellerEmail).add(offer);
		}
		
		// Enviar email a cada vendedor
		try {
			for (java.util.Map.Entry<String, java.util.List<CounterOffer>> entry : sellerOffers.entrySet()) {
				String sellerEmail = entry.getKey();
				if (sellerEmail == null) {
					System.err.println("Email inválido: email de vendedor nulo");
					continue;
				}
				sellerEmail = sellerEmail.trim();
				if (sellerEmail.isEmpty()) {
					System.err.println("Email inválido: email de vendedor vacío");
					continue;
				}
				
				List<CounterOffer> offers = entry.getValue();
				
				StringBuilder emailBody = new StringBuilder();
				emailBody.append("Estimado vendedor,\n\n");
				emailBody.append("Tiene las siguientes contraofertas pendientes:\n\n");
				
				for (CounterOffer offer : offers) {
					emailBody.append("- Producto: ").append(offer.getSale().getTitle()).append("\n");
					emailBody.append("  Precio original: ").append(offer.getSale().getPrice()).append("€\n");
					emailBody.append("  Precio ofertado: ").append(offer.getOfferedPrice()).append("€\n");
					emailBody.append("  Comprador: ").append(offer.getBuyer().getEmail()).append("\n\n");
				}
				
				emailBody.append("Por favor, responda a través de la aplicación.\n\n");
				emailBody.append("Saludos,\nEquipo Market26");
				
				// Enviar email
				EmailService.sendEmail(sellerEmail, "Contraofertas pendientes - Market26", emailBody.toString());
			}
			return true;
		} catch (Exception e) {
			System.err.println("Error al enviar emails: " + e.getMessage());
			return false;
		}
	}
}

