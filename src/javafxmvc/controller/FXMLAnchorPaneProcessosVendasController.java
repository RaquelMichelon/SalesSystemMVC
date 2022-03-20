/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.ItemDeVendaDAO;
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.dao.VendaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
import javafxmvc.model.domain.Venda;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneProcessosVendasController implements Initializable {

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;

    @FXML
    private Label labelVendaCliente;

    @FXML
    private Label labelVendaCodigo;

    @FXML
    private Label labelVendaData;

    @FXML
    private Label labelVendaPago;

    @FXML
    private Label labelVendaValor;

    @FXML
    private TableColumn<Venda, Venda> tableColumnVendaCliente;

    @FXML
    private TableColumn<Venda, Integer> tableColumnVendaCodigo;

    @FXML
    private TableColumn<Venda, LocalDate> tableColumnVendaData;

    @FXML
    private TableView<Venda> tableViewVendas;
    
    private List<Venda> listVendas;
    private ObservableList<Venda> observableListVendas;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final VendaDAO vendaDAO = new VendaDAO();
    private final ItemDeVendaDAO itemDeVendaDAO = new ItemDeVendaDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vendaDAO.setConnection(connection);
        carregarTableViewVendas();
        
        //para mostrar os campos vazios caso nao estejam selecionados
        selecionarItemTableViewVendas(null);

        // Listen
        tableViewVendas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewVendas(newValue));
    }
    
    public void selecionarItemTableViewVendas(Venda venda) {
        if (venda != null) {
            labelVendaCodigo.setText(String.valueOf(venda.getIdVenda()));
            labelVendaData.setText(String.valueOf(venda.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            labelVendaValor.setText(String.format("%.2f", venda.getValor()));
            //labelVendaPago.setText(String.valueOf(venda.getPago())); 
            if (venda.getPago() == true) {
                labelVendaPago.setText("Sim");   
            } else {
                labelVendaPago.setText("Não");   
            }
            
            //labelVendaPago.setText(String.valueOf(venda.getPago()));
            labelVendaCliente.setText(venda.getCliente().toString());
        } else {
            labelVendaCodigo.setText("");
            labelVendaData.setText("");
            labelVendaValor.setText("");
            labelVendaPago.setText("");
            labelVendaCliente.setText("");
        }
    }

    public void carregarTableViewVendas() {
        tableColumnVendaCodigo.setCellValueFactory(new PropertyValueFactory<>("idVenda"));
        tableColumnVendaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        tableColumnVendaCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        listVendas = vendaDAO.listar();

        observableListVendas = FXCollections.observableArrayList(listVendas);
        tableViewVendas.setItems(observableListVendas);
    }
    
    @FXML
    public void handleButtonInserir(ActionEvent event) throws IOException, SQLException {
        Venda venda = new Venda();
        List<ItemDeVenda> listItensDeVenda = new ArrayList<>();
        venda.setItensDeVenda(listItensDeVenda);
        boolean buttonConfirmarClicked = showFXMLAnchorPaneProcessosVendasDialog(venda);
        if (buttonConfirmarClicked) {
            //so vai inserir a venda na tabela quando chamar o commit
            try {
                connection.setAutoCommit(false);
                vendaDAO.setConnection(connection);
                vendaDAO.inserir(venda);
                itemDeVendaDAO.setConnection(connection);
                produtoDAO.setConnection(connection);
                for (ItemDeVenda listItemDeVenda : venda.getItensDeVenda()) {
                    Produto produto = listItemDeVenda.getProduto();
                    listItemDeVenda.setVenda(vendaDAO.buscarUltimaVenda());
                    itemDeVendaDAO.inserir(listItemDeVenda);
                    produto.setQuantidade(produto.getQuantidade() - listItemDeVenda.getQuantidade());
                    produtoDAO.alterar(produto);
                }
                connection.commit();
                carregarTableViewVendas();
            } catch (SQLException ex) {
                try {
                    connection.rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(FXMLAnchorPaneProcessosVendasController.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(FXMLAnchorPaneProcessosVendasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    public void handleButtonAlterar(ActionEvent event) throws IOException {
        Venda venda = tableViewVendas.getSelectionModel().getSelectedItem();
        if (venda != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneProcessosVendasDialog(venda);
            if (buttonConfirmarClicked) {
                vendaDAO.alterar(venda);
                carregarTableViewVendas();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma venda na Tabela!");
            alert.show();
        }
    }
    
    
    @FXML
    public void handleButtonRemover() throws IOException, SQLException {
        Venda venda = tableViewVendas.getSelectionModel().getSelectedItem();
        if (venda != null) {
            connection.setAutoCommit(false);
            vendaDAO.setConnection(connection);
            itemDeVendaDAO.setConnection(connection);
            produtoDAO.setConnection(connection);
            //primeiro deleta todos os itens de venda e depois a venda
            for (ItemDeVenda listItemDeVenda : venda.getItensDeVenda()) {
                Produto produto = listItemDeVenda.getProduto();
                produto.setQuantidade(produto.getQuantidade() + listItemDeVenda.getQuantidade());
                //para colocar os itens cancelados de volta
                produtoDAO.alterar(produto);
                itemDeVendaDAO.remover(listItemDeVenda);
            }
            vendaDAO.remover(venda);
            connection.commit();
            carregarTableViewVendas();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma venda na Tabela!");
            alert.show();
        }
        
    }
    
    public boolean showFXMLAnchorPaneProcessosVendasDialog(Venda venda) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneProcessosVendasDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneProcessosVendasDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        //Stage Dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Registro de Vendas");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        // Seta a Venda no Controller
        FXMLAnchorPaneProcessosVendasDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setVenda(venda);
        
        // Mostra o Dialog e aguarda até o user fechar
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }
    
  
}
