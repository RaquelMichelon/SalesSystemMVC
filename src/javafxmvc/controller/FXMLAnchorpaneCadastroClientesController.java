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
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorpaneCadastroClientesController implements Initializable {

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;

    @FXML
    private Label labelClienteCPF;

    @FXML
    private Label labelClienteCodigo;

    @FXML
    private Label labelClienteNome;

    @FXML
    private Label labelClienteTelefone;
    
    @FXML
    private TableView<Cliente> tableViewClientes;

    @FXML
    private TableColumn<Cliente, String> tableColumnClienteNome;

    @FXML
    private TableColumn<Cliente, String> tableColumnClienteCPF;

    private List<Cliente> listClientes;
    
    private ObservableList<Cliente> observableListClientes;
    
    
    //ATRIBUTOS PARA MANIPULAR O DB
    //fabrica de db, me forneca uma instancia postgresql
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    //a classe ClienteDAO possui metodos basicos para fazer o CRUD
    private final ClienteDAO clienteDAO = new ClienteDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //seta a conexao que foi aberta
        clienteDAO.setConnection(connection);
        // Chamar o método tableview de clientes
        carregarTableViewClientes();
        
        
        //listen
        tableViewClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewClientes(newValue));
    }   
    
    public void carregarTableViewClientes() {
        
        //configurar o que cada campo da coluna vai exibir
        tableColumnClienteNome.setCellValueFactory(new PropertyValueFactory<> ("nome"));
        tableColumnClienteCPF.setCellValueFactory(new PropertyValueFactory<> ("cpf"));
        
        listClientes = clienteDAO.listar();
        
        //converte o listClientes em um observavel
        observableListClientes = FXCollections.observableArrayList(listClientes);
        
        tableViewClientes.setItems(observableListClientes);

    }
    
    //o tableview eh do tipo cliente entao cada item do tableview tbm
    public void selecionarItemTableViewClientes(Cliente cliente) {
        if (cliente != null) {
            labelClienteCodigo.setText(String.valueOf(cliente.getIdCliente()));
            labelClienteNome.setText(cliente.getNome());
            labelClienteCPF.setText(cliente.getCpf());
            labelClienteTelefone.setText(cliente.getTelefone());
        } else {
            labelClienteCodigo.setText("");
            labelClienteNome.setText("");
            labelClienteCPF.setText("");
            labelClienteTelefone.setText("");
        }

    }
    
    //EVENTOS DOS BOTOES INSERIR ALTERAR E REMOVER
    @FXML
    public void handleButtonInserir() throws IOException {
        Cliente cliente = new Cliente();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosClientesDialog(cliente);
        if (buttonConfirmarClicked) {
            clienteDAO.inserir(cliente);
            carregarTableViewClientes();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosClientesDialog(cliente);
            if (buttonConfirmarClicked) {
                clienteDAO.alterar(cliente);
                carregarTableViewClientes();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um cliente na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() throws IOException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            clienteDAO.remover(cliente);
            carregarTableViewClientes();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um cliente na Tabela!");
            alert.show();
        }
        
        if (clienteDAO.remover(cliente) == false) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Não é possível excluir este cliente por estar atrelado a uma venda!");
            alert.show();
        }
    }
    
    public boolean showFXMLAnchorPaneCadastrosClientesDialog(Cliente cliente) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorpaneCadastroClientesDialogController.class.getResource("/javafxmvc/view/FXMLAnchorpaneCadastroClientesDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Cria Stage Dialog
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Clientes");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        // Seta o cliente
        FXMLAnchorpaneCadastroClientesDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCliente(cliente);

        // Mostra o Dialog e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();

    }
    
    
}
