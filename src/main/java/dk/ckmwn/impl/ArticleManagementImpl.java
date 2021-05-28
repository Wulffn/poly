package dk.ckmwn.impl;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dk.ckmwn.contract.ArticleManagement;
import dk.ckmwn.dto.Article;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.Driver;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;


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
            DeleteResult res = articles.deleteOne(eq("_id", new ObjectId(id)));
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
            Document document = articles.find(eq("_id", new ObjectId(id))).first();
            Article article = Article.fromDoc(document);
            //return Article.fromDoc(articles.find(eq("_id", id)).first());
            return article;
        }
        return null;
    }

    @Override
    public boolean updateArticle(Article article) {
        if(article.getId() != null) {
            Document document = articles.find(eq("_id", new ObjectId(article.getId()))).first();
            if(document != null) {
                Article persistedArticle = Article.fromDoc(document);
                if (!article.getContent().equals(persistedArticle.getContent())) {
                    UpdateResult ur = articles.updateOne(eq("_id", new ObjectId(article.getId())), combine(set("content", article.getContent())));
                    if (ur.getModifiedCount() == 0) return false;
                }
                //neo
                return true;
            }
        }
        return false;
    }
}
