/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Produto;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneCadastroProdutosController implements Initializable {

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;

    @FXML
    private Label labelProdutoCategoria;

    @FXML
    private Label labelProdutoCodigo;

    @FXML
    private Label labelProdutoNome;

    @FXML
    private Label labelProdutoPreco;

    @FXML
    private Label labelProdutoQuantidade;

    @FXML
    private TableColumn<Produto, String> tableColumnProdutoNome;

    @FXML
    private TableColumn<Produto, String> tableColumnProdutoPreco;
    
    @FXML
    private TableView<Produto> tableViewProdutos;
    
    private List<Produto> listProdutos;
    
    private ObservableList<Produto> observableListProdutos;
    
    
    //ATRIBUTOS PARA MANIPULAR O DB
    //fabrica de db, me forneca uma instancia postgresql
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produtoDAO.setConnection(connection);
        // Chamar o método tableview de clientes
        carregarTableViewProdutos();
        
        
        //listen
        tableViewProdutos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewProdutos(newValue));
    }
    
    
    public void carregarTableViewProdutos() {
        
        //configurar o que cada campo da coluna vai exibir
        tableColumnProdutoNome.setCellValueFactory(new PropertyValueFactory<> ("nome"));
        tableColumnProdutoPreco.setCellValueFactory(new PropertyValueFactory<> ("preco"));
        
        listProdutos = produtoDAO.listar();
        
        //converte o listClientes em um observavel
        observableListProdutos = FXCollections.observableArrayList(listProdutos);
        
        tableViewProdutos.setItems(observableListProdutos);

    }
    
    public void selecionarItemTableViewProdutos(Produto produto) {
        if (produto != null) {
            labelProdutoCodigo.setText(String.valueOf(produto.getIdProduto()));
            labelProdutoNome.setText(produto.getNome());
            labelProdutoPreco.setText(String.format("%.2f", produto.getPreco()));
            labelProdutoQuantidade.setText(String.valueOf(produto.getQuantidade()));
            labelProdutoCategoria.setText(String.valueOf(produto.getCategoria()));

        } else {
            labelProdutoCodigo.setText("");
            labelProdutoNome.setText("");
            labelProdutoPreco.setText("");
            labelProdutoQuantidade.setText("");
            labelProdutoCategoria.setText("");

        }

    }
    
    //EVENTOS DOS BOTOES INSERIR ALTERAR E REMOVER
    @FXML
    public void handleButtonInserir() throws IOException {
        Produto produto = new Produto();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
        if (buttonConfirmarClicked) {
            produtoDAO.inserir(produto);
            carregarTableViewProdutos();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Produto produto = tableViewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosProdutosDialog(produto);
            if (buttonConfirmarClicked) {
                produtoDAO.alterar(produto);
                carregarTableViewProdutos();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() throws IOException {
        Produto produto = tableViewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            produtoDAO.remover(produto);
            carregarTableViewProdutos();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela!");
            alert.show();
        }
        
        if (produtoDAO.remover(produto) == false) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Não é possível excluir este produto por estar atrelado a uma venda!");
            alert.show();
        }
                
    }
    
    public boolean showFXMLAnchorPaneCadastrosProdutosDialog(Produto produto) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroProdutosDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastroProdutosDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Cria Stage Dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Produtos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Seta o cliente
        FXMLAnchorPaneCadastroProdutosDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProduto(produto);

        // Mostra o Dialog e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();

    }


    
    
}
