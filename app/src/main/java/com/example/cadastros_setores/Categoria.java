package com.example.cadastros_setores;

import java.io.Serializable;
import java.util.ArrayList;

public class Categoria implements Serializable {
    private String descricao;
    private ArrayList<Conta> contas;

    public Categoria(String descricao) {
        this.descricao = descricao;
    }

    public Categoria(){
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ArrayList<Conta> getContas() {
        return contas;
    }

    public void setContas(ArrayList<Conta> contas) {
        this.contas = contas;
    }

    public double retornaSomaContas(){
        double soma = 0;
        if(this.contas == null || this.contas.isEmpty()){
            return 0;
        }

        for (Conta conta : contas) {
            soma += conta.getValor();
        }

        return soma;
    }

    public int retornaQntContas(){
        if(this.contas == null || this.contas.isEmpty()){
            return 0;
        }
        return this.contas.size();
    }
}
