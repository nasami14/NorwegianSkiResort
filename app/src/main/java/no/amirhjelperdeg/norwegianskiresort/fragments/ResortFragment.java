package no.amirhjelperdeg.norwegianskiresort.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.activities.ResortDetail;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;
import no.amirhjelperdeg.norwegianskiresort.adapters.MyListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResortFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResortFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResortFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // declare firebasedb objects
    private  FirebaseDatabase fdb;
    private FirebaseAuth fAuth;

    private ListView resortLists;

    private ArrayList<HashMap<String,String>> resortDataList=new ArrayList<HashMap<String,String>>();


    ListAdapter listAdapter;
    SharedPreferences userLoginInfo;
    String roleId="";

    public ResortFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResortFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResortFragment newInstance(String param1, String param2) {
        ResortFragment fragment = new ResortFragment();
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

        fdb=FirebaseDatabase.getInstance();
        fAuth=FirebaseAuth.getInstance();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resort, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resortLists= (ListView)getView().findViewById(R.id.list_resort);


        resortLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent  intent = new Intent(getContext(), ResortDetail.class);
                HashMap<String,String> resortMap=new HashMap<String, String>();
                resortMap=resortDataList.get(position);
                intent.putExtra("resortName",resortMap.get("name"));
                startActivity(intent);

            }
        });

        getResortData();




    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        userLoginInfo =getContext().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        roleId=userLoginInfo.getString("roleId","");
        if(roleId.equalsIgnoreCase("user"))
        {
            menu.findItem(R.id.action_add_resort).setVisible(false);
        }
    }
    // this method will retrieve the resort names  along with its  all data
    public void getResortData()
    {

        DatabaseReference dbRef= fdb.getReference("resorts");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int index=0;

               for(DataSnapshot resort :dataSnapshot.getChildren())
               {
                   ResortData resortData= resort.getValue(ResortData.class);
                   //Toast.makeText(getContext(),"--"+resortData.getAddress(),Toast.LENGTH_LONG).show();
                   HashMap<String, String> data= new HashMap<String, String>();
                   data.put("name",resort.getKey());
                   data.put("slope",resortData.getSlopDistance());
                   data.put("lifts",resortData.getTotalLifts());
                   data.put("charge",resortData.getTotalCharges());

                   resortDataList.add(data);

                   if(index>10)
                       break;
                   index ++;
               }

                listAdapter = new MyListAdapter(getActivity(),resortDataList);
               resortLists.setAdapter(listAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
