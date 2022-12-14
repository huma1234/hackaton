package com.maksof.linkapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {
    private Context context;
    public static AlertDialog  dialog;
    private ArrayList<JSONObject> listDataHeader;
    private HashMap<String,List<String>> listHashMap;
    ArrayList<JSONObject> listOfAddedBy;
    ArrayList<String> colorList = new ArrayList<>();
    static String phoneNumber;
    private String highlight;
    private Activity activity;
    private static final String[] CALL_PERMISSION =
            {Manifest.permission.CALL_PHONE};
    public ExpandableListAdapter(Context context, ArrayList<JSONObject> listDataHeader,ArrayList<String> colorList, HashMap<String, List<String>> listHashMap, Activity activity,String highlight) {
        this.context = context;
        this.colorList = colorList;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.activity = activity;
        this.highlight = highlight;
    }

    @Override
    public int getGroupCount() {
        if(listDataHeader!=null)
        return listDataHeader.size();
        else return 0;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        try {
            return listDataHeader.get(groupPosition).getString("contactName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getNumber(int groupPosition) {
        try {
            return listDataHeader.get(groupPosition).getString("phoneNumber");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }



    public String getContactId(int groupPosition) {
        try {
            return listDataHeader.get(groupPosition).getString("contactId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<JSONObject> getAddedBy(int groupPosition) {
        try {
            Object getrow = listDataHeader.get(groupPosition).get("addedBy");
            LinkedTreeMap<Object,Object> t = (LinkedTreeMap) getrow;
            Object getrow1 = t.get("values");
            ArrayList<JSONObject> listOfAddedBy2 = (ArrayList<JSONObject>) getrow1;
            return listOfAddedBy2 ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Object getOccupation(int groupPosition) {
        try {
            return listDataHeader.get(groupPosition).getString("occupation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int addedBySize = 0;
        final ArrayList<String> clrList = new ArrayList<>();
        String contactName = (String)getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lab_list_item,null);
        }
        TextView labContactName = (TextView)convertView.findViewById(R.id.labName);
        TextView labMainLetter = (TextView)convertView.findViewById(R.id.labFLetter);
        TextView lab_user_occupation = (TextView) convertView.findViewById(R.id.lab_user_occupation);
        TextView lab_last_person_recommended = (TextView)convertView.findViewById(R.id.lab_last_person_recommended);
        TextView labTotalUsersShared = (TextView)convertView.findViewById(R.id.lab_total_users_shared);
        listOfAddedBy = getAddedBy(groupPosition);

        Object getrow3 = listOfAddedBy.get(0);
        LinkedTreeMap<Object,Object> t3 = (LinkedTreeMap) getrow3;
        Object getrow4 = t3.get("nameValuePairs");
        LinkedTreeMap<Object,Object> t4 = (LinkedTreeMap) getrow4;
        if(t4.get("name")!=null)
            lab_last_person_recommended.setText(t4.get("name").toString());
        addedBySize =listOfAddedBy.size()-1;
        lab_user_occupation.setText((String)getOccupation(groupPosition));

        if(addedBySize==0)
            labTotalUsersShared.setVisibility(View.GONE);
        else labTotalUsersShared.setVisibility(View.VISIBLE);
        String underlineText = "<u style='color:#000000;'>+"+addedBySize +" Others</u>";
        labTotalUsersShared.setText(HtmlCompat.fromHtml(underlineText, HtmlCompat.FROM_HTML_MODE_LEGACY));
        labTotalUsersShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddedBy(groupPosition,clrList);
            }
        });
        lab_last_person_recommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddedBy(groupPosition,clrList);
            }
        });

        String text = contactName;
        String textToBeColored = highlight;
        String htmlText = text.replaceAll("(?i)"+textToBeColored,"<font color='#3f51b5' background-color:'#000000'>"+textToBeColored +"</font>");
        if(highlight.equals(""))
            labContactName.setText(contactName);
        else
            labContactName.setText(Html.fromHtml(htmlText));

        String reg="[a-zA-Z0-9]";
        if(((contactName.charAt(0)+"").matches(reg)))
            labMainLetter.setText(Character.toUpperCase(contactName.charAt(0))+"");
        else labMainLetter.setText(Constants.firstChar);

        final GradientDrawable gradientDrawable = (GradientDrawable) labMainLetter.getBackground().mutate();
        gradientDrawable.setColor(Color.parseColor(colorList.get(groupPosition)));
        return convertView;
    }

    @Override
    public View getRealChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lab_list_item_child, null);
            }
        phoneNumber = (String)getNumber(groupPosition);
        final LinearLayout animate_lab_child = (LinearLayout) convertView.findViewById(R.id.animate_lab_child);
        ImageButton call = (ImageButton) convertView.findViewById(R.id.labCallBtn);
        ImageButton sms = (ImageButton) convertView.findViewById(R.id.labSmsBtn);
        ImageButton whatsApp = (ImageButton) convertView.findViewById(R.id.labWhatsappBtn);
        ImageButton info = (ImageButton) convertView.findViewById(R.id.labInfoBtn);


        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonClass.openWhatsApp(activity,phoneNumber);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    commonClass.call(phoneNumber,activity);
                } else {
                    callTask(activity);
                           }

            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonClass.sms(activity,phoneNumber);
            }
        });


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONArray jsonArray = new JSONArray();
                for(int i=0;i<getAddedBy(groupPosition).size();i++)
                {
                    try {
                        Object  getrow5 =(Object) getAddedBy(groupPosition).get(i);
                        LinkedTreeMap<Object,Object> t3 = (LinkedTreeMap) getrow5;
                        Object getrow = t3.get("nameValuePairs");
                        LinkedTreeMap<Object,Object> getrow4 = (LinkedTreeMap) getrow;
                        JSONObject jb = new JSONObject();
                        jb.put("id",getrow4.get("id"));
                        jb.put("name",getrow4.get("name"));
                        jb.put("phoneNumber",getrow4.get("phoneNumber"));
                        jb.put("date",getrow4.get("date"));
                        jsonArray.put(jb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("contactId",getContactId(groupPosition));
                    jsonObject.put("recommendedBy",jsonArray);
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
//                commonClass.apiIntegration(activity, "profile/getlabprofile", jsonObject.toString(), PreferenceUtils.getToken(activity),"getLabProfile");
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        callTask(activity);
          }

    static boolean hasCallPermissions(Activity activity) {
        return EasyPermissions.hasPermissions(activity, CALL_PERMISSION);
    }

    @AfterPermissionGranted(125)
    public static void callTask(Activity activity) {
        if (hasCallPermissions(activity)) {

            commonClass.call(phoneNumber,activity);
        } else {
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs access to your location and contacts to know where and who you are.", 125, CALL_PERMISSION);
        }
    }
    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {


    }

    class UAdded_Adapter extends BaseAdapter{
        ArrayList<JSONObject> arrayAddedByList;
        ArrayList<String> colorList;
        public  UAdded_Adapter( ArrayList<JSONObject> array, ArrayList<String> colorList){
            this.colorList = colorList;
            this.arrayAddedByList = array;
        }
        @Override
        public int getCount() {
            return this.arrayAddedByList.size();
        }

        @Override
        public Object getItem(int position) {
            return this.arrayAddedByList.get(position);
        }

        public Object getNumber(int position) {
            return value("phoneNumber",position);
        }

        public String getColor(int position) {

            return this.colorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if( convertView == null ){
                convertView = inflater.inflate(R.layout.common_list_item, parent, false);
            }

            TextView uRName = (TextView)convertView.findViewById(R.id.comName);
            TextView uRFLetter = (TextView)convertView.findViewById(R.id.comFLetter);
            TextView comContactNumber = (TextView)convertView.findViewById(R.id.comContactNumber);
            Object getrow3 = getItem(position);
            LinkedTreeMap<Object,Object> t3 = (LinkedTreeMap) getrow3;
            Object getrow4 = t3.get("nameValuePairs");
            LinkedTreeMap<Object,Object> t4 = (LinkedTreeMap) getrow4;
            String name = t4.get("name").toString();
            uRName.setText(name);

            String reg="[a-zA-Z0-9]";
            if((Character.toUpperCase(Character.toUpperCase(name.charAt(0)))+"").matches(reg))
                uRFLetter.setText(Character.toUpperCase(name.charAt(0))+"");
                     else uRFLetter.setText(Constants.firstChar);

            comContactNumber.setText(t4.get("phoneNumber").toString());
            final GradientDrawable gradientDrawable = (GradientDrawable) uRFLetter.getBackground().mutate();
            gradientDrawable.setColor(Color.parseColor(getColor(position)));
            return convertView;

        }


        public  String value(String key,int pos){
            Object getrow3 = this.arrayAddedByList.get(pos);
            LinkedTreeMap<Object,Object> t3 = (LinkedTreeMap) getrow3;
            Object getrow4 = t3.get("nameValuePairs");
            LinkedTreeMap<Object,Object> t4 = (LinkedTreeMap) getrow4;
            return t4.get(key).toString();
        }
    }
    public static void dialCallLab(Activity act){
        com.maksof.linkapp.commonClass.call(phoneNumber,act);
    }
    public void displayAddedBy(int groupPosition, ArrayList<String> clrList ){
        AlertDialog.Builder sBuilder = new AlertDialog.Builder(activity);
        View labShareUserView = activity.getLayoutInflater().inflate(R.layout.nof_users_recommed_modal, null);
        sBuilder.setView(labShareUserView);
        TextView heading = (TextView)labShareUserView.findViewById(R.id.groupName);
        heading.setGravity(Gravity.CENTER_HORIZONTAL);
        ListView uR_List = (ListView)labShareUserView.findViewById(R.id.user_recommended);
        ImageView close = (ImageView)labShareUserView.findViewById(R.id.closeIcon);

        if(getAddedBy(groupPosition) != null){
            for (int i=0;i<getAddedBy(groupPosition).size();i++){
                clrList.add(commonClass.randomColors());
            }
        }
        UAdded_Adapter ur_adapter = new UAdded_Adapter(getAddedBy(groupPosition),clrList);
        uR_List.setAdapter(ur_adapter);
        ur_adapter.notifyDataSetChanged();
        dialog = sBuilder.create();
        if(!activity.isFinishing())
            dialog.show();
        /*dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);*/

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void swapItems(Context context,ArrayList<String> colorList,ArrayList<JSONObject> listDataHeader,HashMap<String,List<String>> listHashMap,Activity activity,String highlight) {
        this.context = context;
        this.colorList = colorList;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.activity = activity;
        this.highlight = highlight;
    }
}
