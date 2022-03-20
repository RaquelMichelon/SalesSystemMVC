/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.Categoria;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLAnchorPaneCadastroCategoriasDialogController implements Initializable {

    @FXML
    private Button buttonCancelar;

    @FXML
    private Button buttonConfirmar;

    @FXML
    private Label labelCategoriaDescricao;

    @FXML
    private TextField textFieldCategoriaNome;
    
    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Categoria categoria;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
     * @return the categoria
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * @param categoria the categoria to set
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    
    //IMPLEMENTAR EVENTOS DE CONFIMAR E CANCELAR
    @FXML
    public void handleButtonConfirmar() {
        
        if (validarEntradaDeDados()) {
            categoria.setDescricao(textFieldCategoriaNome.getText());

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

        if (textFieldCategoriaNome.getText() == null || textFieldCategoriaNome.getText().length() == 0) {
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
