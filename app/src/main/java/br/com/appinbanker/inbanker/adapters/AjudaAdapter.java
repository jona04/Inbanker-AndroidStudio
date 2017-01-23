package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Ajuda;
import br.com.appinbanker.inbanker.entidades.Transacao;

/**
 * Created by jonatasilva on 10/01/17.
 */

public class AjudaAdapter extends BaseExpandableListAdapter {

    private Context _context;
    List<Ajuda> _listDataHeader;
    HashMap<String, Ajuda> _listDataChild;

    public AjudaAdapter(Context context, List<Ajuda> listDataHeader, HashMap<String,Ajuda> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    // set the child view with value
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Ajuda pergunta = (Ajuda) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_ajuda_child, parent, false);
        }

        TextView tv_child =  (TextView) convertView.findViewById(R.id.tv_child_ajuda);
        tv_child.setText(pergunta.getResposta());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        //to get children of the respective header(group).
        //return  this._listDataChild.get(this._listDataHeader.get(groupPosition));
        return  this._listDataHeader.get(groupPosition);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        Ajuda head = (Ajuda) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.adapter_ajuda_header, null);
        }

        TextView tv_header =  (TextView) view.findViewById(R.id.tv_header_ajuda);
        tv_header.setText(head.getPergunta());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}