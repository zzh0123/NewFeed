package ys.app.feed.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ys.app.feed.R;

/**
 * 营养知识
 */
public class NutritionKnowledgeFragment extends Fragment {


    public NutritionKnowledgeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the bottom_group_buy_person_num for this fragment
        return inflater.inflate(R.layout.fragment_nutrition_knowledge, container, false);
    }

}
