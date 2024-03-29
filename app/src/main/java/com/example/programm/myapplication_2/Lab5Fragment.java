package com.example.programm.myapplication_2;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lab5Fragment extends Fragment {

    final static String TAG="myLogsLab5";
    String authToken = null;
    boolean isAuthCompleted = false;
    String searchTextPodcast;

    View rootView;
    ListView listView;
    private EditText queryEditText;
//    ArrayAdapter adapter;
    CustomListAdapter adapter;

//    private ArrayList<String> podcastsList = new ArrayList<>();
    List<Podcast> PodcastsList = new ArrayList<>();

    public static Lab5Fragment newInstance() {
        return new Lab5Fragment();
    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.lab5_fragment, container, false);
//        return rootView;
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setup the listener for the fragment B
        ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PagerAgentViewModel.class).getMessageContainerB().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String msg) {
            authToken = msg;
            Log.i(TAG, "authToken: " + msg);

            if(authToken!=null && !authToken.equals("Default Message"))
                isAuthCompleted = true;
            else
                isAuthCompleted = false;
            Log.i(TAG, "IsAuthCompleted: " + isAuthCompleted);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.lab5_fragment, container, false);

        queryEditText = (EditText) rootView.findViewById(R.id.editTextSearhLab5);
        ImageButton searchPodcastButton = (ImageButton) rootView.findViewById(R.id.searchPodcastButton);
        searchPodcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTextPodcast = ""+queryEditText.getText();

                Log.i(TAG,"searchTextPodcast: "+ searchTextPodcast);
                new ThreadApiPodcasts().execute();
            }
        });

//        PodcastsList = getListData();
        Podcast vietnam = new Podcast("Vietnam", "vn", "url1");
        Podcast russia = new Podcast("Russia", "ru", "url2");


        PodcastsList.add(vietnam);
        PodcastsList.add(russia);
        PodcastsList.add(russia);
        listView = (ListView) rootView.findViewById(R.id.ListViewLab5);
//        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
//                android.R.layout.simple_list_item_1, podcastsList);
//        listView.setAdapter(adapter);


//        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
//                android.R.layout.simple_list_item_1, podcastsList);

        adapter = new CustomListAdapter(getActivity(), PodcastsList);
        listView.setAdapter(adapter);
        return rootView;
    }

    public class apiPodcastsClass {
        String FIO = null;
        //ссылка на решение
        apiPodcastsClass() {
            setTextViewSurname();
        }

        String versionApi = "5.89";

        private void setTextViewSurname() {
            if (isAuthCompleted && searchTextPodcast != null)
            {
                String url = "https://api.vk.com/method/podcasts.searchPodcast?search_string="+searchTextPodcast+"&offset=0&v="+versionApi+"&access_token="+authToken;
                Log.i(TAG + " api request", url);
//                Запрос к API
                Document doc=null;
                try{
                    doc = Jsoup.connect(url)
//                            .referrer("https://vk.com/")
//                        .userAgent("Mozilla/5.0")
//                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                            .ignoreContentType(true)
                            .get();
                } catch (Exception e)
                {
                    Log.d(TAG+" ошибка",e.toString());
                    e.printStackTrace();
                }
                //text внутренняя переменная
                String doc_body= Objects.requireNonNull(doc).body().text();
                Log.d(TAG," doctext = " + doc_body);
                FIO= parsingPodcasts(doc_body);
            }
        }

        String parsingPodcasts(String jsonfile) {

            String FIO = "";
            try {
                JSONObject dataJsonObj = new JSONObject(jsonfile);
                JSONArray podcasts = dataJsonObj.getJSONObject("response").getJSONArray("podcasts");

//                podcastsList.clear();
                PodcastsList.clear();
                String p_title;
                String p_url;
                String p_imgUrl;
                for (int i=0; i < podcasts.length(); i++) {
                    JSONObject podcast = podcasts.getJSONObject(i);
                    Log.d(TAG+i+" obj",""+podcast);
//                  Парсинг Подкаста
                    p_title = podcast.getString("title");
                    p_url = podcast.getString("url");
                    JSONObject coverImg = podcast.getJSONObject("cover").getJSONArray("sizes").getJSONObject(2);

                    Log.i(TAG+" coverImg",coverImg.toString());
                    p_imgUrl = coverImg.getString("url");
                    Log.i(TAG+" imgUrl",p_imgUrl);
                    PodcastsList.add(new Podcast(p_title, p_imgUrl, p_url));
                }
            } catch (JSONException e) {
                Log.d(TAG+" JSONExcept",e.toString());
            }

            return FIO;
        }

        String getFIO(){
            return FIO;
        }

    }
    //todo static
    @SuppressLint("StaticFieldLeak")
    private class ThreadApiPodcasts extends AsyncTask<String, Void, String> {
        private String StringRequest;

        @Override
        protected String doInBackground(String... arg) {
            apiPodcastsClass syte1 = new apiPodcastsClass();
            String fio=syte1.getFIO();
            //класс который захватывает страницу
            return fio;
        }
        @Override
        protected void onPostExecute(String fio) {
            Log.i(TAG,"onPostExecute is working");

//            Обновляем adapter для отображения
            adapter.notifyDataSetChanged();
//            if(adapter.getCount()!=0 && adapter!=null){
//                adapter.clear();
//            } else
//                adapter = new ArrayAdapter<String>();
        }

    }
    private  List<Podcast> getListData() {
        List<Podcast> PodcastsList = new ArrayList<Podcast>();
        Podcast vietnam = new Podcast("Vietnam", "vn", "123");
        Podcast usa = new Podcast("United States", "us", "url2");
        Podcast russia = new Podcast("Russia", "ru", "url3");


        PodcastsList.add(vietnam);
        PodcastsList.add(usa);
        PodcastsList.add(russia);

        return PodcastsList;
    }

}