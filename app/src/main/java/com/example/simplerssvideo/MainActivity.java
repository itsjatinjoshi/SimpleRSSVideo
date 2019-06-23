package com.example.simplerssvideo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvRss;
    ArrayList<String> titles;
    ArrayList<String> links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       lvRss= findViewById(R.id.lvRSS);
       links= new ArrayList<String>();
       titles= new ArrayList<String>();


       lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Uri uri = Uri.parse(links.get(position));
               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
               startActivity(intent);

           }
       });
       new PorgressInBackgroung().execute();
    }

    public InputStream getInputStream(URL url){

        try{
            return url.openConnection().getInputStream();

        }
        catch (IOException e){
            return null;
        }
    }
    public  class PorgressInBackgroung extends AsyncTask<Integer, Void, String>{


        ProgressDialog progressDialog= new ProgressDialog(MainActivity.this);
        Exception exception = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Page is loading... Please Wait..");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            return null;
            

            try{
                String url = "http://feeds.news24.com/articles/fin24/tech/rss";
                // URL url = new URL("http://feeds.news24.com/articles/fin24/tech/rss");

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideItem =false;
                int eventType = xpp.getEventType();
                while (eventType!= XmlPullParser.END_DOCUMENT) {

                    if (eventType != XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if(insideItem){
                            titles.add(xpp.nextText());
                        }
                        }
                        else if(xpp.getName().equalsIgnoreCase("linl")){
                            if(insideItem){
                                links.add(xpp.nextText());
                            }
                        }
                        else if(eventType== XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item"))
                        {
                            insideItem= false;
                        }
                        eventType= xpp.next();
                    }

                }


            }
            catch (MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                exception=e;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.activity_list_item, titles);
            lvRss.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }

}
