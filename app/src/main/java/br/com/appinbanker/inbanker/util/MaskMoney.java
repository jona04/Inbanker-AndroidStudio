package br.com.appinbanker.inbanker.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Jonatas on 09/11/2016.
 */

public abstract class MaskMoney {

    public static TextWatcher insert(final EditText ediTxt) {
        return new TextWatcher() {

            private boolean isUpdating = false;
            Locale ptBr = new Locale("pt", "BR");
            // Pega a formatacao do sistema, se for brasil R$ se EUA US$
            private NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int after) {
                // Evita que o método seja executado varias vezes.
                // Se tirar ele entre em loop
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                isUpdating = true;
                String str = s.toString();
                // Verifica se já existe a máscara no texto.
                boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                        (str.indexOf(".") > -1 || str.indexOf(",") > -1));
                // Verificamos se existe máscara
                if (hasMask) {
                    // Retiramos a máscara.
                    str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                            .replaceAll("[.]", "");
                }

                try {
                    // Transformamos o número que está escrito no EditText em
                    // monetário.
                    str = nf.format(Double.parseDouble(str) / 100);
                    ediTxt.setText(str);
                    ediTxt.setSelection(ediTxt.getText().length());
                } catch (NumberFormatException e) {
                    s = "";
                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Não utilizamos
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Não utilizamos
            }
        };


    }

    public static String removeMask(String str){
        // Retiramos a máscara.
        str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                .replaceAll("[.]", "");

        return str;
    }
}

