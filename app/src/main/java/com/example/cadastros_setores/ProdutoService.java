package com.example.cadastros_setores;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProdutoService extends IntentService {
    public static final String ACTION_LISTAR    = "com.example.produtos.action.LISTAR";
    public static final String ACTION_CADASTRAR = "com.example.produtos.action.CADASTRAR";
    public static final String ACTION_DELETAR = "com.example.produtos.action.DELETAR";
    public static final String ACTION_ATUALIZAR = "com.example.produtos.action.ATUALIZAR";
    public static final String RESULTADO_LISTA_PRODUTOS = "com.example.produtos.RESULTADO_LISTA_PRODUTOS";
    static final String URL_WS = "http://argo.td.utfpr.edu.br/clients/ws/produto";
    Gson gson;

    public ProdutoService() {
        super("ProdutoService");
        gson = new GsonBuilder().create();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null)
            return;
        switch (intent.getAction()) {
            case ACTION_CADASTRAR: cadastrar(intent);
                break;
            case ACTION_LISTAR: listar(intent);
                break;
            case ACTION_DELETAR: remover(intent);
                break;
            case ACTION_ATUALIZAR: atualizar(intent);
        }
    }

    private void remover(Intent intent) {
        try {
            Log.d("REMOVER", "ENTROU");
            Produto prod = (Produto) intent.getSerializableExtra("produto");

            URL url = new URL(URL_WS+"/"+prod.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("content-type","application/json");
            con.connect();

            Log.d("remover", con.getResponseCode()+ '-'+ con.getResponseMessage());
            if (con.getResponseCode() == 200) {
                Log.d("DELETE","OK");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void listar(Intent intent) {
        Log.d("LISTAR-produtos", "ENTROU");
        try {
            URL url = new URL(URL_WS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            if (con.getResponseCode() == 200) {
                Log.d("LISTAR-produtos", "200");
                BufferedReader ent = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                StringBuilder bld = new StringBuilder(100000);
                String linha;
                do {
                    linha = ent.readLine();
                    if (linha != null) {
                        bld.append(linha);
                    }
                } while (linha != null);
                Produto[] produtos = gson.fromJson(bld.toString(), Produto[].class);
                Intent it = new Intent(RESULTADO_LISTA_PRODUTOS);
                it.putExtra("produtos", produtos);
                sendBroadcast(it);
                Log.d("LISTAR-produtos", "FIM");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cadastrar(Intent intent) {
        try {
            Produto prod = (Produto) intent.getSerializableExtra("produto");
            String strProduto = gson.toJson(prod);

            URL url = new URL(URL_WS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type","application/json");
            con.connect();
            PrintWriter writer = new PrintWriter(con.getOutputStream());
            writer.println(strProduto);
            writer.flush();
            if (con.getResponseCode() == 200) {
                Log.d("POST","OK");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void atualizar(Intent intent) {
        try {
            Log.d("ATUALIZAR-produto","INICIO");
            Produto prod = (Produto) intent.getSerializableExtra("produto");
            String strProd = gson.toJson(prod);

            URL url = new URL(URL_WS + "/" + prod.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("content-type","application/json");
            con.connect();
            PrintWriter writer = new PrintWriter(con.getOutputStream());
            writer.println(strProd);
            writer.flush();

            Log.d("ATUALIZAR-produto",con.getResponseCode()+ '-'+ con.getResponseMessage());
            if (con.getResponseCode() == 200) {
                Log.d("PUT","OK");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}