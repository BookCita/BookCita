package com.dam.bookcita;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import models.ModelBook;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RechercherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechercherFragment extends Fragment implements AdapterBook.OnItemClickListener{

    private static final String TAG = "RechercherFragment";

    private static final int MAX = 20;
    private static final String MAX_RESULTS = "40";
    private static final String ISBN = "isbn";
    private static final String ID = "id";
    // test pour rebase
    private EditText etKeyword;
    private String keyword = "";
    private String totalItems;
    private RecyclerView rvBookByKeyword;

    private Button btnChercher;
    private Button btnClearText;
    private Button btnRechScanISBN;


    private ArrayList<ModelBook> bookArrayList;

    private AdapterBook adapterBook;

    public static final int PERMISSION_INTERNET = 0;

    private RequestQueue requestQueue;  //Pour volley

    /** Variables propres au fragment */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


//    private void remplissageArrayListeEnDur() {
//        ModelBook modelBook1 = new ModelBook("https://books.google.com/books/content?id=StXltAEACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api", "Aucun Autre", "John Mac Arthur", "9782890823136");
//        ModelBook modelBook2 = new ModelBook("", "La force d'aimer", "Martin Luther King", "9782356140630");
//
//        bookArrayList.add(modelBook1);
//        bookArrayList.add(modelBook2);
//
//        adapterBook = new AdapterBook(this, bookArrayList);
//
//        rvBookByKeyword.setAdapter(adapterBook);
//
//    }

    // Methode pour verifier les permissions de l'application
    public boolean checkPermission() {
        int INTERNET_PERMISSION = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET);
        if (INTERNET_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.INTERNET}, INTERNET_PERMISSION);
            Log.i(TAG, "checkPermission: " + "Pas de permission INTERNET");
            return false;
        }
        Log.i(TAG, "checkPermission: " + "Permission INTERNET");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_INTERNET: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.INTERNET)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getContext(), "Please allow internet permission", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lancement de l'app

                    }
                }
            }
        }
    }

    private void init(View view) {
        etKeyword = view.findViewById(R.id.etKeyword);
        rvBookByKeyword = view.findViewById(R.id.rvBookByKeyword);
        btnClearText = view.findViewById(R.id.btnClearText);
        btnChercher = view.findViewById(R.id.btnChercher);
        btnRechScanISBN = view.findViewById(R.id.btnRechScanISBN);
        bookArrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

        rvBookByKeyword.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        rvBookByKeyword.setItemAnimator(new DefaultItemAnimator());
    }






    public String convertirLienEnHttps(String lien) {
        try {
            URL url_lien = new URL(lien);
            URL url_lienHttps = new URL("https", url_lien.getHost(), url_lien.getPort(), url_lien.getFile());

            String lienHttps = url_lienHttps.toString();
            return lienHttps;

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }


    private void parseJSON() throws UnsupportedEncodingException {
        //https://www.googleapis.com/books/v1/volumes?q=souris
        String urlJSONFile = "https://www.googleapis.com/books/v1/volumes?q="
                + URLEncoder.encode(keyword, String.valueOf(StandardCharsets.UTF_8))
                + "&maxResults="
                + MAX_RESULTS
                + "&key="
                + "AIzaSyARotakRwdwvBqUpRRHwZ3X7URwamy86G0";

        Log.i(TAG, "parseJSON: urlJSONFile : " + urlJSONFile);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlJSONFile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    totalItems = response.getString("totalItems");
                    Log.i(TAG, "onResponse: totalItems : " + totalItems);


                    JSONArray jsonArray = response.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        String id = item.getString("id");
                        Log.i(TAG, "onResponse: id : " + id);

                        JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                        String titre = "";
                        if (volumeInfo.has("title")) {
                            titre = volumeInfo.getString("title");
                        }
                        Log.i(TAG, "onResponse: titre : " + titre);
                        String auteur = "";
                        if (volumeInfo.has("authors")) {
                            JSONArray jsonArrayAuthors = volumeInfo.getJSONArray("authors");
                            // Pour l'instant on ne récupère que l'auteur principal (le 1er)
                            auteur = jsonArrayAuthors.get(0).toString();
                        }
                        Log.i(TAG, "onResponse: auteur : " + auteur);

                        String isbn = "";
                        if (volumeInfo.has("industryIdentifiers")) {
                            JSONArray jsonArrayIndustryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                            // l'ordre des isbn peut varier
                            for (int j = 0; j < jsonArrayIndustryIdentifiers.length(); j++) {
                                JSONObject jsonObjectIsbn = jsonArrayIndustryIdentifiers.getJSONObject(j);
                                if (jsonObjectIsbn.has("type")) {
                                    if (jsonObjectIsbn.getString("type").equals("ISBN_13")) {
                                        if (jsonObjectIsbn.has("identifier")) {
                                            isbn = jsonObjectIsbn.getString("identifier");
                                        }
                                    }

                                }
                            }
                        }


                        Log.i(TAG, "onResponse: isbn : " + isbn);

                        String coverUrl = "";
                        // recuperation de l'url de 'image de couverture
                        if (volumeInfo.has("imageLinks")) {
                            JSONObject jsonObjectImageLinks = volumeInfo.getJSONObject("imageLinks");
                            if (jsonObjectImageLinks.has("thumbnail")) {
                                coverUrl = jsonObjectImageLinks.getString("thumbnail");
                                coverUrl = convertirLienEnHttps(coverUrl);
                            }
                        }

                        Log.i(TAG, "onResponse: coverUrl : " + coverUrl);

                        bookArrayList.add(new ModelBook(coverUrl, titre, auteur, isbn, id));

                    }

                    adapterBook = new AdapterBook(getContext(), bookArrayList);
                    rvBookByKeyword.setAdapter(adapterBook);
                    adapterBook.setOnItemClickListener(RechercherFragment.this);

                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }


    @Override
    public void onItemClick(int position, View view) {
        Intent detailIntent = new Intent(getContext(), RecupererLivreISBN.class);
        ModelBook clickItemBook = bookArrayList.get(position);
        detailIntent.putExtra(ISBN, clickItemBook.getIsbn());
        detailIntent.putExtra(ID, clickItemBook.getId());

        startActivity(detailIntent);
    }





    public RechercherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RechercherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RechercherFragment newInstance(String param1, String param2) {
        RechercherFragment fragment = new RechercherFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rechercher, container, false);

        init(view);

        btnChercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookArrayList.clear();
                keyword = etKeyword.getText().toString().trim();
                //Toast.makeText(this, "motCle : " + keyword, Toast.LENGTH_SHORT).show();
                try {
                    parseJSON();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        btnClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyword.setText("");
                btnClearText.setVisibility(View.GONE);
            }
        });

        btnRechScanISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraXIntent = new Intent(getContext(), CameraXActivity.class);
                startActivity(cameraXIntent);
            }
        });

        if (checkPermission()) {
            //remplissageArrayListeEnDur();
        }
        btnClearText.setVisibility(View.INVISIBLE);

        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0) {
                    btnClearText.setVisibility(View.VISIBLE);
                } else {
                    btnClearText.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}