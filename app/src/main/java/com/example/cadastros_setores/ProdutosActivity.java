package com.example.cadastros_setores;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

public class ProdutosActivity extends AppCompatActivity {
    ArrayList<Produto> produtos;
    CadastroProdutoFragment fragCadastroProduto;
    ListaProdutosFragment fragListaProduto;
    Produto produtoEditando = null;
    Setor setor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        Intent origem =  getIntent();
        setor = origem.getSerializableExtra("setor", Setor.class);

        fragCadastroProduto = (CadastroProdutoFragment) getFragmentManager().findFragmentByTag("fragCadastroProduto");
        fragListaProduto = (ListaProdutosFragment) getFragmentManager().findFragmentByTag("fragListaProduto");

        fragCadastroProduto.setEdDescricaoCategoria(setor.getDescricao());

        if (savedInstanceState != null) {
            produtos = (ArrayList<Produto>) savedInstanceState.getSerializable("listaProduto");
        }

        if (produtos == null) {
            produtos = new ArrayList<Produto>();
        }

        fragListaProduto.setProdutos(produtos);
        fragListaProduto.setSetor(setor);
    }

    //-----------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle bld) {
        super.onSaveInstanceState(bld);
        bld.putSerializable("listaProduto", produtos);
    }

    //-----------------------------------------------------------------
    public void cadastrarProduto(View v) {
        if (fragCadastroProduto != null) {
            Produto produto = fragCadastroProduto.validarDados(setor);
            if (produto != null) {
                if (produtoEditando != null) {
                    this.confirmaEdit(produtoEditando, produto);
                    Log.d("PRODUTO", "Alterado");
                } else {
                    this.confirmaAdd(produto);
                    Log.d("PRODUTO", "Adicionado");
                }
                produtoEditando = null;
            }
        }
    }

    //----------------------------------------------------------------
    public void removerProduto(View v) {
        if (fragListaProduto != null) {
            Produto produto = fragListaProduto.getProdutoSelecionado();
            if (produto == null) {
                Toast.makeText(this, "Selecione o produto que deseja remover", Toast.LENGTH_SHORT).show();
            } else {
                if (!fragListaProduto.remover(produto)) {
                    Toast.makeText(this, "Erro inesperado ao remover produto, verificar Logs!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //----------------------------------------------------------------
    public void editarProduto(View v) {
        if (fragListaProduto != null) {
            Produto produto = fragListaProduto.getProdutoSelecionado();
            if (produto == null) {
                Toast.makeText(this, "Selecione o produto a editar", Toast.LENGTH_SHORT).show();
            } else {
                fragCadastroProduto.ajustarEdicao(produto);
                produtoEditando = produto;
            }
        }
    }

    //----------------------------------------------------------------
    public void confirmaAdd(Produto produto) {
        AlertDialog.Builder bld = new AlertDialog.Builder(ProdutosActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a adição do produto: " + produto.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaProduto.adicionar(produto);
            }
        });

        bld.setNeutralButton("Cancelar", null);

        bld.show();
    }

    //----------------------------------------------------------------
    public void confirmaEdit(Produto produtoEditando, Produto produto) {
        AlertDialog.Builder bld = new AlertDialog.Builder(ProdutosActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a alteração do produto: " + produto.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaProduto.substituir(produtoEditando, produto);
            }
        });

        bld.setNeutralButton("Cancelar", null);

        bld.show();
    }

    //----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_contas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.item_voltar){
            finish();
        } else if(menuItem.getItemId() == R.id.item_reloadProdutos){
            //recarregar lista do servidor
            fragListaProduto.buscarProdutos();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //----------------------------------------------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
