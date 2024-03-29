package br.blog.om.omessaging.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.blog.om.omessaging.R;
import br.blog.om.omessaging.domain.Usuario;

import static br.blog.om.omessaging.references.Referencias.salasReference;
import static br.blog.om.omessaging.references.Referencias.usuariosReference;
import static br.blog.om.omessaging.util.MensagensUsuario.PERMISSAO_NECESSARIA;
import static br.blog.om.omessaging.util.MensagensUsuario.PERMISSAO_NECESSARIA_MSG;

public class CadastroActivity extends AppCompatActivity {

    static Context context;
    static Button botao;
    private static Usuario usuario;
    static EditText editNome, editEmail, editSenha, editSenha2;


    @Override
    public void onRequestPermissionsResult(int requestCode,
    String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    criarConta();
                } else {
                    pedirAcessoAoChip(PERMISSAO_NECESSARIA, PERMISSAO_NECESSARIA_MSG);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void criarConta(){

        //mostrarMensagem("OK", "Deu certo");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        usuario.setId(editEmail.getText().toString());
        usuario.setNome(editNome.getText().toString());
        usuariosReference.child(usuario.getId()).setValue(usuario);

        entrarEmSala("saladosmorais");
        entrarEmSala("salateste2");



    }


    public void entrarEmSala(String nome){
        salasReference.child(nome).child("nome").setValue(nome);
        salasReference.child(nome).child("usuarios").child(usuario.getId()).setValue(usuario);
        usuariosReference.child(usuario.getId()).child("salas").child(nome).setValue(nome);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        context = CadastroActivity.this;
        botao = findViewById(R.id.botaoCriarConta);
        editNome = findViewById(R.id.editNomeCadastro);
        editEmail = findViewById(R.id.editEmailCadastro);
        editSenha = findViewById(R.id.editSenhaCadastro);
        editSenha2 = findViewById(R.id.editSenhaCadastro2);
        usuario = new Usuario();


        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
           // tm.getSimSerialNumber();


        }

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                    pedirAcessoAoChip(PERMISSAO_NECESSARIA, PERMISSAO_NECESSARIA_MSG);
                } else if(editNome.getText().length()<3){
                    mostrarMensagem("Aviso", "O nome deve conter pelo menos 3 caracteres.");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()).matches()){
                    mostrarMensagem("Aviso", "O e-mail digitado não é válido.");
                } else if (editSenha.getText().length()<5){
                    mostrarMensagem("Aviso", "A senha deve ter pelo menos 5 caracteres.");
                } else if (!editSenha2.getText().toString().equals(editSenha.getText().toString())){
                    mostrarMensagem("Aviso", "As senhas digitadas não coincidem.");
                } else {
                    criarConta();
                }
            }

        });

        usuariosReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Usuario novo = dataSnapshot.getValue(Usuario.class);
                //mostrarMensagem(novo.getNome(), novo.getId());
               // usuario = dataSnapshot.getValue(Usuario.class);
               // Set<String> chaves = usuario.getSalas().keySet();
               // mostrarMensagem(chaves.toArray()[0].toString(), "");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void mostrarMensagem(String titulo, String msg){

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(titulo);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.create().show();//mostra dialogo

    }

    public  void encerrarComErro(String erro){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(erro);
        dlgAlert.setTitle("Erro");
        dlgAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(1); //fecha o app com codigo de erro 1
                    }
                });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();//mostra dialogo de falha
    }

    public  void pedirAcessoAoChip(String titulo, String msg){
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(titulo);
        dlgAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                    }
                });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    public static Context getContext() {
        return context;
    }

    public static Usuario getUsuarioAtual(){ return usuario; }
}
