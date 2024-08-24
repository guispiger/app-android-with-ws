package com.example.cadastros_financas;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetorService extends IntentService {

    public static final String ACTION_LISTAR    = "com.example.setores.action.LISTAR";
    public static final String ACTION_CADASTRAR = "com.example.setores.action.CADASTRAR";
    public static final String ACTION_DELETAR = "com.example.setores.action.DELETAR";
    public static final String ACTION_ATUALIZAR = "com.example.setores.action.ATUALIZAR";
    public static final String RESULTADO_LISTA_SETORES = "com.example.setores.RESULTADO_LISTA_SETORES";
    static final String URL_WS = "http://argo.td.utfpr.edu.br/clients/ws/setor";

    Gson gson;

    public SetorService() {
        super("SetorService");
        gson = new GsonBuilder().create();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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

    private void cadastrar(Intent intent) {
        try {
            Setor set = (Setor) intent.getSerializableExtra("setor");
            String strSetor = gson.toJson(set);

            URL url = new URL(URL_WS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type","application/json");
            con.connect();
            PrintWriter writer = new PrintWriter(con.getOutputStream());
            writer.println(strSetor);
            writer.flush();
            if (con.getResponseCode() == 200) {
                Log.d("POST","OK");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void listar(Intent intent) {
        Log.d("LISTAR", "ENTROU");
        try {
            URL url = new URL(URL_WS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect();
            if (con.getResponseCode() == 200) {
                Log.d("LISTAR", "200");
                BufferedReader ent = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                StringBuilder bld = new StringBuilder(1000);
                String linha;
                do {
                    linha = ent.readLine();
                    if (linha != null) {
                        bld.append(linha);
                    }
                } while (linha != null);
                Setor[] setores = gson.fromJson(bld.toString(), Setor[].class);
                Intent it = new Intent(RESULTADO_LISTA_SETORES);
                it.putExtra("setores", setores);
                sendBroadcast(it);
                Log.d("LISTAR", "FIM");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void remover(Intent intent){
        try {
            Log.d("REMOVER", "ENTROU");
            Setor set = (Setor) intent.getSerializableExtra("setor");

            Log.d("REMOVER", String.valueOf(set.getId()));

            URL url = new URL(URL_WS+"/"+set.getId());
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

    private void atualizar(Intent intent) {
        try {
            Log.d("ATUALIZAR","INICIO");
            Setor set = (Setor) intent.getSerializableExtra("setor");
            String strSetor = gson.toJson(set);

            Log.d("ATUALIZAR",strSetor);

            URL url = new URL(URL_WS + "/" + set.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("content-type","application/json");
            con.connect();
            PrintWriter writer = new PrintWriter(con.getOutputStream());
            writer.println(strSetor);
            writer.flush();

            Log.d("ATUALIZAR",con.getResponseCode()+ '-'+ con.getResponseMessage());
            if (con.getResponseCode() == 200) {
                Log.d("PUT","OK");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}