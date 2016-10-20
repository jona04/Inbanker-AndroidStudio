package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.appinbanker.inbanker.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PedirEmprestimoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PedirEmprestimoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedirEmprestimoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private LoginButton loginButton;
    private CallbackManager callbackManager;

    public PedirEmprestimoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InicioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PedirEmprestimoFragment newInstance(String param1, String param2) {
        PedirEmprestimoFragment fragment = new PedirEmprestimoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    Log.i("Facebook","nao logado");
                } else {
                    Log.i("Facebook","logando accestoken = "+AccessToken.getCurrentAccessToken());
                    graphFacebook(AccessToken.getCurrentAccessToken());
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedir_emprestimo, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_friends");

        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Facebook", "onSuceess - loingResult= "+loginResult);

                //chamamos o metedo graphFacebook para obter os dados do usuario logado
                //passando como parametro o accessToken gerado no login
                graphFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("Facebook", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("Facebook", "onError - exception = "+exception);
            }

        });

        return view;

    }

    public void graphFacebook(final AccessToken accessToken){
        try{



            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            //Log.v("Facebook", response.toString());
                            //Log.v("Facebook", object.toString());


                            try {
                                // Application code
                                String id = object.getString("id");
                                obterDados(accessToken,id);

                                //String birthday = object.getString("birthday"); // 01/31/1980 format
                            }catch (Exception e){
                                Log.i("Facebook","Exception JSON graph facebook = "+e);
                            }
                        }
                    });

            request.executeAsync();
        }
        catch (Exception e){
            Log.i("Facebook","Exception graph facebook = "+e);
        }
    }

    public void obterDados(AccessToken accessToken, String id){


        /* make the API call */
        new GraphRequest(
                accessToken,
                "/"+id+"?fields=id,name,email,picture.type(large),friends{id,name,picture.type(large){url}}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.i("Facebook","dados gerais = "+response);

                        try {

                            JSONObject object = response.getJSONObject();

                            String id = object.getString("id");
                            //String email = object.getString("email");
                            String name = object.getString("name");

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            String url_picture = pic.getString("url");

                            object = object.getJSONObject("friends");
                            JSONArray friends_list = object.getJSONArray("data");

                            Log.i("Facebook","id, email, name ="+ id +"- "+name + " - "+url_picture);
                            Log.i("Facebook","friends = "+friends_list);
                        }
                        catch(Exception e){
                            Log.i("Facebook",""+e);
                        }
                    }
                }
        ).executeAsync();

    }

    /*boolean public boolean isLoggedIn() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.i("Facebook","acctoken = "+accessToken);
        if(accessToken != null)
            return true;
        else
            return false;

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Facebook", "onActivitResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
