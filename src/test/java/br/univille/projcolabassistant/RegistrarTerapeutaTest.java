package br.univille.projcolabassistant;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import br.univille.projcolabassistant.controller.RegisterController;



@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrarTerapeutaTest {


    @Autowired
    private RegisterController registerController;
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void contextLoads() {
        //Verifica a existência da instância do controlador
             assertThat(registerController).isNotNull();
    }
    

    @Test
    public void registerController() throws Exception {
        //Teste do método index
        this.mockMvc.perform(get("/user")).andExpect(status().isOk())
        .andExpect(xpath("/html/body/div/div/table").exists());
    }
    
    @Test
    public void pacienteControllerSaveTest() throws Exception {
        this.mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("form", "")
                .content("id=0&nome=Bruno Henrique Cristofolini&email=bruno@gmail.com&phone=(47) 9 8897-7354&address=Rua Tajsdsaj&city=Joinville"))
                .andDo(print())
                .andExpect(status().isMovedTemporarily())
                .andExpect(view().name("redirect:/user"));
        
        this.mockMvc.perform(get("/user")).andDo(print()).andExpect(status().isOk())
        .andExpect(xpath("/html/body/div/div/table/tbody/tr/td[1]/text()").string("Bruno Henrique Cristofolini"))
        .andExpect(xpath("/html/body/div/div/table/tbody/tr/td[2]/text()").string("bruno@gmail.com"));
        andExpect(xpath("/html/body/div/div/table/tbody/tr/td[3]/text()").string("(47) 9 8897-7354"));
        andExpect(xpath("/html/body/div/div/table/tbody/tr/td[4]/text()").string("Rua Tajsdsaj"));
        andExpect(xpath("/html/body/div/div/table/tbody/tr/td[5]/text()").string("Joinville"));
            
    }


	private void andExpect(ResultMatcher string) {
		// TODO Auto-generated method stub
		
	}
    

}