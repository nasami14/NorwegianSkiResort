package no.amirhjelperdeg.norwegianskiresort.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import no.amirhjelperdeg.norwegianskiresort.R;
import no.amirhjelperdeg.norwegianskiresort.activities.AccessLiftActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.NearByResortActivity;
import no.amirhjelperdeg.norwegianskiresort.activities.TransportationActivity;
import no.amirhjelperdeg.norwegianskiresort.database_models.ResortData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //
    private ImageView imgFindResort,imgNearByResort, imgAccessLift,imgTransport;

    FirebaseDatabase fdb;
    FirebaseAuth fAuth;

    SharedPreferences userLoginInfo;
    String roleId="";

    Menu allMenu;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        fdb= FirebaseDatabase.getInstance();
        fAuth= FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userLoginInfo =getContext().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        roleId=userLoginInfo.getString("roleId","");

        fdb= FirebaseDatabase.getInstance();
        fAuth= FirebaseAuth.getInstance();
        imgFindResort=getView().findViewById(R.id.img_find_resort_fraghome);
        imgNearByResort=getView().findViewById(R.id.img_nearby_resort_fraghome);
        imgAccessLift=getView().findViewById(R.id.img_accesslift_fraghome);

        imgTransport=getView().findViewById(R.id.img_trasnport_fraghome);

        imgFindResort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                findResort();
            }
        });

        imgNearByResort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),NearByResortActivity.class));
            }
        });

        imgTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),TransportationActivity.class));
            }
        });

        imgAccessLift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), AccessLiftActivity.class));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main,menu);
        allMenu=menu;
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        userLoginInfo =getContext().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        roleId=userLoginInfo.getString("roleId","");
        if(roleId.equalsIgnoreCase("user"))
        {
            menu.findItem(R.id.action_add_resort).setVisible(false);
            allMenu.findItem(R.id.action_add_resort).setVisible(false);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * this method will fetch the resort list from database
     */
    public void findResort()
    {
        ResortFragment fragment = new ResortFragment();
        FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame,fragment,"ResortList");
        fragmentTransaction.commitAllowingStateLoss();
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
