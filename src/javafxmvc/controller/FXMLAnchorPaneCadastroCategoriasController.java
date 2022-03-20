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
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;
import javafxmvc.model.domain.Cliente;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneCadastroCategoriasController implements Initializable {

    @FXML
    private Label labelCategoriaCodigo;

    @FXML
    private Label labelCategoriaDescricao;

    @FXML
    private TableColumn<Categoria, String> tableColumnCategoriaDescricao;

    @FXML
    private TableView<Categoria> tableViewCategorias;

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;
    
    //para retornar uma lista com todas as categorias que estao no DB
    private List<Categoria> listCategorias;
    
    private ObservableList<Categoria> observableListCategorias;
    
    //ATRIBUTOS PARA MANIPULAR O DB
    //fabrica de db, me forneca uma instancia postgresql
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    //a classe ClienteDAO possui metodos basicos para fazer o CRUD
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //seta a conexao que foi aberta
        categoriaDAO.setConnection(connection);
        // Chamar o método tableview de clientes
        carregarTableViewCategorias();
        
        
        //listen
        tableViewCategorias.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewCategorias(newValue));
    }


    public void carregarTableViewCategorias() {
        
        tableColumnCategoriaDescricao.setCellValueFactory(new PropertyValueFactory<> ("descricao"));
        
        listCategorias = categoriaDAO.listar();
        
        observableListCategorias = FXCollections.observableArrayList(listCategorias);
        
        tableViewCategorias.setItems(observableListCategorias);

    }
    
    
    public void selecionarItemTableViewCategorias(Categoria categoria) {
        if (categoria != null) {
            labelCategoriaCodigo.setText(String.valueOf(categoria.getIdCategoria()));
            labelCategoriaDescricao.setText(categoria.getDescricao());
        } else {
            labelCategoriaCodigo.setText("");
            labelCategoriaDescricao.setText("");
        }

    }
    
    
    //EVENTOS DOS BOTOES INSERIR ALTERAR E REMOVER
    @FXML
    public void handleButtonInserir() throws IOException {
        Categoria categoria = new Categoria();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosCategoriasDialog(categoria);
        if (buttonConfirmarClicked) {
            categoriaDAO.inserir(categoria);
            carregarTableViewCategorias();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Categoria categoria = tableViewCategorias.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosCategoriasDialog(categoria);
            if (buttonConfirmarClicked) {
                categoriaDAO.alterar(categoria);
                carregarTableViewCategorias();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() throws IOException {
        Categoria categoria = tableViewCategorias.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            categoriaDAO.remover(categoria);
            carregarTableViewCategorias();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um cliente na Tabela!");
            alert.show();
        }
        
        if (categoriaDAO.remover(categoria) == false) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Não é possível excluir esta categoria por estar atrelada a uma venda!");
            alert.show();
        }
    }
    
    public boolean showFXMLAnchorPaneCadastrosCategoriasDialog(Categoria categoria) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroCategoriasDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastroCategoriasDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Cria Stage Dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Categorias");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Seta a categoria
        FXMLAnchorPaneCadastroCategoriasDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCategoria(categoria);

        // Mostra o Dialog e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();

    }
    
}
