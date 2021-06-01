package dk.ckmwn.impl;

import dk.ckmwn.TestBase;
import dk.ckmwn.dto.Keyword;
import dk.ckmwn.dto.Stock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KeywordManagementTest extends TestBase {

    @BeforeAll
    public void setupKm() {
//        MongoDatabase db = mongoClient.getDatabase("abc");
//        db.createCollection("articles");
        km = new KeywordManagementImpl(neoDriver);
        sm = new StockManagementImpl(neoDriver);
    }

    @Test
    public void mustCreateKeyword() {
        //Arrange
        Keyword keyword = new Keyword("Elon Musk");
        //Act
        boolean res = km.createKeyword(keyword);
        //Assert
        assertTrue(res);
    }

    @Test
    public void mustNotCreateKeywordWithNullText() {
        //Arrange
        Keyword keyword = new Keyword(null);
        //Act
        boolean res = km.createKeyword(keyword);
        //Assert
        assertFalse(res);
    }

    @Test
    public void mustDeleteKeyword() {
        //Arrange
        Keyword keyword = new Keyword("Elon Musk");
        km.createKeyword(keyword);
        //Act
        boolean res = km.deleteKeyword(keyword);
        //Assert
        assertTrue(res);
    }

    @Test
    public void mustNotDeleteKeywordWithNullText() {
        //Arrange
        Keyword keyword = new Keyword(null);
        km.createKeyword(keyword);
        //Act
        boolean res = km.deleteKeyword(keyword);
        //Assert
        assertFalse(res);
    }

//    @Test
//    public void mustGetKeywordFromString() {
//        //Arrange
//        Keyword keyword = new Keyword("Elon Musk");
//        km.createKeyword(keyword);
//        //Act
//        Keyword persistedKeyword = km.getKeyword("Elon Musk");
//        //Assert
//        assertEquals(persistedKeyword.getText(), keyword.getText());
//    }

    @Test
    public void mustAddKeywordToStock() {
        //Arrange
        Keyword keyword = new Keyword("Elon Musk");
        Stock stock = new Stock("Tesla");
        km.createKeyword(keyword);
        sm.createStock(stock);
        //Act
        boolean res = km.addKeywordToStock(keyword, stock);
        //Assert
        assertTrue(res);
    }

    @Test
    public void mustRemoveKeywordFromStock() {
        //Arrange
        Keyword keyword = new Keyword("Elon Musk");
        Stock stock = new Stock("Tesla");
        km.createKeyword(keyword);
        sm.createStock(stock);
        //Act
        boolean res = km.removeKeywordFromStock(keyword, stock);
        //Assert
        assertTrue(res);
    }

}
