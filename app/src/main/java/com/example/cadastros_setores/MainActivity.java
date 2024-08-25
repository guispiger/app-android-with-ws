package com.example.cadastros_setores;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Setor> setores;
    CadastroSetorFragment fragCadastroSetor;
    ListaSetorFragment fragListaSetor;
    Setor setorSelecionado = null;

    //-----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragCadastroSetor = (CadastroSetorFragment) getFragmentManager().findFragmentByTag("fragCadastroSetor");
        fragListaSetor = (ListaSetorFragment) getFragmentManager().findFragmentByTag("fragListaSetor");
        if (savedInstanceState != null) {
            setores = (ArrayList<Setor>) savedInstanceState.getSerializable("listaSetores");
            setorSelecionado = (Setor) savedInstanceState.getSerializable("setorSelecionado");
        }
        if (setores == null) {
            setores = new ArrayList<Setor>();
        }
        fragListaSetor.setSetores(setores);

        fragListaSetor.retornaListViewSetores().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setorSelecionado = setores.get(position);
                Intent it = new Intent(getApplicationContext(), ProdutosActivity.class);
                it.putExtra("setor", setorSelecionado);
                startActivityForResult(it, 123);
                return false;
            }
        });
    }

    //-----------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle bld) {
        super.onSaveInstanceState(bld);
        bld.putSerializable("listaSetores", setores);
        bld.putSerializable("setorSelecionado", setorSelecionado);
    }

    //-----------------------------------------------------------------
    public void adicionar(View v) {
        if (fragCadastroSetor != null) {
            Setor setor = fragCadastroSetor.validarDados();
            if (setor != null) {
                if (setorSelecionado != null) {
                    this.confirmaEdit(setorSelecionado, setor);
                    Log.d("SETOR", "Alterado");
                } else {
                    this.confirmaAdd(setor);
                    Log.d("SETOR", "Adicionado");
                }
                setorSelecionado = null;
            }
        }
    }

    //----------------------------------------------------------------
    public void removerSetor(View v) {
        if (fragListaSetor != null) {
            Setor setor = fragListaSetor.getSetorSelecionado();
            if (setor == null) {
                Toast.makeText(this, "Selecione o setor que deseja remover", Toast.LENGTH_SHORT).show();
            } else {
                if (!fragListaSetor.remover(setor)) {
                    Toast.makeText(this, "Erro inesperado ao remover setor, verificar Logs!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //----------------------------------------------------------------
    public void editarSetor(View v) {
        if (fragListaSetor != null) {
            Setor setor = fragListaSetor.getSetorSelecionado();
            if (setor == null) {
                Toast.makeText(this, "Selecione o setor a editar", Toast.LENGTH_SHORT).show();
            } else {
                fragCadastroSetor.ajustarEdicao(setor);
                setorSelecionado = setor;
            }
        }
    }

    //----------------------------------------------------------------
    public void confirmaAdd(Setor setor) {
        AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a adição do setor: " + setor.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaSetor.adicionar(setor);
            }
        });

        bld.setNeutralButton("Cancelar", null);

        bld.show();
    }

    //----------------------------------------------------------------
    public void confirmaEdit(Setor setorEditando, Setor setor) {
        AlertDialog.Builder bld = new AlertDialog.Builder(MainActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a alteração da categoria: " + setor.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaSetor.substituir(setorEditando, setor);
            }
        });

        bld.setNeutralButton("Cancelar", null);

        bld.show();
    }

    //----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == R.id.item_addSetor){
            setorSelecionado = fragListaSetor.getSetorSelecionado();
            addProduto(setorSelecionado);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //----------------------------------------------------------------
    public void addProduto(Setor setor){
        if(setor == null){
            Toast.makeText(this, "Selecione um setor para adicionar uma conta", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent it = new Intent(this, ProdutosActivity.class);
        it.putExtra("setor", setor);
        startActivityForResult(it, 123);
    }

    //----------------------------------------------------------------
    @Override
    public void onActivityResult(int requisicao, int resposta, Intent dados){
        super.onActivityResult(requisicao,resposta,dados);
        if(requisicao == 123 && resposta == RESULT_OK){
            setorSelecionado = null;
        }
    }
}