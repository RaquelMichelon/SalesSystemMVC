/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
import javafxmvc.model.domain.Venda;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneProcessosVendasDialogController implements Initializable {

    @FXML
    private Button buttonAdicionar;

    @FXML
    private Button buttonCancelar;

    @FXML
    private Button buttonConfirmar;

    @FXML
    private CheckBox checkBoxVendaPago;

    @FXML
    private ComboBox comboBoxVendaCliente;

    @FXML
    private ComboBox comboBoxVendaProduto;

    @FXML
    private DatePicker datePickerVendaData;

    @FXML
    private TableView<ItemDeVenda> tableViewItensDeVenda;
    
    @FXML
    private TableColumn<ItemDeVenda, Produto> tableColumnItemDeVendaProduto;

    @FXML
    private TableColumn<ItemDeVenda, Integer> tableColumnItemDeVendaValor;

    @FXML
    private TableColumn<ItemDeVenda, Double> tableColumnItemdeVendaQuantidade;

    @FXML
    private TextField textFieldVendaItemDeVendaQuantidade;

    @FXML
    private TextField textFieldVendaValor;
    
    @FXML
    private MenuItem contextMenuItemAtualizarQtd;

    @FXML
    private MenuItem contextMenuItemRemoverItem;
    
    
    private List<Cliente> listClientes;
    private List<Produto> listProdutos;
    private ObservableList<Cliente> observableListClientes;
    private ObservableList<Produto> observableListProdutos;
    private ObservableList<ItemDeVenda> observableListItensDeVenda;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Venda venda;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteDAO.setConnection(connection);
        produtoDAO.setConnection(connection);
        carregarComboBoxClientes();
        carregarComboBoxProdutos();
        tableColumnItemDeVendaProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
        tableColumnItemdeVendaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tableColumnItemDeVendaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
    } 
    
    
    //POPULAR COMBOBOX
    public void carregarComboBoxClientes() {
        listClientes = clienteDAO.listar();
        observableListClientes = FXCollections.observableArrayList(listClientes);
        comboBoxVendaCliente.setItems(observableListClientes);
    }

    public void carregarComboBoxProdutos() {
        listProdutos = produtoDAO.listar();
        observableListProdutos = FXCollections.observableArrayList(listProdutos);
        comboBoxVendaProduto.setItems(observableListProdutos);
    }
    

    //GET E SET
    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Venda getVenda() {
        return this.venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
        comboBoxVendaCliente.getSelectionModel().select(this.venda.getCliente());
        datePickerVendaData.setValue(this.venda.getData());
        checkBoxVendaPago.setSelected(this.venda.getPago());
        observableListItensDeVenda = FXCollections.observableArrayList(this.venda.getItensDeVenda());
        tableViewItensDeVenda.setItems(observableListItensDeVenda);
        textFieldVendaValor.setText(String.format("%.2f", venda.getValor()));
        
    }

    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }
    
    @FXML
    public void handleButtonAdicionar() {
        Produto produto;
        ItemDeVenda itemDeVenda = new ItemDeVenda();
        if (comboBoxVendaProduto.getSelectionModel().getSelectedItem() != null) {
            produto = (Produto) comboBoxVendaProduto.getSelectionModel().getSelectedItem();
            if (produto.getQuantidade() >= Integer.parseInt(textFieldVendaItemDeVendaQuantidade.getText())) {
                itemDeVenda.setProduto((Produto) comboBoxVendaProduto.getSelectionModel().getSelectedItem());
                itemDeVenda.setQuantidade(Integer.parseInt(textFieldVendaItemDeVendaQuantidade.getText()));
                itemDeVenda.setValor(itemDeVenda.getProduto().getPreco() * itemDeVenda.getQuantidade());
                venda.getItensDeVenda().add(itemDeVenda);
                venda.setValor(venda.getValor() + itemDeVenda.getValor());
                observableListItensDeVenda = FXCollections.observableArrayList(venda.getItensDeVenda());
                tableViewItensDeVenda.setItems(observableListItensDeVenda);
                textFieldVendaValor.setText(String.format("%.2f", venda.getValor()));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Problemas na escolha do produto!");
                alert.setContentText("N??o existe a quantidade de produtos dispon??veis no estoque!");
                alert.show();
            }
        }
    }
    
    

    @FXML
    public void handleButtonConfirmar() {
        if (validarEntradaDeDados()) {
            venda.setCliente((Cliente) comboBoxVendaCliente.getSelectionModel().getSelectedItem());
            venda.setPago(checkBoxVendaPago.isSelected());
            venda.setData(datePickerVendaData.getValue());
            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar() {
        getDialogStage().close();
    }

    //Validar entrada de dados para o cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (comboBoxVendaCliente.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Cliente inv??lido!\n";
        }
        if (datePickerVendaData.getValue() == null) {
            errorMessage += "Data inv??lida!\n";
        }
        if (observableListItensDeVenda == null) {
            errorMessage += "Itens de Venda inv??lidos!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Mostrando a mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inv??lidos, por favor, corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
    
    //desabilitar menus quando nao ha item selecionado
    @FXML
    void handleTableViewMouseClicked(MouseEvent event) {
        ItemDeVenda itemDeVenda = tableViewItensDeVenda.getSelectionModel().getSelectedItem();
        if (itemDeVenda == null) {
            contextMenuItemAtualizarQtd.setDisable(true);
            contextMenuItemRemoverItem.setDisable(true);
        } else {
            contextMenuItemAtualizarQtd.setDisable(false);
            contextMenuItemRemoverItem.setDisable(false);
        }
    }
    
    @FXML
    private void handleContextMenuItemAtualizarQtd() {
        ItemDeVenda itemDeVenda = tableViewItensDeVenda.getSelectionModel().getSelectedItem();
        int index = tableViewItensDeVenda.getSelectionModel().getSelectedIndex();
        
        Produto produto = itemDeVenda.getProduto();
        int quantidadeProdutonoBanco = produto.getQuantidade();
        int quantidadeProdutonaVenda = itemDeVenda.getQuantidade();
        int capturaInput = Integer.parseInt(inputDialog(itemDeVenda.getQuantidade()));
        //se a nova quantidade informada for menor ou igual a soma do que tem no banco com o que est?? registrado na tabela
        if (capturaInput <= (quantidadeProdutonoBanco + quantidadeProdutonaVenda)) {
            itemDeVenda.setQuantidade(capturaInput);
            venda.getItensDeVenda().set(index, itemDeVenda);
            venda.setValor(venda.getValor() - itemDeVenda.getValor());

            itemDeVenda.setValor(itemDeVenda.getProduto().getPreco() * itemDeVenda.getQuantidade());

            venda.setValor(venda.getValor() + itemDeVenda.getValor());
            tableViewItensDeVenda.refresh();
            textFieldVendaValor.setText(String.format("%.2f", venda.getValor()));
        } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Problemas na escolha do produto!");
                alert.setContentText("N??o existe a quantidade de produtos dispon??veis em estoque!");
                alert.show();
        }
   
    } 

    
    
    private String inputDialog(int value) {
        
        TextInputDialog dialog = new TextInputDialog(Integer.toString(value));
        dialog.setTitle("Entrada de dados");
        dialog.setHeaderText("Atualiza????o da Quantidade de Produtos");
        dialog.setContentText("Quantidade: ");
        
        Optional<String> result = dialog.showAndWait();
        return result.get();
    }
    
    @FXML
    private void handleContextMenuItemRemoverItem() {
        ItemDeVenda itemDeVenda = tableViewItensDeVenda.getSelectionModel().getSelectedItem();
        int index = tableViewItensDeVenda.getSelectionModel().getSelectedIndex();

        venda.setValor(venda.getValor() - itemDeVenda.getValor());
        venda.getItensDeVenda().remove(index);
        
        observableListItensDeVenda = FXCollections.observableArrayList(venda.getItensDeVenda());
        tableViewItensDeVenda.setItems(observableListItensDeVenda);
        
        textFieldVendaValor.setText(String.format("%.2f", venda.getValor()));
   
    } 
    

    
}
