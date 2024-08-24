package com.example.cadastros_financas;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContasActivity extends AppCompatActivity {
    ArrayList<Conta> contas;
    CadastroContaFragment fragCadastroConta;
    ListaContasFragment fragListaContas;
    Conta contaEditando = null;
    Categoria categoria;
    Date data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);

        Intent origem =  getIntent();
        categoria = origem.getSerializableExtra("categoria", Categoria.class);

        fragCadastroConta = (CadastroContaFragment) getFragmentManager().findFragmentByTag("fragCadastroConta");
        fragListaContas = (ListaContasFragment) getFragmentManager().findFragmentByTag("fragListaContas");

        fragCadastroConta.setEdDescricaoCategoria(categoria.getDescricao());

        if (savedInstanceState != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            contas = (ArrayList<Conta>) savedInstanceState.getSerializable("listaContas");
            try {
                data = sdf.parse(savedInstanceState.getString("data"));
                fragCadastroConta.defineVencimento(data);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if(categoria.getContas() != null){
            contas = categoria.getContas();
        }
        if (contas == null) {
            contas = new ArrayList<Conta>();
        }

        fragListaContas.setContas(contas);
    }

    //-----------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle bld) {
        super.onSaveInstanceState(bld);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        bld.putSerializable("listaContas", contas);
        bld.putString("data", sdf.format(data));
    }

    //-----------------------------------------------------------------
    public void cadastrarConta(View v) {
        if (fragCadastroConta != null) {
            Conta conta = fragCadastroConta.validarDados(this.categoria);
            if (conta != null) {
                if (contaEditando != null) {
                    this.confirmaEdit(contaEditando, conta);
                    Log.d("CONTA", "Alterada");
                } else {
                    this.confirmaAdd(conta);
                    Log.d("CONTA", "Adicionada");
                }
                contaEditando = null;
            }
        }
    }

    //----------------------------------------------------------------
    public void removerConta(View v) {
        if (fragListaContas != null) {
            Conta conta = fragListaContas.getContaSelecionada();
            if (conta == null) {
                Toast.makeText(this, "Selecione a conta que deseja remover", Toast.LENGTH_SHORT).show();
            } else {
                if (!fragListaContas.remover(conta)) {
                    Toast.makeText(this, "Erro inesperado ao remover conta, verificar Logs!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //----------------------------------------------------------------
    public void editarConta(View v) {
        if (fragListaContas != null) {
            Conta conta = fragListaContas.getContaSelecionada();
            if (conta == null) {
                Toast.makeText(this, "Selecione a conta a editar", Toast.LENGTH_SHORT).show();
            } else {
                fragCadastroConta.ajustarEdicao(conta);
                contaEditando = conta;
            }
        }
    }

    //----------------------------------------------------------------
    public void confirmaAdd(Conta conta) {
        AlertDialog.Builder bld = new AlertDialog.Builder(ContasActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a adição da Conta: " + conta.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaContas.adicionar(conta);
            }
        });

        bld.setNeutralButton("Cancelar", null);

        bld.show();
    }

    //----------------------------------------------------------------
    public void confirmaEdit(Conta contaEditando, Conta conta) {
        AlertDialog.Builder bld = new AlertDialog.Builder(ContasActivity.this);
        bld.setTitle("Confirmar");
        bld.setMessage("Confirma a alteração da conta: " + conta.getDescricao() + " ?");

        bld.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fragListaContas.substituir(contaEditando, conta);
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
            devolveContas();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //----------------------------------------------------------------
    public void devolveContas(){
        Intent itResult = new Intent();
        itResult.putExtra("contas", fragListaContas.getContas());
        setResult(RESULT_OK, itResult);
        finish();
    }

    //----------------------------------------------------------------
    @Override
    public void onBackPressed() {
        devolveContas();
        super.onBackPressed();
    }

    //----------------------------------------------------------------
    public void selecionarVencimento(View v){
        DatePickerDialog dpd = new DatePickerDialog(this);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                ano = ano - 1900;
                data = new Date(ano, mes, dia);
                fragCadastroConta.defineVencimento(data);
            }
        });
        dpd.show();
    }
}
