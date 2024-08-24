package com.example.cadastros_financas;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroContaFragment extends Fragment {
    EditText edDescricao;
    EditText edValor;
    TextView txtVencimento;
    TextView edDescricaoCategoria;
    Date dataVencimento = null;

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
        View v = inflater.inflate(R.layout.fragment_cadastro_conta, container, false);
        edDescricao = (EditText) v.findViewById(R.id.edDescricao);
        edValor = (EditText) v.findViewById(R.id.edValor);
        txtVencimento = (TextView) v.findViewById(R.id.txtVencimento);
        edDescricaoCategoria = (TextView) v.findViewById(R.id.edDescricaoCategoria);
        return v;
    }

    //-----------------------------------------------------------------
    public Conta validarDados(Categoria categoria){
        String descricao = edDescricao.getText().toString().trim();
        Date vencimento = dataVencimento;
        String vlString = edValor.getText().toString().trim();
        Double valor = null;
        if(!vlString.isEmpty()){
            valor = Double.parseDouble(vlString);
        }

        if(descricao == null || descricao.isEmpty() || vencimento == null || valor == null){
            Toast.makeText(getActivity(), "É necessário informar todos os dados!", Toast.LENGTH_SHORT).show();
            return null;
        }
        edDescricao.setText("");
        txtVencimento.setText("Selecionar o Vencimento");
        edValor.setText("");

        return new Conta(descricao, vencimento, valor, categoria);
    }

    //-----------------------------------------------------------------
    public void ajustarEdicao(Conta c){
        edDescricao.setText(c.getDescricao());
        apresentaVencimentoEmTela(c.getVencimento());
        defineVencimento(c.getVencimento());
        edValor.setText(Double.toString(c.getValor()));
    }

    //-----------------------------------------------------------------
    public void setEdDescricaoCategoria(String descricaoCategoria){
        edDescricaoCategoria.setText(descricaoCategoria);
    }

    //-----------------------------------------------------------------
    public void defineVencimento(Date data) {
        dataVencimento = data;
        apresentaVencimentoEmTela(data);

    }

    //-----------------------------------------------------------------
    public void apresentaVencimentoEmTela(Date data){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtVencimento.setText(sdf.format(data));
    }
}