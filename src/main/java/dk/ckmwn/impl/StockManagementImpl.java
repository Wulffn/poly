package dk.ckmwn.impl;

import dk.ckmwn.contract.StockManagement;
import dk.ckmwn.dto.Stock;
import org.neo4j.driver.*;
import static org.neo4j.driver.Values.parameters;

public class StockManagementImpl implements StockManagement {

    private Driver neoDriver;

    public StockManagementImpl(Driver neoDriver) {
        this.neoDriver = neoDriver;
    }

    @Override
    public boolean createStock(Stock stock) {
        if (stock != null && stock.getSymbol() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("CREATE (s: Stock) " +
                                        "SET s.symbol=$symbol RETURN s;",
                                parameters("symbol", stock.getSymbol()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public boolean deleteStock(Stock stock) {
        if (stock != null && stock.getSymbol() != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (s: Stock) WHERE s.symbol = $symbol DELETE s;",
                                parameters("symbol", stock.getSymbol()));
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
        return false;
    }

    @Override
    public Stock getStock(String symbol) {
        if (symbol != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Stock>() {
                    @Override
                    public Stock execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (s: Stock) WHERE s.symbol = $symbol RETURN s;",
                                parameters("symbol", symbol));
                        return new Stock(result.single().get("symbol").asString());
                    }
                });
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Override
    public boolean updateStock(String fromSymbol, String toSymbol) {
        if (fromSymbol != null && toSymbol != null) {
            try (Session session = neoDriver.session()) {
                return session.writeTransaction(new TransactionWork<Boolean>() {
                    @Override
                    public Boolean execute(Transaction transaction) {
                        Result result = transaction.run("MATCH (s: Stock) WHERE s.symbol = $fromSymbol " +
                                        "SET s.symbol=$toSymbol RETURN s;",
                                parameters("fromSymbol", fromSymbol, "toSymbol", toSymbol));
                        return true;
                    }
                });
            } catch (Exception e) {
            }

        }
        return false;
    }
}

