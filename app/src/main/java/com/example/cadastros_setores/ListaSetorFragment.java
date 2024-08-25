package com.example.cadastros_setores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;


import java.util.ArrayList;
import java.util.Arrays;


public class ListaSetorFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView listaSetores;
    ArrayList<Setor> setores;
    CategoriaAdapter adapter;
    int selectedPosition = -1;

    ArrayList<Produto> produtos;

    //-----------------------------------------------------------------
    class SetorServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SetorService.RESULTADO_LISTA_SETORES)) {
                Setor[] sets = (Setor[]) intent.getSerializableExtra("setores");
                setores.clear(); // refaz a lista do adapter.
                if (sets != null && sets.length > 0) {
                    setores.addAll(Arrays.asList(sets));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //-----------------------------------------------------------------
    class ProdutoServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ProdutoService.RESULTADO_LISTA_PRODUTOS)) {
                Produto[] prodts = (Produto[]) intent.getSerializableExtra("produtos");
                produtos = new ArrayList<>();
                if (prodts != null && prodts.length > 0) {
                    Arrays.stream(prodts).filter(p -> p.getSetor() != null)
                            .forEach(p -> produtos.add(p));
                }
            }
        }
    }

    //-----------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(new SetorServiceObserver(),
                new IntentFilter(SetorService.RESULTADO_LISTA_SETORES), Context.RECEIVER_EXPORTED);

        getActivity().registerReceiver(new ProdutoServiceObserver(),
                new IntentFilter(ProdutoService.RESULTADO_LISTA_PRODUTOS), Context.RECEIVER_EXPORTED);

        buscarSetores();
        buscarProdutos();
    }

    //-----------------------------------------------------------------
    class CategoriaAdapter extends ArrayAdapter<Setor> {
        public CategoriaAdapter(Context context) {
            super(context, 0, setores);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_setor, null);
            }
            Setor setor = setores.get(position);
            ((TextView) v.findViewById(R.id.edId)).setText(String.valueOf(setor.getId()));
            ((TextView) v.findViewById(R.id.edDescricao)).setText(setor.getDescricao());
            ((TextView) v.findViewById(R.id.edMargem)).setText("$" + Double.toString(setor.getMargem()));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_setor, container, false);
        listaSetores = (ListView) v.findViewById(R.id.listaSetores);
        listaSetores.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaSetores.setOnItemClickListener(this);
        return v;
    }

    //-----------------------------------------------------------------
    public void buscarSetores() {
        Intent it = new Intent(getActivity(), SetorService.class);
        it.setAction( SetorService.ACTION_LISTAR );
        getActivity().startService(it);
    }

    //-----------------------------------------------------------------
   public ListView retornaListViewSetores(){
        return listaSetores;
   }

    //-----------------------------------------------------------------
    public void adicionar(Setor s) {
//        cadastrar
        Intent it = new Intent(getActivity(), SetorService.class);
        it.setAction( SetorService.ACTION_CADASTRAR );
        it.putExtra("setor", s);
        getActivity().startService(it);
        setores.add(s);

//        recarrega lista
        buscarSetores();

        adapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------
    public void setSetores(ArrayList<Setor> setores) {
        this.setores = setores;
        adapter = new CategoriaAdapter(getActivity());
        listaSetores.setAdapter(adapter);
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
    public Setor getSetorSelecionado() {
        if (selectedPosition < 0) {
            return null;
        }
        return setores.get(selectedPosition);
    }

    //-----------------------------------------------------------------
    public void substituir(Setor old, Setor novo) {
        int posicao = setores.indexOf(old);
        if (posicao >= 0) {
//          pega o id e atualiza na lista
            novo.setId(old.getId());
            setores.set(posicao, novo);

//          update
            Intent it = new Intent(getActivity(), SetorService.class);
            it.setAction( SetorService.ACTION_ATUALIZAR);
            it.putExtra("setor", novo);
            getActivity().startService(it);

//          chama lista do servidor
            buscarSetores();

            adapter.notifyDataSetChanged();
        }
    }
    //-----------------------------------------------------------------
    public String remover(Setor setor) {
        String result = null;
        try {
            //Verificar se o setor possui produtos
            if(produtos != null){
                for (Produto p : produtos) {
                    if (p.getSetor() != null &&
                        p.getSetor().getId() == setor.getId()) {
                        result = "Não é possível remover o setor " + setor.getDescricao() +
                                " pois ele possui produtos associados, " +
                                "remova primeiro os produtos!";
                        break;
                    }
                }
                if(result != null){
                    return result;
                }
            }

            //deletar
            Intent it = new Intent(getActivity(), SetorService.class);
            it.setAction( SetorService.ACTION_DELETAR);
            it.putExtra("setor", setor);
            getActivity().startService(it);

            //recarregar lista do servidor
            buscarSetores();

            setores.remove(setor);
            adapter.notifyDataSetChanged();
            return result;
        }catch (Exception e){
            Log.d("LISTA-SETOR", e.getMessage());
            return e.getMessage();
        }
    }

    public void buscarProdutos(){
        Intent it = new Intent(getActivity(), ProdutoService.class);
        it.setAction( ProdutoService.ACTION_LISTAR);
        getActivity().startService( it );
    }
}