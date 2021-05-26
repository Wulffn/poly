package dk.ckmwn.impl;
import com.mongodb.client.MongoDatabase;
import dk.ckmwn.TestBase;
import dk.ckmwn.dto.Article;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArticleManagementTest extends TestBase {

    @BeforeAll
    public void setupAm() {
        MongoDatabase db = mongoClient.getDatabase("abc");
        db.createCollection("articles");
        am = new ArticleManagementImpl(db.getCollection("articles"), null);
    }

    @Test
    public void mustCreateArticle() {
        //Arrange
        Article article = new Article("Bla bla", "Bla", 0.87, new Date(System.currentTimeMillis()));
        //Act
        boolean res = am.createArticle(article);
        //Assert
        assertTrue(res);
        assertNotNull(article.getId());
    }

    @Test
    public void mustNotCreateArticleWithId() {
        //Arrange
        Article article = new Article("kfokdovk","Bla bla", "Bla", 0.87, new Date(System.currentTimeMillis()));
        //Act
        boolean res = am.createArticle(article);
        //Assert
        assertFalse(res);
    }

    @Test
    public void mustDeleteArticleWithExistingId() {
        //Arrange
        Article article = new Article("Bla bla", "Bla", 0.87, new Date(System.currentTimeMillis()));
        am.createArticle(article);
        String id = article.getId();
        //Act
        boolean res = am.deleteArticle(id);
        //Assert
        assertTrue(res);
    }

    @Test
    public void mustGetArticleWithValidId() {
        //Arrange
        Article article = new Article("Bla bla", "Bla", 0.87, new Date(System.currentTimeMillis()));
        am.createArticle(article);
        String id = article.getId();
        //Act
        Article res = am.getArticle(id);
        //Assert
        assertEquals(res.getId(), id);
        assertEquals(res.getContent(), article.getContent());
    }


}
