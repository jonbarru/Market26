package businessLogic;

import java.io.File;
import java.util.Date;
import java.util.List;

import domain.Sale;
import domain.Seller;
import domain.CounterOffer;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.awt.Image;

import gui.*;
/**
 * Interface that specifies the business logic.
 */
@WebService
public interface BLFacade  {
	  

	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Sale
	 */
   @WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException;
	
	
	/**
	 * This method retrieves the products that contain desc
	 * 
	 * @param desc the text to search
	 * @return collection of sales that contain desc 
	 */
	@WebMethod public List<Sale> getSales(String desc);
	
	/**
	 * 	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @param pubDate the date  of the publication date
	 * @return collection of sales that contain desc and published before pubDate
	 */
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);

	
	/**
	 * This method calls the data access to initialize the database with some sellers and products.
	 * It is only invoked  when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();
	
		
	@WebMethod public Image downloadImage(String imageName);
	
	//
	public boolean registerSeller(String name, String email, String password);
	public boolean registerBuyer(String name, String email, String password);
	public Object login(String email, String password);
	public boolean acceptSale(String buyerEmail, Integer saleNumber);
	public List<Sale> getAcceptedSales(String sellerEmail);
	
	
	public List<Sale> getActiveSalesByTitle(String title);
	
	//
	// --- Métodos de Contraoferta ---
	public boolean makeCounterOffer(String buyerEmail, Integer saleNumber, float offeredPrice);
	public List<CounterOffer> getPendingCounterOffers(String sellerEmail);
	public boolean resolveCounterOffer(Integer counterOfferId, boolean accept);
	
	// -- Métodos para editar el perfil --
	public boolean editName(String currentMail, String newName);
	public boolean editMail(String currentMail, String newMail);
	public boolean editPassword(String currentMail, String newPass);
	
	// --- Métodos para ranking de vendedores y envío de emails ---
	public List<Seller> getSellerRanking();
	public boolean sendCounterOffersEmailToSellers();
	public boolean rateSeller(String sellerEmail, double rating);
}
