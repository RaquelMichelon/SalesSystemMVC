package javafxmvc.model.domain;

import java.io.Serializable;

public class Categoria implements Serializable {

    private int idCategoria;
    private String descricao;
    
    public Categoria(){
    }

    public Categoria(int idCategoria, String descricao) {
        this.idCategoria = idCategoria;
        this.descricao = descricao;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Override
    public String toString() {
        return this.descricao;
    }
    
}
