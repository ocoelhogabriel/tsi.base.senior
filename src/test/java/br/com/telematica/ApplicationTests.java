package br.com.telematica;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import br.com.telematica.seniorx.apis.IApisController;

@SpringBootTest
class ApplicationTests {

	@MockBean
	private IApisController apisController;

	@Test
	public void contextLoads() {
		assertNotNull(apisController);
	}

}
