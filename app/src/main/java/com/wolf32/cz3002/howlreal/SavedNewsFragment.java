package com.wolf32.cz3002.howlreal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wolf32.cz3002.howlreal.adapters.NewsAdapter;
import com.wolf32.cz3002.howlreal.adapters.SavedNewsAdapter;
import com.wolf32.cz3002.howlreal.model.News;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SavedNewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavedNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedNewsFragment extends Fragment implements GetSavedNews.RetrieveListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SavedNewsFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SavedNewsAdapter mAdapter;
    private ListView savedNewsListView;

    public SavedNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedNewsFragment newInstance(String param1, String param2) {
        SavedNewsFragment fragment = new SavedNewsFragment();
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
        View mView = inflater.inflate(R.layout.fragment_news, container, false);

        savedNewsListView = (ListView) mView.findViewById(R.id.fragment_news_listView);

        GetSavedNews getSavedNews = new GetSavedNews(this);
        getSavedNews.loadSavedNews(getActivity());


        return mView;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSuccess(final ArrayList<News> newsList) {
        Log.e(TAG, "newsList, size onSuccess: " + newsList.size());

        mAdapter = new SavedNewsAdapter(Objects.requireNonNull(getContext()), newsList);
        savedNewsListView.setAdapter(mAdapter);

        savedNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                News currentNews = newsList.get(position);

                Intent newsArticleIntent = new Intent(savedNewsListView.getContext(), ReadNewsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", currentNews);
                newsArticleIntent.putExtras(bundle);
                newsArticleIntent.putExtra("position", position);

                // change this activity to read offline news.
                startActivity(newsArticleIntent);


            }
        });
    }

    @Override
    public void onFailure() {
        Log.e(TAG, "error: retriever listener onFailure.");

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
