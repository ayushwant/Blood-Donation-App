package com.example.blooddonationapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blooddonationapp.Activities.WebViewActivity;
import com.example.blooddonationapp.Adapters.FeedAdapter;
import com.example.blooddonationapp.ModelClasses.Feed;
import com.example.blooddonationapp.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    RecyclerView feedRv;
    List<Feed> feedList;
    FeedAdapter.RvClickListener clickListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        if(getActivity()!=null)
//        getActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
////                if(getActivity()!=null)
////                    getActivity().finish();
//
//                getActivity().finishAndRemoveTask();
////                System.exit(0);
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        feedList = new LinkedList<>();

        feedRv = view.findViewById(R.id.feedRV);

        setRvOnClickListener();
        feedRv.setLayoutManager(new LinearLayoutManager(getContext()));

        FeedAdapter feedAdapter = new FeedAdapter(getContext(), feedList, clickListener);
        feedRv.setAdapter(feedAdapter);

//        feedList.add(0, new Feed("hello just a check", R.drawable.background_1, "https://www.youtube.com/watch?v=vBxNDtyE_Co"));
//        feedList.add(0, new Feed("again just a check 😉", R.drawable.pic1, "https://www.google.com/search?q=hello"));

        return view;
    }

    private void setRvOnClickListener() {
        clickListener = new FeedAdapter.RvClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("link", feedList.get(position).getLink());
                startActivity(intent);
            }
        };
    }

//    private void layoutAnimation(RecyclerView recyclerView)
//    {
//        Context context = recyclerView.getContext();
//        LayoutAnimationController layoutAnimationController =
//                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
//
//        recyclerView.setLayoutAnimation(layoutAnimationController);
//
//        recyclerView.getAdapter().notifyDataSetChanged();
//        recyclerView.scheduleLayoutAnimation();
//    }

}