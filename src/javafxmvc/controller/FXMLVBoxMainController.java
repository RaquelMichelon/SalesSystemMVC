/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmvc.controller;

import java.net.URL;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author raqueldarellimichelon
 */
public class FXMLVBoxMainController implements Initializable {

    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MenuItem menuItemCadastroClientes;

    @FXML
    private MenuItem menuItemGraficosVendasPorMes;

    @FXML
    private MenuItem menuItemProcessosVendas;

    @FXML
    private MenuItem menuItemRelatorioQuantidadeProdutosEstoque;
    
    @FXML
    private MenuItem menuItemCadastroCategorias;
    
    @FXML
    private MenuItem menuItemCadastroProdutos;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    //evento que abrirá a tela de cadastros
    @FXML
    public void handleMenuItemCadastrosClientes() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorpaneCadastroClientes.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    //evento que abrirá a tela de vendas
    @FXML
    public void handleMenuItemProcessosVendas() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorPaneProcessosVendas.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    //evento que abrirá a tela de graficos
    @FXML
    public void handleMenuItemGraficosVendasPorMes() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorPaneGraficosVendasPorMes.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    //evento que abrirá a tela de relatorios
    @FXML
    public void handleMenuItemRelatoriosQuantidadeProdutos() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorPaneRelatoriosQuantidadeProdutos.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    //evento que abrirá a tela de cadastro de categorias
    @FXML
    public void handleMenuItemCadastrosCategorias() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorPaneCadastroCategorias.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    //evento que abrirá a tela de cadastro de produtos
    @FXML
    public void handleMenuItemCadastrosProdutos() throws IOException {
        AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLAnchorPaneCadastroProdutos.fxml"));
        anchorPane.getChildren().setAll(a);
    }
    
    
    
}
