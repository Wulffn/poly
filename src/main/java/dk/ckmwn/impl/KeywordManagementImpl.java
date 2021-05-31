package dk.ckmwn.impl;

import dk.ckmwn.contract.KeywordManagement;
import dk.ckmwn.dto.Keyword;
import dk.ckmwn.dto.Stock;
import org.neo4j.driver.*;

import java.security.Key;
import java.util.Collection;

import static org.neo4j.driver.Values.parameters;

public class KeywordManagementImpl implements KeywordManagement {

    private Driver neoDriver;

    public KeywordManagementImpl(Driver neoDriver) {
        this.neoDriver = neoDriver;
    }

    @Override
    public boolean createKeyword(Keyword keyword) {
        if (keyword != null && keyword.getText() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("CREATE (k: Keyword) " +
                                        "SET k.text=$text RETURN k;",
                                parameters("id", keyword.getText()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public boolean deleteKeyword(Keyword keyword) {
        if (keyword != null && keyword.getText() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (k: Keyword) WHERE k.text = $text DELETE k;",
                                parameters("text", keyword.getText()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public Keyword getKeyword(String text) {
        if (text != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Keyword>() {
                    @Override
                    public Keyword execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (k:Keyword) WHERE k.text = $text RETURN k;",
                                parameters("text", text));
                        return new Keyword(result.single().get("text").asString());
                    }
                });
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    public boolean addKeywordToStock(Keyword keyword, Stock stock) {
        if (keyword != null && keyword.getText() != null && stock != null && stock.getSymbol() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (s:Stock {symbol: $symbol}), (k:Keyword {text: $text}) " +
                                        "CREATE (s)-[r:SearchTermOf]->(k);",
                                parameters("text", keyword.getText(), "symbol", stock.getSymbol()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public boolean removeKeywordFromStock(Keyword keyword, Stock stock) {
        if (keyword != null && keyword.getText() != null && stock != null && stock.getSymbol() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (s:Stock {symbol: $symbol})-[r:SearchTermOf]->(k:Keyword {text: $text})\n" +
                                        "DELETE r;",
                                parameters("text", keyword.getText(), "symbol", stock.getSymbol()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public Collection<Keyword> suggestKeywordsForStock(Stock stock, int width) {
        return null;
    }
}
