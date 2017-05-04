package br.com.appinbanker.inbanker.fragments_navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Transacao;

/**
 * Created by jonatasilva on 22/04/17.
 */

public class FragmentAbaReceber extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aba_inicio_receber, container, false);

        TextView tv = (TextView) view.findViewById(R.id.tv_aba_receber);

        double total = getArguments().getDouble("total");

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        tv.setText(nf.format(total));

        return view;
    }

}
