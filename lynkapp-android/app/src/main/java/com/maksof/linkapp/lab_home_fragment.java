package com.maksof.linkapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class lab_home_fragment extends Fragment {

    EditText bsnsName, bsnsOccup, bsnsFeed,bsnsNum, bsnsWeb, bsnsEmail;
    static ExpandedHeightGridView bsnsImageGridView ;

    AutocompleteSupportFragment autocompleteFragment;
    Button uploadImage;
    TextView ratingText;
    RatingBar bsnsRatingBar;
    String address1 = "";
    String address;
    static AlertDialog dialog;
    public static AnimatedExpandableListView labListView;
    public static ExpandableListAdapter labListAdapter;
    public static HashMap<String,List<String>> listHashMap;
    public static   ArrayList<JSONObject> listLabContacts;
    public static TextView noLabs;

    View bView;
    public static ArrayList<String> colorList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View labView =  inflater.inflate(R.layout.lab_home_fragment, container,false);
        labListView = (AnimatedExpandableListView)labView.findViewById(R.id.labExpandList);
        final SwipeRefreshLayout pullToRefresh = labView.findViewById(R.id.phoneHome2);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                APIServices.updateLab(getActivity());
                pullToRefresh.setRefreshing(false);
            }
        });
        labListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (labListView.getChildAt(0) != null) {
                    pullToRefresh.setEnabled(labListView.getFirstVisiblePosition() == 0 && labListView.getChildAt(0).getTop() == 0);
                    pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            APIServices.updateLab(getActivity());
                            pullToRefresh.setRefreshing(false);
                        }
                    });

                }
            }
        });
        noLabs = (TextView) labView.findViewById(R.id.noLabs);
        Places.initialize(getContext(), "AIzaSyCYlFhfg9LErOaVm_JivuovEcyh2CpuIN0");
        PlacesClient placesClient = Places.createClient(getContext());
        initData(getActivity());

        labListView.setFocusableInTouchMode(true);
        labListView.requestFocus();
        labListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            int previousGroup=-1;
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {


                if (labListView.isGroupExpanded(groupPosition)) {
                    labListView.collapseGroupWithAnimation(groupPosition);
                    previousGroup=-1;
                } else {
                    labListView.expandGroupWithAnimation(groupPosition);
                    if(previousGroup!=-1){
                        labListView.collapseGroupWithAnimation(previousGroup); }
                    previousGroup=groupPosition;
                }
                return true;
            }
        });
        Button businessRecommended = (Button)labView.findViewById(R.id.businessRecommended);
        businessRecommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder businessBuilder;
                businessBuilder = new AlertDialog.Builder(getContext());
                try {
                    bView = getActivity().getLayoutInflater().inflate(R.layout.business_recommended, null);
                } catch (InflateException e) {

                }

                if (bView != null) {
                    ViewGroup parent = (ViewGroup) bView.getParent();
                    if (parent != null) {
                        parent.removeAllViews();
                    }
                }

                Places.initialize(getContext(), "AIzaSyCYlFhfg9LErOaVm_JivuovEcyh2CpuIN0");
                PlacesClient placesClient = Places.createClient(getContext());
                autocompleteFragment = (AutocompleteSupportFragment) getActivity().getSupportFragmentManager ().findFragmentById(R.id.location_autocomplete_fragment6);

                if (autocompleteFragment != null) {
                    EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
                    if (etPlace != null)
                    {
                        etPlace.setText("");
                        etPlace.setBackgroundColor(Color.parseColor("#ededed"));
                        etPlace.setHintTextColor(Color.parseColor("#959595"));
                        etPlace.setPadding(5,0,0,0);
                        etPlace.setTextSize(18);
                    }
                    if((View)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button)!=null)
                        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);

                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();
                    autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME));
                    autocompleteFragment.setOnPlaceSelectedListener(new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {

                        @Override
                        public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                            autocompleteFragment.setText(place.getName());
                            address1 = place.getName();
                            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                            if(autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button)!=null)
                                autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setBackgroundColor(Color.parseColor("#ededed"));

                        }

                        @Override
                        public void onError(Status status) {
                            // TODO: Handle the error.
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });

                }
                if(bView!=null){
                    bsnsName = (EditText) bView.findViewById(R.id.bsnsName);
                    bsnsEmail = (EditText) bView.findViewById(R.id.bsnsEmail);
                    bsnsWeb = (EditText) bView.findViewById(R.id.bsnsWebsite);
                    bsnsNum = (EditText) bView.findViewById(R.id.bsnsPhone);
                    bsnsOccup = (EditText) bView.findViewById(R.id.bsnsOccupation);
                    bsnsFeed = (EditText) bView.findViewById(R.id.bsnsDesc);
                    uploadImage = (Button) bView.findViewById(R.id.uploadImage);
                    ratingText = (TextView) bView.findViewById(R.id.ratingText);
                    bsnsRatingBar = (RatingBar) bView.findViewById(R.id.bsnsRatingBar);
                    Constants.imgsURL.removeAll(Constants.imgsURL);
                    bsnsName.setText("");
                    bsnsEmail.setText("");
                    bsnsWeb.setText("");
                    bsnsNum.setText("");
                    bsnsOccup.setText("");
                    bsnsFeed.setText("");
                    ratingText.setText("");
                    bsnsName.setError(null);
                    bsnsEmail.setError(null);
                    bsnsWeb.setError(null);
                    bsnsNum.setError(null);
                    bsnsOccup.setError(null);
                    bsnsFeed.setError(null);
                    bsnsRatingBar.setRating(0.0f);
                    LinearLayout hide = (LinearLayout)bView.findViewById(R.id.hideKeyboard);
                    hide.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(bView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                            return true;
                        }
                    });
                    LayerDrawable stars = (LayerDrawable) bsnsRatingBar.getProgressDrawable();
                    stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.lightGrey), PorterDuff.Mode.SRC_ATOP);

                    bsnsName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    bsnsOccup.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    bsnsFeed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    ratingText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


                    bsnsImageGridView = (ExpandedHeightGridView)bView.findViewById(R.id.bsnsRecImgCon);
                    bsnsImageGridView.setVisibility(View.GONE);
                    Constants.imgsURL.removeAll(Constants.imgsURL);
                    autocompleteFragment.setHint("Address");
                    focusChange(bsnsName);
                    focusChange(bsnsEmail);
                    focusChange(bsnsWeb);
                    focusChange(bsnsNum);
                    focusChange(bsnsOccup);
                    focusChange(bsnsFeed);

                    bsnsRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        }
                    });


                    uploadImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                commonClass.selectImage(getActivity(),"images");

                                // Do the file write
                            } else {
                                // Request permission from the user
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }

                        }
                    });

                    Button share = (Button) bView.findViewById(R.id.shareBsnsProfile);
                    Button cancel = (Button) bView.findViewById(R.id.closeBsnsDialogue);

                    businessBuilder.setView(bView);
                    dialog = businessBuilder.create();
                    if(!(getActivity().isFinishing()))
                    {
                        dialog.show();
                        dialog.getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                    try {
                        dialog.setCanceledOnTouchOutside(true);
                    }
                    catch (Exception e){
                    }

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            com.maksof.linkapp.commonClass.hideSoftKeyboard(getActivity());
                            String name, num, email, website, occupation,  bsnsF, rating;
                            name = bsnsName.getText().toString();
                            num = bsnsNum.getText().toString();
                            email = bsnsEmail.getText().toString();
                            website = bsnsWeb.getText().toString();
                            occupation = bsnsOccup.getText().toString();
                            address = "";
                            bsnsF = bsnsFeed.getText().toString();
                            rating = bsnsRatingBar.getRating() + "";
                            PreferenceUtils.save(rating, "labContactRating", getContext());
                            PreferenceUtils.save(bsnsF, "labContactFeedBack", getContext());
                            boolean flag = true;
                            if ("".equals(occupation) || occupation == null) {
                                flag = false;
                                bsnsOccup.setError("Required feild");
                            }
                            if ("".equals(name) || name == null) {
                                flag = false;
                                bsnsName.setError("Enter Name");
                            }

                            String nameReg="[a-zA-Z][a-z A-Z.\\d]{1,20}$";
                            String emailReg="^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
                            String numReg ="[+|0-9][0-9]{1,20}$";
                            if(!name.matches(nameReg)) {
                                flag=false;
                                bsnsName.setError("Invalid First Name");

                            }
                            if(email!=null&&(!email.equals(""))){
                                if(!email.matches(emailReg)) {
                                    flag=false;
                                    bsnsEmail.setError("Invalid email address");
                                }
                            }

                            if(!num.matches(numReg)) {
                                flag=false;
                                bsnsNum.setError("Invalid Number");

                            }

                            if ("".equals(bsnsF) || bsnsF == null) {
                                flag = false;
                                bsnsFeed.setError("Required feild");
                            }

                            if(bsnsRatingBar.getRating()==0.0){
                                flag=false;
                                toast("Required field");
                                LayerDrawable stars = (LayerDrawable) bsnsRatingBar.getProgressDrawable();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                    stars.getDrawable(0).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                                else stars.getDrawable(0).setColorFilter(Color.parseColor("#ff9999"), PorterDuff.Mode.SRC_ATOP);

                            }
                            if(bsnsRatingBar.getRating()>0.0){
                                LayerDrawable stars = (LayerDrawable) bsnsRatingBar.getProgressDrawable();
                                stars.getDrawable(0).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                            }

                            if (flag == true) {
                                num = num.replaceAll("[^0-9]", "");

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("contactName", name);
                                    jsonObject.put("phoneNumber", num);
                                    jsonObject.put("email", email);
                                    jsonObject.put("latitude", Constants.latitude + "");
                                    jsonObject.put("longitude", Constants.longitude + "");
                                    jsonObject.put("occupation", occupation);
                                    jsonObject.put("description", bsnsF);
                                    jsonObject.put("address", address1);
                                    jsonObject.put("website", website);
                                    jsonObject.put("directoryProfile", "LAB");
//                                    commonClass.apiIntegration(getContext(), "contact/createcontact", jsonObject.toString(), PreferenceUtils.getToken(getContext()), "recommendUserLab");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                }
            }
        });



        return labView;
    }

    public static void initData(final Activity activity) {
        final ArrayList<String> clrList = new ArrayList<>();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listLabContacts = new ArrayList<>();
                listHashMap = new LinkedHashMap<>();
                listLabContacts = PreferenceUtils.getGroupContactsJSON("getLabContacts", activity);
                if (listLabContacts != null) {

                    try {
                        for (int i = 0; i < listLabContacts.size(); i++) {
                            listHashMap.put(listLabContacts.get(i).getString("contactName"), null);
                            clrList.add(commonClass.randomColors());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                        if(listLabContacts.size()==0){
                            labListView.setVisibility(View.GONE);
                            noLabs.setVisibility(View.VISIBLE);
                        }else {
                            labListView.setVisibility(View.VISIBLE);
                            noLabs.setVisibility(View.GONE);
                        }
                    if(labListAdapter==null)
                        labListAdapter = new ExpandableListAdapter(activity,listLabContacts,clrList,listHashMap, activity,"");
                    else
                    {
                        labListAdapter.swapItems((Context)activity,clrList,listLabContacts,listHashMap, activity,"");
                    }
                    labListView.setAdapter(labListAdapter);
                    labListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private View parentLayout;
    int REQUEST_READ_CONTACTS = 123;
    private boolean contactPermissionNotGiven,flag=false;
    public boolean askContactsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(parentLayout, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            if (getActivity() == null) {
                                return;
                            }
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            getActivity().startActivity(intent);
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);

                        }
                    }).show();
            return true;
        } else if (contactPermissionNotGiven) {
            Toast.makeText(getContext(),"Contact Access Permission Not Given",Toast.LENGTH_SHORT).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
            contactPermissionNotGiven = true;

        }
        return false;
    }
    public static void closeModal(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
    }
    public void toast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG);
    }

    static int inc;
    public static void images(final Activity activity){
        final ArrayList<JSONObject> imgs = PreferenceUtils.getGroupNamesJSON("imgURLSlist",activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap resized;
                if(bsnsImageGridView!=null)
                {
                    for( inc = 0 ;inc<imgs.size();inc++){
                        ArrayList<String> imgurl = new ArrayList<>();
                            for( inc = 0 ;inc<imgs.size();inc++) {
                                imgurl.add(com.maksof.linkapp.commonClass.urlBitmap(activity, inc));
                            }

                        bsnsImageGridView.setExpanded(true);
                        bsnsImageGridView.setVisibility(View.VISIBLE);
                        lab_home_fragment.ImageAdapter imageAdapter = new lab_home_fragment.ImageAdapter(imgurl,activity);
                        bsnsImageGridView.setAdapter(imageAdapter);

                    }
                }
            }
        });

    }
    static class ImageAdapter extends BaseAdapter {
        ArrayList<String> images;
        Activity activity;
        ImageAdapter(ArrayList<String> review,Activity activity){

            this.images = review;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return  this.images.size();
        }

        @Override
        public Object getItem(int position) {
            return this.images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = activity.getLayoutInflater().inflate(R.layout.recommneded_images, null);
            final ImageView imgViewDirec = (ImageView) convertView.findViewById(R.id.imgViewDirec);
            final ImageView closeBtn = (ImageView) convertView.findViewById(R.id.btn_close1);

            final String url = (String)this.getItem(position);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(30f);
                    circularProgressDrawable.start();
                    GlideApp.with(activity)
                            .load(com.maksof.linkapp.commonClass.imgUrl+url)
                            .placeholder(circularProgressDrawable)
                            .into(imgViewDirec);
                    imgViewDirec.setVisibility(View.VISIBLE);
                }
            });
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> imgs = PreferenceUtils.getGroupString("imgString",activity);
                    if(imgs!=null)
                        imgs.remove(url);
                    commonClass.updateConstantsImageUrl(imgs);
                    PreferenceUtils.saveGroupString(imgs,"imgString",activity);
                    imgViewDirec.setVisibility(View.GONE);
                    closeBtn.setVisibility(View.GONE);
                }
            });
            return convertView;
        }

    }
    public  void focusChange(EditText et){
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        ImageButton triggerSearchBar = (ImageButton)getActivity().findViewById(R.id.triggerSearchBar);
        triggerSearchBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    public static void search(Activity activity,final String newText){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinkedHashMap listHashMap1 = new LinkedHashMap<>();
                    ArrayList<JSONObject> listLabSearchContacts = new ArrayList<>();
                    listLabContacts = PreferenceUtils.getGroupContactsJSON("getLabContacts", activity);
                    initData(activity);

                    if (listLabContacts != null)
                        for (int i = 0; i < listLabContacts.size(); i++) {
                            try {
                                if (((listLabContacts.get(i).getString("occupation").toLowerCase().contains(newText.toLowerCase()))) || (listLabContacts.get(i).getString("contactName").toLowerCase().contains(newText.toLowerCase())))
                                    listLabSearchContacts.add(listLabContacts.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    if (listLabSearchContacts != null) {

                        try {
                            for (int i = 0; i < listLabSearchContacts.size(); i++) {
                                listHashMap1.put(listLabSearchContacts.get(i).getString("contactName"), null);
                                colorList.add(commonClass.randomColors());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ExpandableListAdapter labListAdapter1 = new ExpandableListAdapter(activity, listLabSearchContacts, colorList, listHashMap1, activity, newText);
                        labListView.setAdapter(labListAdapter1);
                        labListAdapter1.notifyDataSetChanged();
                        labListView.setFocusableInTouchMode(true);
                        labListView.requestFocus();
                        labListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            int previousGroup=-1;
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {


                                if (labListView.isGroupExpanded(groupPosition)) {
                                    labListView.collapseGroupWithAnimation(groupPosition);
                                    previousGroup=-1;
                                } else {
                                    labListView.expandGroupWithAnimation(groupPosition);
                                    if(previousGroup!=-1){
                                        labListView.collapseGroupWithAnimation(previousGroup); }
                                    previousGroup=groupPosition;
                                }
                                return true;
                            }
                        });
                    }
                }
            });
    }

}
