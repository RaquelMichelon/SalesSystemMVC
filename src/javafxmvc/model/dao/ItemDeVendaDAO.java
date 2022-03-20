package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
import javafxmvc.model.domain.Venda;

public class ItemDeVendaDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(ItemDeVenda itemDeVenda) {
        String sql = "INSERT INTO itensdevenda(quantidade, valor, idProduto, idVenda) VALUES(?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, itemDeVenda.getQuantidade());
            stmt.setDouble(2, itemDeVenda.getValor());
            stmt.setInt(3, itemDeVenda.getProduto().getIdProduto());
            stmt.setInt(4, itemDeVenda.getVenda().getIdVenda());
            
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ItemDeVendaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean alterar(ItemDeVenda itemDeVenda) {
        return true;
    }

    public boolean remover(ItemDeVenda itemDeVenda) {
        String sql = "DELETE FROM itensDeVenda WHERE idItemDeVenda=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, itemDeVenda.getIdItemDeVenda());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ItemDeVendaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<ItemDeVenda> listar() {
        String sql = "SELECT * FROM itensDeVenda";
        List<ItemDeVenda> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                ItemDeVenda itemDeVenda = new ItemDeVenda();
                Produto produto = new Produto();
                Venda venda = new Venda();
                itemDeVenda.setIdItemDeVenda(resultado.getInt("idItemDeVenda"));
                itemDeVenda.setQuantidade(resultado.getInt("quantidade"));
                itemDeVenda.setValor(resultado.getDouble("valor"));
                
                produto.setIdProduto(resultado.getInt("idProduto"));
                venda.setIdVenda(resultado.getInt("idVenda"));
                
                //Obtendo os dados completos do Produto associado ao Item de Venda
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.setConnection(connection);
                produto = produtoDAO.buscar(produto);
                
                VendaDAO vendaDAO = new VendaDAO();
                vendaDAO.setConnection(connection);
                venda = vendaDAO.buscar(venda);
                
                itemDeVenda.setProduto(produto);
                itemDeVenda.setVenda(venda);
                
                retorno.add(itemDeVenda);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ItemDeVendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    
    public List<ItemDeVenda> listarPorVenda(Venda venda) {
        String sql = "SELECT * FROM itensDeVenda WHERE idVenda=?";
        List<ItemDeVenda> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, venda.getIdVenda());
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                ItemDeVenda itemDeVenda = new ItemDeVenda();
                Produto produto = new Produto();
                Venda v = new Venda();
                itemDeVenda.setIdItemDeVenda(resultado.getInt("idItemDeVenda"));
                itemDeVenda.setQuantidade(resultado.getInt("quantidade"));
                itemDeVenda.setValor(resultado.getDouble("valor"));
                
                produto.setIdProduto(resultado.getInt("idProduto"));
                v.setIdVenda(resultado.getInt("idVenda"));
                
                //Obtendo os dados completos do Produto associado ao Item de Venda
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.setConnection(connection);
                produto = produtoDAO.buscar(produto);
                
                itemDeVenda.setProduto(produto);
                itemDeVenda.setVenda(v);
                
                retorno.add(itemDeVenda);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ItemDeVendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public ItemDeVenda buscar(ItemDeVenda itemDeVenda) {
        String sql = "SELECT * FROM itensDeVenda WHERE idItemDeVenda=?";
        ItemDeVenda retorno = new ItemDeVenda();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, itemDeVenda.getIdItemDeVenda());
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                Produto produto = new Produto();
                Venda venda = new Venda();
                itemDeVenda.setIdItemDeVenda(resultado.getInt("idItemDeVenda"));
                itemDeVenda.setQuantidade(resultado.getInt("quantidade"));
                itemDeVenda.setValor(resultado.getDouble("valor"));
                
                produto.setIdProduto(resultado.getInt("idProduto"));
                venda.setIdVenda(resultado.getInt("idVenda"));
                
                //Obtendo os dados completos do Cliente associado à Venda
                ProdutoDAO produtoDAO = new ProdutoDAO();
                produtoDAO.setConnection(connection);
                produto = produtoDAO.buscar(produto);
                
                VendaDAO vendaDAO = new VendaDAO();
                vendaDAO.setConnection(connection);
                venda = vendaDAO.buscar(venda);
                
                itemDeVenda.setProduto(produto);
                itemDeVenda.setVenda(venda);
                
                retorno = itemDeVenda;
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
}
