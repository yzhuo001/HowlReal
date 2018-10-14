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

                if (currentNews.getTitle().equals("World Bank provides US$1 billion standby facility for Indonesia disaster relief")){
                    currentNews.setContent("NUSA DUA, Indonesia: The World Bank on Sunday (Oct 14) said it would provide a standby loan of up to US$1 billion for the Indonesian government for relief and reconstruction efforts in the country's two islands that were hit by earthquakes and a tsunami.\n\n" +
                            "The multilateral lender said the fund will also give a US$5 million grant for technical assistance to ensure reconstruction is robust and community-led, it said in a statement.\n\n" +
                            "The World Bank's chief executive officer Kristalina Georgieva on Friday visited the city of Palu, which was rocked by a 7.5-magnitude earthquake and a tsunami on Sep 28. More than 2,000 people died in Palu and its surrounding areas.\n\n" +
                            "In the resort island of Lombok, over 500 people were killed by a series of earthquakes in July and August.\n\n" +
                            "Indonesia is hosting the annual meetings of the World Bank and the International Monetary Fund in Bali, just west of Lombok.\n\n" +
                            "The World Bank said its preliminary report showed that the physical loss from damage on the infrastructure, residential and non-residential property in Sulawesi was around US$531 million. It noted that the report did not take into account loss of life, lost land or the disruption to the economy through lost jobs, livelihoods and business.\n\n" +
                            "The loan facility package could include cash transfers to the poorest 150,000 affected families for a period between 6 months to one year, which could help during the recovery phase, the bank said.\n\n" +
                            "The Indonesian government plans to launch a new strategy to fund disaster recovery, which could include insuring state assets and selling \"catastrophe bonds\", Indonesia's head of fiscal policy office Suahasil Nazara said earlier this month.\n\n" +
                            "Read more at https://www.channelnewsasia.com/news/world/world-bank-provides-us-1-billion-standby-facility-for-indonesia-disaster-relief-10825870");
                }
                if (currentNews.getTitle().equals("Get ready; Spreading fake news and rumors in UAE would cost you Dh1 million")){
                    currentNews.setContent("Dubai Police official has stated that spreading fake news and rumours by using social media platform is a threat to national security. During a panel discussion, organized by police's Al Ameen service officials also stated that spreading such rumours could cause losses to the tune of \"millions of dirhams.\"\n\n"+
                            "The panel discussion was conducted to spread the awareness among the citizen about the surge of fake news. Jamal Ahmed from Al Ameen service stated that circulation of false stories is considered as a criminal offence and a convict can face a fine up to Dh1 million (S$ 375142.92).\n\n" +
                            "During the discussion, Ahmed also added that as per their analysis most of the fake news came from people and organisations, who want to increase their followers on social media.\n\n" +
                            "He said that there are many rumours which were shared to destroy the reputation of the country. These identified fake news include strikes by Houthi militia at Abu Dhabi and Dubai airports, abandoned vehicles in Dubai, stories about the murder of a popular Moroccan singer in Dubai, the availability of the drug in school premises and misleading photos that showed university students as martyrs in the Yemen War.\n\n" +
                            "As per Ahmed, one of the viral posts that stated a certain 'ruler's court' was giving money away, while another bizarre write-up claimed that Dubai was a ghost town.\n\n" +
                            "A UAE (United Arab Emirates) based daily English language newspaper, Khaleej Times reported that according to Ahmed, one of the rumours that went viral on social media, suggested that \"UAE had launched a robot to spy on Friday prayer-goers. However, it later emerged that the interactive robot was in fact invented by students of the Al Ain University.\"\n\n" +
                            "However, considering the popularity of fake news market on social media, Ahmed said now the time has come to spread the awareness across all institutions and in this operation family and teachers have an important role to train the youth on how to handle the information they come across while using an online platform.\n\n" +
                            "Ahmed also advised people to report a news or statement if it doesn't consist reliable sources. \"Propaganda is tough to combat. It is an organised evil that is designed to cause fear using lies and half-truths. Defending the country against it takes patience, skills and strategy. It can only be countered through awareness,\" he further added.\n\n" +
                            "In addition, Al Ameen said, media, as well as the news platforms of government officers, including police and other concerned authorities, will remain the most reliable sources of information.\n\n" +
                            "UAE is not the first country, which faced issues related to the emergence of fake news, as a few weeks ago the chairman of Singapore's Monetary Authority, Tharman Shanmugaratnam became the victim of a false story that claimed he is a \"venture capitalist\" who confirmed the entry of Singapore into the bitcoin cryptocurrency market. The Singapore Government and the MAS clarified that this story was published by a fraudulent website, which has been soliciting investments in Bitcoins by using fabricated comments from Deputy Prime Minister Shanmugaratnam.\n\n" +
                            "Even, South Korean Prime minister Lee Nak-yon has decided to take serious actions to combat fake news, as he believes these stories and false information it is a 'destroyer of democracy.' He also had to face criticism due to a misleading photo, which was shared online to defame his personality.\n\n");
                }
                Intent newsArticleIntent = new Intent(savedNewsListView.getContext(), ReadSavedNewsActivity.class);
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
