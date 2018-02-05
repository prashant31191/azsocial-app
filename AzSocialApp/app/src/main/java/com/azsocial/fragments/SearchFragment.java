package com.azsocial.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.azsocial.R;
import com.azsocial.activities.MainActivity;
import com.azsocial.fragments.sub.TopHeadLinesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchFragment extends BaseFragment{

    @BindView(R.id.tvSearch)
    TextView tvSearch;

    int fragCount;
   /* public static NewsFragment newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, view);

        ( (MainActivity)getActivity()).updateToolbarTitle("Search");


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentNavigation != null) {
                    //mFragmentNavigation.pushFragment(NewsFragment.newInstance(fragCount + 1));
                    //mFragmentNavigation.pushFragment(TopHeadLinesFragment.newInstance(fragCount + 1));
                    mFragmentNavigation.pushFragment(TopHeadLinesFragment.newInstance((Object) "axios"));
                }
            }
        });


        ( (MainActivity)getActivity()).updateToolbarTitle((fragCount == 0) ? "Search" : "Sub Search "+fragCount);


    }


}
