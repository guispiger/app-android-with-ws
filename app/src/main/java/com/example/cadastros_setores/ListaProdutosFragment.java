package com.example.cadastros_setores;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;
import java.util.Arrays;


public class ListaProdutosFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listaProdutos;
    ArrayList<Produto> produtos;
    ProdutoAdapter adapter;

    Setor setor;
    int selectedPosition = -1;
    //-----------------------------------------------------------------
    class ProdutoServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ProdutoService.RESULTADO_LISTA_PRODUTOS)) {
                Produto[] prodts = (Produto[]) intent.getSerializableExtra("produtos");
                produtos.clear(); // refaz a lista do adapter.
                if (prodts != null && prodts.length > 0) {
                    Arrays.stream(prodts).filter(p -> p.getSetor().getId() == setor.getId())
                                         .forEach(p -> produtos.add(p));
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //-----------------------------------------------------------------
    class ProdutoAdapter extends ArrayAdapter<Produto> {
        public ProdutoAdapter(Context context) {
            super(context, 0, produtos);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.item_produto, null);
            }
            Produto produto = produtos.get(position);
            ((TextView) v.findViewById(R.id.item_descricao)).setText(produto.getDescricao());
            ((TextView) v.findViewById(R.id.item_estoque)).setText(Double.toString(produto.getEstoque()));
            ((TextView) v.findViewById(R.id.item_preco)).setText(Double.toString(produto.getPreco()));
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
        getActivity().registerReceiver(new ProdutoServiceObserver(),
                new IntentFilter(ProdutoService.RESULTADO_LISTA_PRODUTOS), Context.RECEIVER_EXPORTED);

        buscarProdutos();
    }

    //-----------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_produtos, container, false);
        listaProdutos = (ListView) v.findViewById(R.id.listaProdutos);
        listaProdutos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listaProdutos.setOnItemClickListener(this);
        return v;
    }
    //-----------------------------------------------------------------
    public void buscarProdutos() {
        Intent it = new Intent(getActivity(), ProdutoService.class);
        it.setAction( ProdutoService.ACTION_LISTAR);
        getActivity().startService(it);
    }

    //-----------------------------------------------------------------
    public void adicionar(Produto p) {
//        cadastrar
        Intent it = new Intent(getActivity(), ProdutoService.class);
        it.setAction( ProdutoService.ACTION_CADASTRAR );
        it.putExtra("produto", p);
        getActivity().startService(it);
        produtos.add(p);

//        recarrega lista
        it = new Intent(getActivity(), ProdutoService.class);
        it.setAction( ProdutoService.ACTION_LISTAR );
        getActivity().startService( it );

        adapter.notifyDataSetChanged();
    }

    //-----------------------------------------------------------------
    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos;
        adapter = new ProdutoAdapter(getActivity());
        listaProdutos.setAdapter(adapter);
    }
    //-----------------------------------------------------------------
    public void setSetor(Setor setor){
        this.setor = setor;
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
    public Produto getProdutoSelecionado() {
        if (selectedPosition < 0) {
            return null;
        }
        return produtos.get(selectedPosition);
    }

    //-----------------------------------------------------------------
    public void substituir(Produto old, Produto novo) {
        int posicao = produtos.indexOf(old);
        if (posicao >= 0) {
//          pega o id e atualiza na lista
            novo.setId(old.getId());
            produtos.set(posicao, novo);

//          update
            Intent it = new Intent(getActivity(), ProdutoService.class);
            it.setAction( ProdutoService.ACTION_ATUALIZAR);
            it.putExtra("produto", novo);
            getActivity().startService(it);

//          chama lista do servidor
            it = new Intent(getActivity(), ProdutoService.class);
            it.setAction( ProdutoService.ACTION_LISTAR);
            getActivity().startService( it );
            adapter.notifyDataSetChanged();
        }
    }

    //-----------------------------------------------------------------
    public boolean remover(Produto produto) {
        try {
            Intent it = new Intent(getActivity(), ProdutoService.class);
            it.setAction( ProdutoService.ACTION_DELETAR);
            it.putExtra("produto", produto);
            getActivity().startService(it);

            it = new Intent(getActivity(), ProdutoService.class);
            it.setAction( ProdutoService.ACTION_LISTAR);
            getActivity().startService( it );

            produtos.remove(produto);
            adapter.notifyDataSetChanged();
            return true;
        }catch (Exception e){
            Log.d("LISTA-PRODUTO", e.getMessage());
            return false;
        }
    }

    //-----------------------------------------------------------------
    public ArrayList<Produto> getProdutos(){
        return produtos;
    }
}