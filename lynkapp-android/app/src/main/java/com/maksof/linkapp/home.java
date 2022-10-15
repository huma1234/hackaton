package com.maksof.linkapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadContacts(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    TextView home, lab, groups,directory;
    static ViewPager viewPager;
    PagerViewAdapter pagerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton triggerSearchBar = getActivity().findViewById(R.id.triggerSearchBar);
        triggerSearchBar.setVisibility(View.GONE);
        home = (TextView)rootView.findViewById(R.id.phoneBookText);
        lab = (TextView)rootView.findViewById(R.id.labText);
        groups = (TextView)rootView.findViewById(R.id.groupsText);
        directory = (TextView)rootView.findViewById(R.id.directoryText);
        viewPager = (ViewPager)rootView.findViewById(R.id.homeFragment);

        pagerViewAdapter = new PagerViewAdapter(getChildFragmentManager());

        viewPager.setAdapter(pagerViewAdapter);
        viewPager.setCurrentItem(1);
        PreferenceUtils.save(1+"","tab",getContext());

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        viewPager.setCurrentItem(0); }
        });
        directory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3); }
        });
        lab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonClass.apiIntegration(getContext(), "group/getgroupsofuser", "", PreferenceUtils.getToken(getContext()), "getGroupOfUser");
                viewPager.setCurrentItem(2);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    //commonClass.apiIntegration(getActivity(), "notification/getusernotification","",PreferenceUtils.getToken(getActivity()), "getNotification");
                    JSONObject jsonObject = new JSONObject();
                    Constants.imgsURL.removeAll(Constants.imgsURL);

                onChangeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        return rootView;
    }
    @SuppressLint("ResourceType")
    private void onChangeTab(int position) {
        PreferenceUtils.save(position+"","tab",getContext());
        ImageButton triggerSearchBar = getActivity().findViewById(R.id.triggerSearchBar);
        if (position == 0){
            triggerSearchBar.setVisibility(View.VISIBLE);
            loadContacts(getContext());
            home.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            home.setBackgroundColor(Color.parseColor("#80000000"));


            lab.setTextColor(Color.parseColor("#a9a9a9"));
            lab.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            groups.setTextColor(Color.parseColor("#a9a9a9"));
            groups.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            directory.setTextColor(Color.parseColor("#a9a9a9"));
            directory.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));


        }
        else if (position == 1){
            triggerSearchBar.setVisibility(View.VISIBLE);
            home.setTextColor(Color.parseColor("#a9a9a9"));
            home.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));


            lab.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            lab.setBackgroundColor(Color.parseColor("#80000000"));

            groups.setTextColor(Color.parseColor("#a9a9a9"));
            groups.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            directory.setTextColor(Color.parseColor("#a9a9a9"));
            directory.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

        }

        else if (position == 2){
            triggerSearchBar.setVisibility(View.VISIBLE);
            home.setTextColor(Color.parseColor("#a9a9a9"));
            home.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            lab.setTextColor(Color.parseColor("#a9a9a9"));
            lab.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            directory.setTextColor(Color.parseColor("#a9a9a9"));
            directory.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            groups.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            groups.setBackgroundColor(Color.parseColor("#80000000"));

        }

        else if (position == 3){
            triggerSearchBar.setVisibility(View.GONE);
            home.setTextColor(Color.parseColor("#a9a9a9"));
            home.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            lab.setTextColor(Color.parseColor("#a9a9a9"));
            lab.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            groups.setTextColor(Color.parseColor("#a9a9a9"));
            groups.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));

            directory.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            directory.setBackgroundColor(Color.parseColor("#80000000"));
        }
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
public static void onBackPressed(Activity activity){
    if (viewPager.getCurrentItem() != 1) {
        viewPager.setCurrentItem(1,true);
    }else{
        AlertDialog.Builder sBuilder = new AlertDialog.Builder(activity);
        View sView = activity.getLayoutInflater().inflate(R.layout.close_app_permission, null);
        final TextView ok = (TextView) sView.findViewById(R.id.closeOK);
        final TextView cancel = (TextView) sView.findViewById(R.id.closeCancel);
        sBuilder.setView(sView);
        AlertDialog dialog;dialog = sBuilder.create();

        if(!((Activity) activity).isFinishing())
        {
            dialog.show();
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //show dialog
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                activity.startActivity(intent);
            }
        });
    }
}
public void loadContacts(Context cxt){
            try {
                commonClass.myContacts(cxt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
}
}
