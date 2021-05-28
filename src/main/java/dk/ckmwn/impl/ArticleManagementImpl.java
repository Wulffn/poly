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
import org.neo4j.driver.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.neo4j.driver.Values.parameters;


public class ArticleManagementImpl implements ArticleManagement {

    private MongoCollection<Document> articles;
    private Driver neoDriver;

    public ArticleManagementImpl(MongoCollection<Document> articles, Driver neoDriver) {
        this.articles = articles;
        this.neoDriver = neoDriver;
    }

    /**
     * Opretter artikel i MongoDB.
     * Article.id vil være opdateret med id fra MongoDB hvis persistering lykkes.
     * @param article Article object
     * @return true hvis article persisteres ellers false.
     */
    private boolean createMongoArticle(Article article)
    {
        if(article.getId() == null)
        {
            Document doc = new Document("content", article.getContent());
            articles.insertOne(doc);
            String id = doc.get("_id").toString();
            if (id != null)
            {
                article.setId(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createArticle(Article article)
    {
        return createMongoArticle(article) && createNeoArticle(article);
    }


    private boolean createNeoArticle(Article article) {

        if (article != null && article.getId() != null)
        {
            try (Session session = neoDriver.session())
            {
                return session.writeTransaction(new TransactionWork<Boolean>()
                {
                    @Override
                    public Boolean execute(Transaction transaction)
                    {
                        Result result = transaction.run("CREATE (a: Article) " +
                                        "SET a.id=$id, a.createdAt=$createdAt, a.summary=$summary, a.rating=$rating RETURN a;",
                                parameters("id", article.getId(),
                                        "createdAt", article.getCreatedAt().toString(),
                                        "summary", article.getSummary(),
                                        "rating", article.getRating()));


                        return true;
                    }
                });
            }
            catch (Exception e) {}
        }

        return false;
    }

    @Override
    public boolean deleteArticle(String id){
        return deleteMongoArticle(id) && deleteNeoArticle(id);
    }


    private boolean deleteMongoArticle(String id) {
        if(id != null) {
            Article article = getArticle(id);
            if(article != null)
            {
                DeleteResult res = articles.deleteOne(eq("_id", new ObjectId(id)));
                return res.getDeletedCount() == 1;
            }
        }
        return false;
    }

   public boolean deleteNeoArticle(String id)
   {
       return true;
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
