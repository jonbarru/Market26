package dataAccess;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Seller;
import domain.Sale;
import domain.Buyer;
import domain.CounterOffer;

import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

/**
 * It implements the data access to the objectDb database
 */
public class DataAccess  {
	private  EntityManager  db;
	private  EntityManagerFactory emf;
    private static final int baseSize = 160;

	private static final String basePath="src/main/resources/images/";
	private static final String dbServerDir = "src/main/resources/db/";


	ConfigXML c=ConfigXML.getInstance();

     public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			if (!c.isDatabaseLocal()) fileName=dbServerDir+fileName;
			
			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();
				System.out.println("File deleted");
			 } else {
				 System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized()) 
			initializeDB();
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

	
	
	/**
	 * This method  initializes the database with some products and sellers.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try { 
	       
		    //Create sellers 
			Seller seller1=new Seller("seller1@gmail.com","Aitor Fernandez");
			Seller seller2=new Seller("seller22@gmail.com","Ane Gaztañaga");
			Seller seller3=new Seller("seller3@gmail.com","Test Seller");

			
			//Create products
			Date today = UtilDate.trim(new Date());
		
			
			seller1.addSale("futbol baloia", "oso polita, gutxi erabilita", 2, 10,  today, null);
			seller1.addSale("salomon mendiko botak", "44 zenbakia, 3 ateraldi",2, 20,  today, null);
			seller1.addSale("samsung 42\" telebista", "berria, erabili gabe", 2, 175,  today, null);


			seller2.addSale("imac 27", "7 urte, dena ondo dabil", 1, 200,today, null);
			seller2.addSale("iphone 17", "oso gutxi erabilita", 2, 400, today, null);
			seller2.addSale("orbea mendiko bizikleta", "29\" 10 urte, mantenua behar du", 3,225, today, null);
			seller2.addSale("polar kilor erlojua", "Vantage M, ondo dago", 3, 30, today, null);

			seller3.addSale("sukaldeko mahaia", "1.8*0.8, 4 aulkiekin. Prezio finkoa", 3,45, today, null);

			
			db.persist(seller1);
			db.persist(seller2);
			db.persist(seller3);

	
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Product
 	 * @throws SaleAlreadyExistException if the same product already exists for the seller
	 */
	public Sale createSale(String title, String description, int status, float price,  Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
		

		System.out.println(">> DataAccess: createProduct=> title= "+title+" seller="+sellerEmail);
		try {
		

			if(pubDate.before(UtilDate.trim(new Date()))) {
				throw new MustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
			}
			if (file==null)
				throw new FileNotUploadedException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorFileNotUploadedException"));

			db.getTransaction().begin();
			
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller.doesSaleExist(title)) {
				db.getTransaction().commit();
				throw new SaleAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.SaleAlreadyExist"));
			}

			Sale sale = seller.addSale(title, description, status, price, pubDate, file);
			//next instruction can be obviated

			db.persist(seller); 
			db.getTransaction().commit();
			 System.out.println("sale stored "+sale+ " "+seller);

			return sale;
		} catch (NullPointerException e) {
			   e.printStackTrace();
			// TODO Auto-generated catch block
			db.getTransaction().commit();
			return null;
		}
		
		
	}
	
	/**
	 * This method retrieves all the products that contain a desc text in a title
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getSales(String desc) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <=?2",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		query.setParameter(2,pubDate);
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}

public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	public BufferedImage getFile(String fileName) {
		File file=new File(basePath+fileName);
		BufferedImage targetImg=null;
		try {
             targetImg = rescale(ImageIO.read(file));
        } catch (IOException ex) {
            //Logger.getLogger(MainAppFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
		return targetImg;

	}
	
	public BufferedImage rescale(BufferedImage originalImage)
    {
		System.out.println("rescale "+originalImage);
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	
	
	
	public void close(){
		db.close();
		System.out.println("DataAcess closed");
	}
	
	//
	// 1. Registrar un Vendedor
		public boolean registerSeller(String name, String email, String password) {
			db.getTransaction().begin();
			// Comprobamos si ya existe alguien con ese email
			if (db.find(Seller.class, email) != null) {
				db.getTransaction().rollback();
				return false; 
			}
			Seller s = new Seller(email, name);
			s.setPassword(password);
			db.persist(s);
			db.getTransaction().commit();
			return true;
		}

		// 2. Registrar un Comprador
		public boolean registerBuyer(String name, String email, String password) {
			db.getTransaction().begin();
			if (db.find(Buyer.class, email) != null) {
				db.getTransaction().rollback();
				return false; 
			}
			Buyer b = new Buyer(email, name, password);
			db.persist(b);
			db.getTransaction().commit();
			return true;
		}

		// 3. Hacer Login (devuelve el usuario si acierta, o null si falla)
		public Object login(String email, String password) {
			Seller s = db.find(Seller.class, email);
			if (s != null && password.equals(s.getPassword())) {
				return s;
			}
			Buyer b = db.find(Buyer.class, email);
			if (b != null && password.equals(b.getPassword())) {
				return b;
			}
			return null; // Credenciales incorrectas
		}

		// 4. Aceptar Oferta (El comprador compra el "Sale")
		public boolean acceptSale(String buyerEmail, Integer saleNumber) {
			db.getTransaction().begin();
			Buyer b = db.find(Buyer.class, buyerEmail);
			Sale s = db.find(Sale.class, saleNumber);

			// Si ambos existen y la oferta no ha sido comprada aún
			if (b != null && s != null && s.getBuyer() == null) {
				s.setBuyer(b); // Le asignamos el comprador a la oferta
				b.addAcceptedSale(s); // Le añadimos la oferta a la lista del comprador
				db.getTransaction().commit();
				return true;
			}
			db.getTransaction().rollback();
			return false;
		}

		// 5. Visualizar Ofertas Aceptadas (Para el Vendedor)
		public List<Sale> getAcceptedSales(String sellerEmail) {
			Seller s = db.find(Seller.class, sellerEmail);
			List<Sale> accepted = new ArrayList<Sale>();
			if (s != null) {
				for (Sale sale : s.getSales()) {
					// Si la oferta tiene un comprador, es que fue aceptada
					if (sale.getBuyer() != null) {
						accepted.add(sale);
					}
				}
			}
			return accepted;
		}
		
		// Devuelve una lista con todas las ofertas disponibles que tengan ese nombre
		// Devuelve una lista con todas las ofertas disponibles que CONTENGAN ese nombre
		public List<Sale> getActiveSalesByTitle(String title) {
			// Cambiamos el '=' por 'LIKE'
			javax.persistence.TypedQuery<Sale> query = db.createQuery(
				"SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.buyer IS NULL", Sale.class);
			
			// Añadimos los '%' para que busque la palabra en cualquier parte del texto
			query.setParameter(1, "%" + title + "%");
			return query.getResultList();
		}
		// --- 1. Guardar una nueva contraoferta ---
		public boolean makeCounterOffer(String buyerEmail, Integer saleNumber, float offeredPrice) {
			db.getTransaction().begin();
			Buyer buyer = db.find(Buyer.class, buyerEmail);
			Sale sale = db.find(Sale.class, saleNumber);

			// Comprobamos que existan y que la oferta no esté ya vendida
			if (buyer != null && sale != null && sale.getBuyer() == null) {
				CounterOffer offer = new CounterOffer(offeredPrice, buyer, sale);
				db.persist(offer); // Guardamos la contraoferta
				sale.addCounterOffer(offer); // La enlazamos con la oferta original
				db.getTransaction().commit();
				return true;
			}
			db.getTransaction().rollback();
			return false;
		}

		// --- 2. Buscar las contraofertas que el Vendedor tiene que revisar ---
		public List<CounterOffer> getPendingCounterOffers(String sellerEmail) {
			// Buscamos contraofertas cuyo estado sea "Pendiente" y que pertenezcan a las ofertas de este vendedor
			javax.persistence.TypedQuery<CounterOffer> query = db.createQuery(
				"SELECT c FROM CounterOffer c WHERE c.sale.seller.email = ?1 AND c.status = 'Pendiente'", 
				CounterOffer.class);
			query.setParameter(1, sellerEmail);
			return query.getResultList();
		}

		// --- 3. El Vendedor decide: Aceptar o Rechazar ---
		public boolean resolveCounterOffer(Integer counterOfferId, boolean accept) {
			db.getTransaction().begin();
			CounterOffer offer = db.find(CounterOffer.class, counterOfferId);
			
			if (offer != null && offer.getStatus().equals("Pendiente")) {
				if (accept) {
					offer.setStatus("Aceptada");
					Sale sale = offer.getSale();
					Buyer buyer = offer.getBuyer();
					
					// ¡Se efectúa la venta al nuevo precio regateado!
					sale.setBuyer(buyer);
					sale.setPrice(offer.getOfferedPrice()); 
					buyer.addAcceptedSale(sale);
					
					// Opcional pero recomendado: Rechazar automáticamente a los demás que pujaban por lo mismo
					for (CounterOffer other : sale.getCounterOffers()) {
						if (other.getStatus().equals("Pendiente") && !other.getId().equals(counterOfferId)) {
							other.setStatus("Rechazada");
						}
					}
				} else {
					// Si no la acepta, simplemente la marcamos como rechazada
					offer.setStatus("Rechazada");
				}
				db.getTransaction().commit();
				return true;
			}
			db.getTransaction().rollback();
			return false;
		}
		
		// Edit profile
		// -- 1. editName --
		public boolean editName(String currentMail, String newName) {
			db.getTransaction().begin();
			// Asegurar que el nuevo nombre no está ocupado 
			boolean existSeller = !db.createQuery("SELECT s FROM Seller s WHERE s.name = :nombre")
					.setParameter("nombre", newName).getResultList().isEmpty();
			
			boolean existBuyer = !db.createQuery("SELECT b FROM Buyer b WHERE b.name = :nombre")
					.setParameter("nombre", newName).getResultList().isEmpty();
			
			if(existSeller || existBuyer) {
				db.getTransaction().rollback();
				return false;
			}
			
			Seller s = db.find(Seller.class, currentMail);
	        if (s != null) {
	            s.setName(newName);
	            db.getTransaction().commit();
	            return true;
	        }
	        
	        Buyer b = db.find(Buyer.class, currentMail);
	        if (b != null) {
	            b.setName(newName);
	            db.getTransaction().commit();
	            return true;
	        }
	        
	        db.getTransaction().rollback();
	        return false;
		} 
		
		// -- 2. editMail --
		public boolean editMail(String currentMail, String newMail) {
			// Si el nuevo email es igual, false
			if (currentMail.equals(newMail)) {
		        return false; 
		    }
			
			db.getTransaction().begin();
			
			// Asegurar que el nuevo email no está ocupado
			if(db.find(Seller.class, newMail) != null || db.find(Buyer.class, newMail) != null) {
				db.getTransaction().rollback();
				return false;
			}
			
			// Hay que hacer un nuevo usuario en la BBDD porque email es primary key
			Seller oldSeller = db.find(Seller.class, currentMail);
			
			if(oldSeller != null) {
				Seller newSeller = new Seller(newMail, oldSeller.getName());
				db.remove(oldSeller);
				db.persist(newSeller);
				db.getTransaction().commit();
				return true;
			}
			
			Buyer oldBuyer = db.find(Buyer.class, currentMail);
			
			if(oldBuyer != null) {
				Buyer newBuyer = new Buyer(newMail, oldBuyer.getName(), oldBuyer.getPassword());
				db.remove(oldBuyer);
				db.persist(newBuyer);
				db.getTransaction().commit();
				return true;
			}
	        
	        db.getTransaction().rollback();
	        return false;
		} 
		
		// -- 3. editPassword --
		public boolean editPassword(String currentMail, String newPass) {
			db.getTransaction().begin();
			
			Seller s = db.find(Seller.class, currentMail);
	        if (s != null) {
	            s.setPassword(newPass);
	            db.getTransaction().commit();
	            return true;
	        }
	        
	        Buyer b = db.find(Buyer.class, currentMail);
	        if (b != null) {
	        	b.setPassword(newPass);
	            db.getTransaction().commit();
	            return true;
	        }
	        
	        db.getTransaction().rollback();
	        return false;
		}
	
		// --- Métodos para ranking de vendedores y envío de emails ---
		
		// Obtener todos los vendedores ordenados por rating (descendente)
		public List<Seller> getSellerRanking() {
			javax.persistence.TypedQuery<Seller> query = db.createQuery(
				"SELECT s FROM Seller s ORDER BY s.rating DESC", 
				Seller.class);
			return query.getResultList();
		}
		
		// Obtener resumen de contraofertas pendientes para envío por email
		public List<CounterOffer> getCounterOffersSummary() {
			javax.persistence.TypedQuery<CounterOffer> query = db.createQuery(
				"SELECT c FROM CounterOffer c WHERE c.status = 'Pendiente' ORDER BY c.sale.seller.email, c.id", 
				CounterOffer.class);
			return query.getResultList();
		}
		
		// Actualizar rating del vendedor basado en sus ventas aceptadas
		public void updateSellerRating(String sellerEmail, double newRating) {
			db.getTransaction().begin();
			Seller s = db.find(Seller.class, sellerEmail);
			if (s != null) {
				s.setRating(newRating);
				db.getTransaction().commit();
			} else {
				db.getTransaction().rollback();
			}
		}
}
