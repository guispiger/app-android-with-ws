package com.example.cadastros_setores;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CadastroProdutoFragment extends Fragment {
    EditText edDescricao;
    EditText edPreco;
    EditText edEstoque;
    TextView edDescricaoSetor;

    //-----------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //-----------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cadastro_produto, container, false);
        edDescricao = (EditText) v.findViewById(R.id.edDescricao);
        edPreco = (EditText) v.findViewById(R.id.edPreco);
        edEstoque = (EditText) v.findViewById(R.id.edEstoque);
        edDescricaoSetor = (TextView) v.findViewById(R.id.edDescricaoSetor);
        return v;
    }

    //-----------------------------------------------------------------
    public Produto validarDados(Setor setor){
        String descricao = edDescricao.getText().toString().trim();
        String vlStringPreco = edPreco.getText().toString().trim();
        String vlStringEstoque = edEstoque.getText().toString().trim();

        Double preco = null;
        if(!vlStringPreco.isEmpty()){
            preco = Double.parseDouble(vlStringPreco);
        }

        Double estoque = null;
        if(!vlStringEstoque.isEmpty()){
            estoque = Double.parseDouble(vlStringEstoque);
        }

        if(descricao == null || descricao.isEmpty() || estoque == null || preco == null){
            Toast.makeText(getActivity(), "É necessário informar todos os dados!", Toast.LENGTH_SHORT).show();
            return null;
        }

        limparTela();
        return new Produto(descricao, estoque, preco, setor);
    }
    //-----------------------------------------------------------------
    public void limparTela(){
        edDescricao.setText("");
        edEstoque.setText("");
        edPreco.setText("");
    }

    //-----------------------------------------------------------------
    public void ajustarEdicao(Produto p){
        edDescricao.setText(p.getDescricao());
        edPreco.setText(Double.toString(p.getPreco()));
        edEstoque.setText(Double.toString(p.getEstoque()));
    }

    //-----------------------------------------------------------------
    public void setEdDescricaoCategoria(String descricaoCategoria){
        edDescricaoSetor.setText(descricaoCategoria);
    }
}