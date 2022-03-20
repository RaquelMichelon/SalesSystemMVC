package javafxmvc.model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafxmvc.model.domain.Cliente;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
import javafxmvc.model.domain.Venda;

public class VendaDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Venda venda) {
        String sql = "INSERT INTO vendas(data, valor, pago, idCliente) VALUES(?,?,?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(venda.getData()));
            stmt.setDouble(2, venda.getValor());
            stmt.setBoolean(3, venda.getPago());
            stmt.setInt(4, venda.getCliente().getIdCliente());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean alterar(Venda venda) {
        String sql = "UPDATE vendas SET data=?, valor=?, pago=?, idCliente=? WHERE idVenda=?";
        try {
            //primeiro busco a venda anterior do banco e mantém na variável vendaAnterior
            connection.setAutoCommit(false);
            ItemDeVendaDAO itemDeVendaDAO = new ItemDeVendaDAO();
            itemDeVendaDAO.setConnection(connection);
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.setConnection(connection);

            //remover todos os itens de venda da venda anterior
            Venda vendaAnterior = buscar(venda.getIdVenda());
            List<ItemDeVenda> itensDeVenda = itemDeVendaDAO.listarPorVenda(vendaAnterior);
            int quantidadeAnteriorDeProd;
            int quantidadeDoProdutoNoBanco;
            
            for (ItemDeVenda itemVenda : itensDeVenda) {
                Produto produto = itemVenda.getProduto();
                quantidadeAnteriorDeProd = itemVenda.getQuantidade(); //guarda a quantidade anterior 

                quantidadeDoProdutoNoBanco = produtoDAO.buscar(produto.getIdProduto()); //retorna a quantidade oque está no banco             
                produto.setQuantidade(quantidadeDoProdutoNoBanco + quantidadeAnteriorDeProd);               
                produtoDAO.alterar(produto);    
                itemDeVendaDAO.remover(itemVenda);                
            }
            
            //só agora atualiza os dados da venda
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(venda.getData()));
            stmt.setDouble(2, venda.getValor());
            stmt.setBoolean(3, venda.getPago());
            stmt.setInt(4, venda.getCliente().getIdCliente());
            stmt.setInt(5, venda.getIdVenda()); 
            stmt.execute();
            
            for (ItemDeVenda iv : venda.getItensDeVenda()) {
                Produto produto = iv.getProduto();               
                quantidadeDoProdutoNoBanco = produtoDAO.buscar(produto.getIdProduto());                                
                int quantidadeCapturadaAlterarBanco = produto.getQuantidade();                              
                produto.setQuantidade(quantidadeDoProdutoNoBanco - iv.getQuantidade());                 
                produtoDAO.alterar(produto);
                itemDeVendaDAO.inserir(iv);                
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException ex) {
            
            try {
                
                connection.rollback();            
            } catch (SQLException ex1) {
                Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }            
    }

    public boolean remover(Venda venda) {
        String sql = "DELETE FROM vendas WHERE idVenda=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, venda.getIdVenda());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<Venda> listar() {
        String sql = "SELECT * FROM vendas";
        List<Venda> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Venda venda = new Venda();
                Cliente cliente = new Cliente();
                List<ItemDeVenda> itensDeVenda = new ArrayList();

                venda.setIdVenda(resultado.getInt("idVenda"));
                venda.setData(resultado.getDate("data").toLocalDate());
                venda.setValor(resultado.getDouble("valor"));
                venda.setPago(resultado.getBoolean("pago"));
                cliente.setIdCliente(resultado.getInt("idCliente"));

                //Obtendo os dados completos do Cliente associado à Venda
                ClienteDAO clienteDAO = new ClienteDAO();
                clienteDAO.setConnection(connection);
                cliente = clienteDAO.buscar(cliente);

                //Obtendo os dados completos dos Itens de Venda associados à Venda
                ItemDeVendaDAO itemDeVendaDAO = new ItemDeVendaDAO();
                itemDeVendaDAO.setConnection(connection);
                itensDeVenda = itemDeVendaDAO.listarPorVenda(venda);

                venda.setCliente(cliente);
                venda.setItensDeVenda(itensDeVenda);
                retorno.add(venda);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public Venda buscar(Venda venda) {
        String sql = "SELECT * FROM vendas WHERE idVenda=?";
        Venda retorno = new Venda();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, venda.getIdVenda());
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                Cliente cliente = new Cliente();
                venda.setIdVenda(resultado.getInt("idVenda"));
                venda.setData(resultado.getDate("data").toLocalDate());
                venda.setValor(resultado.getDouble("valor"));
                venda.setPago(resultado.getBoolean("pago"));
                cliente.setIdCliente(resultado.getInt("idCliente"));
                venda.setCliente(cliente);
                retorno = venda;
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    
    //buscar anterior
    public Venda buscar(int idVenda) {
        String sql = "SELECT * FROM vendas WHERE idVenda=?";
        Venda retorno = new Venda();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idVenda);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                Cliente cliente = new Cliente();
                retorno.setIdVenda(resultado.getInt("idVenda"));
                retorno.setData(resultado.getDate("data").toLocalDate());
                retorno.setValor(resultado.getDouble("valor"));
                retorno.setPago(resultado.getBoolean("pago"));
                cliente.setIdCliente(resultado.getInt("idCliente"));
                retorno.setCliente(cliente);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    
    
    public Venda buscarUltimaVenda() {
        String sql = "SELECT max(idVenda) as ultima FROM vendas";
        Venda retorno = new Venda();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();

            if (resultado.next()) {
                retorno.setIdVenda(resultado.getInt("ultima"));
                return retorno;
            }
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public Map<Integer, ArrayList> listarQuantidadeVendasPorMes() {
        String sql = "select count(idVenda) as quantidadeVendas, extract(year from data) as ano, extract(month from data) as mes from vendas group by ano, mes order by ano, mes";
        Map<Integer, ArrayList> retorno = new HashMap();
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                ArrayList linha = new ArrayList();
                if (!retorno.containsKey(resultado.getInt("ano")))
                {
                    linha.add(resultado.getInt("mes"));
                    linha.add(resultado.getInt("quantidadeVendas"));
                    retorno.put(resultado.getInt("ano"), linha);
                }else{
                    ArrayList linhaNova = retorno.get(resultado.getInt("ano"));
                    linhaNova.add(resultado.getInt("mes"));
                    linhaNova.add(resultado.getInt("quantidadeVendas"));
                }
            }
            return retorno;
        } catch (SQLException ex) {
            Logger.getLogger(VendaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
}
