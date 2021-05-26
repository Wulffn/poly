package dk.ckmwn.impl;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import dk.ckmwn.contract.ArticleManagement;
import dk.ckmwn.dto.Article;
import org.bson.BsonDocument;
import org.bson.Document;
import org.neo4j.driver.Driver;

import static com.mongodb.client.model.Filters.eq;

public class ArticleManagementImpl implements ArticleManagement {

    private MongoCollection<Document> articles;

    public ArticleManagementImpl(MongoCollection<Document> articles, Driver neoDriver) {
        this.articles = articles;
    }

    @Override
    public boolean createArticle(Article article) {
        if(article.getId() == null) {
            Document doc = new Document("content", article.getContent());
            articles.insertOne(doc);
            String id = doc.get("_id").toString();
            if(id != null) {
                article.setId(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteArticle(String id) {
        if(id != null) {
            Article article = getArticle(id);
            if(article == null) return false;
            DeleteResult res = articles.deleteOne(eq("_id", id));
            if(res.getDeletedCount() == 1) {
                //Neo
                return true;
            }
        }
        return false;
    }

    @Override
    public Article getArticle(String id) {
        if(id != null) {
            return Article.fromDoc(articles.find(eq("_id", id)).first());
        }
        return null;
    }

    @Override
    public Article updateArticle(Article article) {
        return null;
    }
}
