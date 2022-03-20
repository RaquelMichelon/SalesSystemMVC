/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;
import javafxmvc.model.domain.Produto;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneCadastroProdutosDialogController implements Initializable {

    @FXML
    private Button buttonCancelar;

    @FXML
    private Button buttonConfirmar;

    @FXML
    private ComboBox<Categoria> comboBoxProdutoCategoria;

    @FXML
    private TextField textFieldProdutoNome;

    @FXML
    private TextField textFieldProdutoPreco;

    @FXML
    private TextField textFieldProdutoQuantidade;
    
    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Produto produto;

    private List<Categoria> listCategorias;
    private ObservableList<Categoria> observableListCategorias;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoriaDAO.setConnection(connection);
        carregarComboBoxCategorias();
    }

    //POPULAR COMBOBOX
    public void carregarComboBoxCategorias() {
        listCategorias = categoriaDAO.listar();
        observableListCategorias = FXCollections.observableArrayList(listCategorias);
        comboBoxProdutoCategoria.setItems(observableListCategorias);
    }

    /**
     * @return the dialogStage
     */
    public Stage getDialogStage() {
        return dialogStage;
    }

    /**
     * @param dialogStage the dialogStage to set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * @return the buttonConfirmarClicked
     */
    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    /**
     * @param buttonConfirmarClicked the buttonConfirmarClicked to set
     */
    public void setButtonConfirmarClicked(boolean buttonConfirmarClicked) {
        this.buttonConfirmarClicked = buttonConfirmarClicked;
    }

    /**
     * @return the produto
     */
    public Produto getProduto() {
        return produto;
    }

    /**
     * @param produto the produto to set
     */
    public void setProduto(Produto produto) {
        this.produto = produto;
        this.textFieldProdutoNome.setText(produto.getNome());
        this.textFieldProdutoQuantidade.setText(Integer.toString(produto.getQuantidade()));
        this.textFieldProdutoPreco.setText(Double.toString(produto.getPreco()));
        this.comboBoxProdutoCategoria.getSelectionModel().select(produto.getCategoria());
    }
    
    //IMPLEMENTAR EVENTOS DE CONFIMAR E CANCELAR
    @FXML
    public void handleButtonConfirmar() {
        
        if (validarEntradaDeDados()) {
            produto.setNome(textFieldProdutoNome.getText());
            produto.setQuantidade(Integer.parseInt(textFieldProdutoQuantidade.getText()));
            produto.setPreco(Double.parseDouble(textFieldProdutoPreco.getText()));
            produto.setCategoria(comboBoxProdutoCategoria.getSelectionModel().getSelectedItem());


            buttonConfirmarClicked = true;
            dialogStage.close();
        }

    }
    
    @FXML
    public void handleButtonCancelar() {

        dialogStage.close();

    }
    
    
    //VALIDA ENTRADA DE DADOS DO CADASTRO
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if (textFieldProdutoNome.getText() == null || textFieldProdutoNome.getText().length() == 0) {
            errorMessage += "Nome de Produto inválido!\n";
        }
        if (textFieldProdutoQuantidade.getText() == null || textFieldProdutoQuantidade.getText().length() == 0) {
            errorMessage += "Quantidade inválida!\n";
        }
        if (textFieldProdutoPreco.getText() == null || textFieldProdutoPreco.getText().length() == 0) {
            errorMessage += "Preço inválido!\n";
        }
        if (comboBoxProdutoCategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Categoria inválida!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Mostrando a mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor, corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
    
    
    
    
    
    
}
