package br.univille.projcolabassistant;

import static br.univille.projcolabassistant.constants.Constants.ANY_END_DATE;
import static br.univille.projcolabassistant.constants.Constants.ANY_START_DATE;
import static br.univille.projcolabassistant.constants.Constants.EXPECTED_FILES;
import static br.univille.projcolabassistant.constants.Constants.RESULT_NOT_FOUND_FILE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import br.univille.projcolabassistant.model.AccessoryColor;
import br.univille.projcolabassistant.model.AccessoryPhoto;
import br.univille.projcolabassistant.model.AccessorySize;
import br.univille.projcolabassistant.model.AssistiveAccessory;
import br.univille.projcolabassistant.model.Category;
import br.univille.projcolabassistant.model.City;
import br.univille.projcolabassistant.model.Institution;
import br.univille.projcolabassistant.model.OrderItems;
import br.univille.projcolabassistant.model.OrderRequest;
import br.univille.projcolabassistant.model.User;
import br.univille.projcolabassistant.repository.InstitutionRepository;
import br.univille.projcolabassistant.repository.OrderRequestRepository;
import br.univille.projcolabassistant.repository.UserRepository;
import br.univille.projcolabassistant.service.impl.ReportServiceImpl;
import br.univille.projcolabassistant.viewmodel.OrderSumByCategory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportServiceTests {
	@InjectMocks
	private ReportServiceImpl reportService;

	@Mock
	private UserRepository mockUserRepository;

	@Mock
	private InstitutionRepository mockInstitutionRepository;

	@Mock
	private OrderRequestRepository mockOrderRepository;
	
	
	@Autowired
	private TemplateEngine templateEngine;

	private User dummyUserPedro;
	private User dummyUserAna;
	private User dummyUserJoao;
	private User dummyUserAdmin;

	private City dummyJoinville;
	private City dummyCuritiba;

	private Institution dummyUniville;
    private Institution dummyHospital;
    private Institution dummyEscola;
	private Institution dummySaude;

	private OrderRequest dummyPedidoA;
	private OrderRequest dummyPedidoB;
	private OrderRequest dummyPedidoC;

	private final long JAN_01_2018 = 1514772000000L;
	private final long JAN_02_2018 = 1514858400000L;
	private final long JAN_20_2018 = 1516413600000L;
	private final long FEV_05_2018 = 1517796000000L;
	private final long FEV_15_2018 = 1518660000000L;
	private final long MAR_10_2018 = 1520650800000L;
	private final long APR_01_2018 = 1522551600000L;
	
	private Category categoryA;
	private Category categoryB;
	
	private AccessoryPhoto photoA;
	
	private AssistiveAccessory assistiveAccessoryA;
	private AssistiveAccessory assistiveAccessoryB;
	
	private OrderItems orderItemsA;
	private OrderItems orderItemsB;
	
	private AccessorySize accessorySizeA; 
	
	private AccessoryColor accessoryColorA; 
	private AccessoryColor accessoryColorB;
	
	private OrderSumByCategory orderSumCategoryA;
	private OrderSumByCategory orderSumCategoryB;
	
    @Before
    public void setUp() {
    	reportService.setTemplateEngine(templateEngine);

    	dummyUserPedro = new User(12548049127L, "Pedro Barbosa", "pedro@test.com", "ADMIN", "(47) 1111-2222", "Av. Macieira - 777", true);
		dummyUserAna = new User(34144262579L, "Ana Barbosa", "ana@test.com", "BASIC", "(47) 3333-4444", "Av. Laranjeira - 888", true);
		dummyUserJoao = new User(48038862175L, "Joao Silva", "joao@test.com", "ADMIN", "(47) 5555-6666", "Av. Bananeira - 999", true);
		dummyUserAdmin = new User(48038862175L, "System Admin", "admin@univille.br", "ADMIN", "(47) 7777-8888", "Av. Admin - 999", true);

		dummyJoinville = new City(10001, "Joinville", "SC");
		dummyCuritiba = new City(10002, "Curitiba", "PR");

		dummyUniville = new Institution(20001, "UNIVILLE", "Uma universidade de Joinville.", "R. Paulo Malschitzki, 10", "(47) 3461-9085", "contato@univille.br", dummyJoinville);
		dummyHospital = new Institution(20002, "Hospital Municipal Cardiológico Costantini", "Um hospital de Curitiba.", "R. Pedro Collere, 890", "(41) 3013-9000", "sac@hospitalcostantini.com.br", dummyCuritiba);
		dummyEscola = new Institution(20003, "Escola Municipal Vila Torres", "Uma escola de Curitiba.", "R. do Chile, 838", "(41) 3333-0199", "emvilatorres@sme.curitiba.pr.gov.br", dummyCuritiba);
		dummySaude = new Institution(20003, "Instituto Pró-Rim Curitiba", "Um instituto de saúde.", "R. do Vale, 321", "(41) 3223-0166", "pro@inst.pro.org", dummyCuritiba);

		dummyPedidoA = new OrderRequest(40001, new Date(JAN_01_2018), new Date(JAN_20_2018), 100, dummyUniville, dummyUserAdmin, dummyUserAna);
		dummyPedidoB = new OrderRequest(40002, new Date(JAN_02_2018), new Date(FEV_05_2018), 200, dummyHospital, dummyUserAdmin, dummyUserAna);
		dummyPedidoC = new OrderRequest(40003, new Date(FEV_15_2018), new Date(MAR_10_2018), 100, dummyHospital, dummyUserAdmin, dummyUserJoao);
		
		categoryA = new Category(00001,"Categoria A");
		categoryB = new Category(00002,"Categoria B");
		
		photoA = new AccessoryPhoto(00001,"PhotoSupimpa.jpg","Descricao de teste",00001);
		
		assistiveAccessoryA = new AssistiveAccessory(00001,"00001","Descrição do primeiro acessório","O primeiro acessório serve para testar","Teste 00001","Prescricao teste",categoryA);
		assistiveAccessoryB = new AssistiveAccessory(00002,"00002","Descrição do segundo acessório"," O segundo acessório serve para verificar se o primeiro funcionou","Teste 00002","Prescricao teste2",categoryB);
		
		accessorySizeA = new AccessorySize(00001,"Nome Tamanho A", 00001);
		
		accessoryColorA = new AccessoryColor(00001,"Vermelho");
		accessoryColorB = new AccessoryColor(00002,"Preto");
		
		orderItemsA = new OrderItems(00001,10,assistiveAccessoryA,accessorySizeA,accessoryColorA);
		orderItemsB = new OrderItems(00002,35,assistiveAccessoryB,accessorySizeA,accessoryColorA);
		
		orderSumCategoryA = new OrderSumByCategory(00003, "Categoria A", 10);
		orderSumCategoryB = new OrderSumByCategory(00004, "Categoria B", 35);
    }

    //==============================
    //      Users Report Tests
    //==============================

    @Test
    public void getAllUsersReportTest() {
		when(mockUserRepository.searchWithFilters("", "")).thenReturn(asList(dummyUserPedro, dummyUserAna, dummyUserJoao));

		File returnedFile = reportService.generateUserReport("", "", "", true);
		assertNotNull(returnedFile);

		returnedFile.delete();
    }

    @Test
    public void getReportsFilteredByNameTest() {
		when(mockUserRepository.searchWithFilters("Barbosa", "")).thenReturn(asList(dummyUserPedro, dummyUserAna));

		File returnedFile = reportService.generateUserReport("Barbosa", "", "", true);
		assertNotNull(returnedFile);

		returnedFile.delete();
    }

    @Test
    public void getReportsFilteredByTypeTest() {
		when(mockUserRepository.searchWithFiltersWithStatus("", "", "ADMIN")).thenReturn(asList(dummyUserPedro, dummyUserJoao));

		File returnedFile = reportService.generateUserReport("", "", "ADMIN", true);
		assertNotNull(returnedFile);

		returnedFile.delete();
    }

    @Test
    public void getZeroUsersReportTest() {
		when(mockUserRepository.searchWithFiltersWithStatus("Nonexistent User", "Nonexistent Email", "Nonexistent Status")).thenReturn(new ArrayList<User>());

		File returnedFile = reportService.generateUserReport("Nonexistent User", "Nonexistent Email", "Nonexistent Status", true);
		File expectedFile = new File(RESULT_NOT_FOUND_FILE);

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);
    }

    //=====================================
    //      Institutions Report Tests
    //=====================================
    
    @Test
    public void getAllInstitutionsTest() {
		when(mockInstitutionRepository.searchWithFiltersByDesc("", "", "")).thenReturn(asList(dummyUniville, dummySaude, dummyHospital, dummyEscola));

		File returnedFile = reportService.generateInstitutionReport("", "", "", true);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_all.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getInstitutionsFilteredByNameTest() {
		when(mockInstitutionRepository.searchWithFiltersByDesc("Municipal", "", "")).thenReturn(asList(dummyHospital, dummyEscola));

		File returnedFile = reportService.generateInstitutionReport("Municipal", "", "", true);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_filter_name.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getInstitutionsFilteredByCityTest() {
		when(mockInstitutionRepository.searchWithFiltersByDesc("", "", "ritib")).thenReturn(asList(dummySaude, dummyHospital, dummyEscola));

		File returnedFile = reportService.generateInstitutionReport("", "", "ritib", true);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_filter_city.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getInstitutionsFilteredByCityAndEmailTest() {
		when(mockInstitutionRepository.searchWithFiltersByDesc("", "pr.gov.br", "ritib")).thenReturn(asList(dummyEscola));

		File returnedFile = reportService.generateInstitutionReport("", "pr.gov.br", "ritib", true);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_filter_city_email.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getZeroInstitutionsReportTest() {
		when(mockInstitutionRepository.searchWithFiltersByDesc("Nonexistent Institution", "Nonexistent Email", "Nonexistent City")).thenReturn(new ArrayList<Institution>());

		File returnedFile = reportService.generateInstitutionReport("Nonexistent Institution", "Nonexistent Email", "Nonexistent City", true);
		File expectedFile = new File(RESULT_NOT_FOUND_FILE);

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);
    }
    
    @Test
    public void getAllInstitutionsAscTest() {
		when(mockInstitutionRepository.searchWithFiltersByAsc("", "", "")).thenReturn(asList(dummyEscola, dummyHospital, dummySaude, dummyUniville));

		File returnedFile = reportService.generateInstitutionReport("", "", "", false);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_all_asc.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }
    
    @Test
    public void getInstitutionsFilteredByNameAscTest() {
		when(mockInstitutionRepository.searchWithFiltersByAsc("Municipal", "", "")).thenReturn(asList(dummyEscola, dummyHospital));

		File returnedFile = reportService.generateInstitutionReport("Municipal", "", "", false);
		File expectedFile = new File(EXPECTED_FILES + "expected_insti_filter_name_asc.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    //===============================
    //      Orders Report Tests
    //===============================

    @Test
    public void getAllOrdersTest() {
		when(mockOrderRepository.searchBetweenAsc(ANY_START_DATE, ANY_END_DATE, ANY_START_DATE, ANY_END_DATE, "")).thenReturn(asList(dummyPedidoA, dummyPedidoB, dummyPedidoC));

		File returnedFile = reportService.generateOrderReport(null, null, null, null, "", null, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_all.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

	    returnedFile.delete();
    }

    @Test
    public void getOrdersByCreationUntilTest() {
    	when(mockOrderRepository.searchBetweenAsc(ANY_START_DATE, new Date(JAN_20_2018), ANY_START_DATE, ANY_END_DATE, "")).thenReturn(asList(dummyPedidoA, dummyPedidoB));

		File returnedFile = reportService.generateOrderReport(null, new Date(JAN_20_2018), null, null, "", null, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_creation_until.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersByCreationBetweenTest() {
    	when(mockOrderRepository.searchBetweenAsc(new Date(JAN_02_2018), new Date(FEV_05_2018), ANY_START_DATE, ANY_END_DATE, "")).thenReturn(asList(dummyPedidoB));

		File returnedFile = reportService.generateOrderReport(new Date(JAN_02_2018), new Date(FEV_05_2018), null, null, "", null, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_creation_between.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersByFinishedUntilTest() {
    	when(mockOrderRepository.searchBetweenAsc(ANY_START_DATE, ANY_END_DATE, ANY_START_DATE, new Date(JAN_20_2018), "")).thenReturn(asList(dummyPedidoA));

		File returnedFile = reportService.generateOrderReport(null, null, null, new Date(JAN_20_2018), "", null, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_finished_until.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersByFinishedBetweenTest() {
    	when(mockOrderRepository.searchBetweenAsc(ANY_START_DATE, ANY_END_DATE, new Date(FEV_05_2018), new Date(APR_01_2018), "")).thenReturn(asList(dummyPedidoB, dummyPedidoC));

		File returnedFile = reportService.generateOrderReport(null, null, new Date(FEV_05_2018), new Date(APR_01_2018), "", null, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_finished_between.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersByFinishedBetweenAndStatusTest() {
    	when(mockOrderRepository.searchBetweenWithStatusAsc(ANY_START_DATE, ANY_END_DATE, new Date(FEV_05_2018), new Date(APR_01_2018), "", 100)).thenReturn(asList(dummyPedidoC));

		File returnedFile = reportService.generateOrderReport(null, null, new Date(FEV_05_2018), new Date(APR_01_2018), "", 100, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_finished_between_status.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersByStatusAndNameTest() {
    	when(mockOrderRepository.searchBetweenWithStatusAsc(ANY_START_DATE, ANY_END_DATE, ANY_START_DATE, ANY_END_DATE, "Ana Barbosa", 200)).thenReturn(asList(dummyPedidoB));

		File returnedFile = reportService.generateOrderReport(null, null, null, null, "Ana Barbosa", 200, false);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_status_name.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    @Test
    public void getOrdersAllOrderedByDesc() {
    	when(mockOrderRepository.searchBetweenDesc(ANY_START_DATE, ANY_END_DATE, ANY_START_DATE, ANY_END_DATE, "")).thenReturn(asList(dummyPedidoC, dummyPedidoB, dummyPedidoA));

		File returnedFile = reportService.generateOrderReport(null, null, null, null, "", null, true);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_all_desc.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
	}

	@Test
	public void getOrdersByNameByDescTest() {
		when(mockOrderRepository.searchBetweenDesc(ANY_START_DATE, ANY_END_DATE, ANY_START_DATE, ANY_END_DATE, "Barbosa")).thenReturn(asList(dummyPedidoB, dummyPedidoA));

		File returnedFile = reportService.generateOrderReport(null, null, null, null, "Barbosa", null, true);
		File expectedFile = new File(EXPECTED_FILES + "expected_order_name_desc.pdf");

		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);

		assertEquals(expectedContent, returnedContent);

		returnedFile.delete();
    }

    private String getPDFContent(File file) {
    	PDFParser parser = null;
	    PDDocument pdDocument = null;
	    COSDocument cosDocument = null;
	    PDFTextStripper pdfStripper;

	    String parsedText = "";
	    try {
	        parser = new PDFParser(new FileInputStream(file));
	        parser.parse();
	        cosDocument = parser.getDocument();
	        pdfStripper = new PDFTextStripper();
	        pdDocument = new PDDocument(cosDocument);
	        parsedText = pdfStripper.getText(pdDocument);
	    } catch (Exception ex) {
	        ex.printStackTrace();

	        try {
	            if (cosDocument != null) {
	                cosDocument.close();
	            }
	            if (pdDocument != null) {
	                pdDocument.close();
	            }
	        } catch (Exception e) {
	            //ignore
	        }
	    }

	    parsedText = parsedText.replace("\n", "").replace("\r", "").replace(" ", "").toLowerCase();

	    return parsedText;
    }
    
    //===========================================
    //      Order Sum by Category Tests      
    //===========================================
    
    @Test
    public void returnOrderSumByCategoryTest() {
		when(mockOrderRepository.searchOrderSumByCategory("ria")).thenReturn(asList(orderSumCategoryA, orderSumCategoryB));
    	
    	File returnedFile = reportService.generateOrderSumByCategoryReport("ria");
		File expectedFile = new File(EXPECTED_FILES + "expected_order_sum_result.pdf");
		
		String returnedContent = getPDFContent(returnedFile);
		String expectedContent = getPDFContent(expectedFile);
		
		assertEquals(returnedContent, expectedContent);
		
		returnedFile.delete();
    	
    }
    
}