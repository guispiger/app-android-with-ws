package com.example.cadastros_setores;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CadastroSetorFragment extends Fragment {
    private EditText edDescricao;
    private EditText edMargem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cadastro_setor, container, false);
        edDescricao = (EditText) v.findViewById(R.id.edDescricao);
        edMargem = (EditText) v.findViewById(R.id.edMargem);
        return v;
    }

    public Setor validarDados(){
        String descricao = edDescricao.getText().toString().trim();
        Double margem = null;
        try {
            margem = Double.parseDouble(edMargem.getText().toString());
        }catch (Exception e){
            Toast.makeText(getActivity(), "Informe um núemro para margem válido", Toast.LENGTH_SHORT).show();
        }

        if(descricao == null || descricao.isEmpty()){
            Toast.makeText(getActivity(), "Informe uma descrição para o setor", Toast.LENGTH_SHORT).show();
            return null;
        }

        if(margem == null || margem < 0){
            Toast.makeText(getActivity(), "Informe uma margem superior a 0 para o setor", Toast.LENGTH_SHORT).show();
            return null;
        }
        edDescricao.setText("");
        edMargem.setText("");
        return new Setor(descricao, margem);
    }

    public void ajustarEdicao(Setor s){
        edDescricao.setText(s.getDescricao());
        edMargem.setText(String.valueOf(s.getMargem()));
    }
}