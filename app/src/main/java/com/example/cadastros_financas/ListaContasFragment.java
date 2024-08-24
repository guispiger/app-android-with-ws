package com.example.cadastros_financas;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ListaContasFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listaContas;
    ArrayList<Conta> contas;
    ContaAdapter adapter;
    int selectedPosition = -1;
    //-----------------------------------------------------------------
    class ContaAdapter extends ArrayAdapter<Conta> {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        public ContaAdapter(Context context) {
            super(context, 0, contas);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_conta, null);
            }
            Conta conta = contas.get(position);
            ((TextView) v.findViewById(R.id.item_descricao)).setText(conta.getDescricao());
            ((TextView) v.findViewById(R.id.item_vencimento)).setText(sdf.format(conta.getVencimento()));
            ((TextView) v.findViewById(R.id.item_valor)).setText(Double.toString(conta.getValor()));
            if (position == selectedPosition) {
                v.setBackgroundColor( Color.LTGRAY);
            } else {
                v.setBackgroundColor( Color.WHITE);
            }
            return v;
        }
    }

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
        View v = inflater.inflate(R.layout.fragment_lista_contas, container, false);
        listaContas = (ListView) v.findViewById(R.id.listaContas);
        listaContas.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaContas.setOnItemClickListener(this);
        return v;
    }

    //-----------------------------------------------------------------
    public void adicionar(Conta c) {
        contas.add(c);
        adapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------
    public void setContas(ArrayList<Conta> contas) {
        this.contas = contas;
        adapter = new ContaAdapter(getActivity());
        listaContas.setAdapter(adapter);
    }

    //-----------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if (pos == selectedPosition) {
            selectedPosition = -1;
        } else {
            selectedPosition = pos;
        }
        adapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------
    public Conta getContaSelecionada() {
        if (selectedPosition < 0) {
            return null;
        }
        return contas.get(selectedPosition);
    }

    //-----------------------------------------------------------------
    public void substituir(Conta old, Conta novo) {
        int posicao = contas.indexOf(old);
        if (posicao >= 0) {
            contas.set(posicao, novo);
            adapter.notifyDataSetChanged();
        }
    }

    //-----------------------------------------------------------------
    public boolean remover(Conta conta) {
        try {
            contas.remove(conta);
            adapter.notifyDataSetChanged();
            return true;
        }catch (Exception e){
            Log.d("LISTA-CONTAS", e.getMessage());
            return false;
        }
    }

    //-----------------------------------------------------------------
    public ArrayList<Conta> getContas(){
        return contas;
    }
}