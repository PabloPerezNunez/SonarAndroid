package gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;

/**
 * Created by PPN on 17/05/2015.
 */

public class Ajustes extends PreferenceActivity {

    private Preference filePicker,limpiaCanciones,limpiaBusquedas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carga las preferencias desde un archivo XML
        addPreferencesFromResource(R.xml.ventana_ajustes);



        String carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();

        filePicker = (Preference) findPreference(Principal.OPCION_CARPETA_DESCARGAS);
        filePicker.setSummary(Principal.preferencias.getString(Principal.OPCION_CARPETA_DESCARGAS,carpeta));

        filePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(); //Intent to start openIntents File Manager
                intent.setClass(Ajustes.this, DirectoryChooserActivity.class);
                intent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME, "");
                startActivityForResult(intent, 0);

                return true;
            }
        });

        limpiaBusquedas = (Preference) findPreference(Principal.OPCION_LIMPIAR_BUSQUEDAS);
        limpiaCanciones = (Preference) findPreference(Principal.OPCION_LIMPIAR_CANCIONES);

        limpiaBusquedas.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                limpiarHistorialBusquedas();
                return true;
            }
        });

        limpiaCanciones.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                limpiarHistorialCanciones();
                return true;
            }
        });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences preferences = Principal.preferencias;

        String newValue = preferences.getString(Principal.OPCION_CARPETA_DESCARGAS,"");

        if (requestCode == 0) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                newValue = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
            }
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Principal.OPCION_CARPETA_DESCARGAS, newValue);
        editor.commit();

        filePicker.setSummary(Principal.preferencias.getString(Principal.OPCION_CARPETA_DESCARGAS, "/storage/emulated/0/Download"));

    }


    private void limpiarHistorialCanciones() {

        int num = Principal.database.clearHistoryTable();
        Principal.toast("Se han eliminado " + num + " canciones");
    }

    private void limpiarHistorialBusquedas() {

        int num = Principal.database.clearSearchesTable();
        Principal.toast("Se han eliminado " + num + " busquedas");
    }

}