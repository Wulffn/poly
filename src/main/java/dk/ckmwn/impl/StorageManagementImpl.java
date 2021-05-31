package dk.ckmwn.impl;

import com.mongodb.client.MongoCollection;
import dk.ckmwn.contract.ArticleManagement;
import dk.ckmwn.contract.KeywordManagement;
import dk.ckmwn.contract.StockManagement;
import dk.ckmwn.contract.StorageManagement;
import dk.ckmwn.dto.Article;
import dk.ckmwn.dto.Keyword;
import dk.ckmwn.dto.Stock;
import org.bson.Document;
import org.neo4j.driver.Driver;

import java.util.Collection;

public class StorageManagementImpl implements StorageManagement {

    private ArticleManagement am;
    private KeywordManagement km;
    private StockManagement sm;

    public StorageManagementImpl(MongoCollection<Document> articles, Driver neoDriver) {
        this.am = new ArticleManagementImpl(articles, neoDriver);
        this.km = new KeywordManagementImpl(neoDriver);
        this.sm = new StockManagementImpl(neoDriver);
    }

    @Override
    public boolean createStock(Stock stock) {
        return sm.createStock(stock);
    }

    @Override
    public boolean addKeywordToStock(Stock stock, Keyword keyword) {
        return km.addKeywordToStock(keyword, stock);
    }

    @Override
    public boolean removeKeywordFromStock(Stock stock, Keyword keyword) {
        return km.removeKeywordFromStock(keyword, stock);
    }

    @Override
    public Collection<Keyword> suggestKeywordsForStock(Stock stock, int width) {
        return null;
    }

    @Override
    public boolean createArticle(Article article) {
        return am.createArticle(article);
    }

    @Override
    public Article getArticle(String id) {
        return am.getArticle(id);
    }
}
