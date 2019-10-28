package com.lumos.lumosapp;

public class Produtos {

    private String descricao;
    private String barCode;

    public Produtos(String descricao, String barCode) {
        this.descricao = descricao;
        this.barCode = barCode;
    }

    public Produtos() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}


