package com.example.heartbeat.ECG;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heartbeat.CSVFile;
import com.example.heartbeat.Command;
import com.example.heartbeat.MenuActivity;
import com.example.heartbeat.R;

public class FragmentECG extends Fragment{
    Button start;
    Button pause;


    Command MyCmd;
    FrameLayout frameLayout;
    TextView sensorField;
    View view;
    RealTimeGraphECG myGraph;
    Thread realTimeThread;
    Activity activity;
    Boolean threadFlag;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentecg, container, false);

        start = (Button)view.findViewById(R.id.start);
        pause = (Button)view.findViewById(R.id.pause);
        sensorField = (TextView)view.findViewById(R.id.ecg_rtor_value);
        myGraph = new RealTimeGraphECG(view);

        threadFlag = false;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((MenuActivity)getActivity()).setViewField(view, "ecg");
                ((MenuActivity)getActivity()).sendStrCmd(MyCmd.str_readecg2);
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                threadFlag = true;
                realTimeStart();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((MenuActivity)getActivity()).setViewField(view, "stop");
                ((MenuActivity)getActivity()).sendStrCmd(MyCmd.str_stop);
                start.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                threadFlag = false;
                realTimeThread.interrupt();
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        frameLayout = ((MenuActivity)getActivity()).getFrameLayout();

        MyCmd = new Command();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        ((MenuActivity)getActivity()).sendStrCmd(MyCmd.str_stop);
        ((MenuActivity)getActivity()).mode="stop";
        frameLayout.setVisibility(View.GONE);
        if(realTimeThread != null)realTimeThread.interrupt();
        super.onDetach();

    }



    public void realTimeStart(){
        realTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(threadFlag){
                    try {
                        Thread.sleep(90);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                ECGData ecgData = ((MenuActivity)activity).ecgData;
                                myGraph.addEntry(ecgData.getEcg1());
                                myGraph.addEntry(ecgData.getEcg2());
                                myGraph.addEntry(ecgData.getEcg3());
                                myGraph.addEntry(ecgData.getEcg4());
                                //myGraph.addEntry(((MenuActivity) activity).valueForGraph);


                        }
                    });
                }
            }
        });

        realTimeThread.start();
    }


}
