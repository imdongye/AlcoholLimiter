package com.example.alcohollimiter;

import android.os.AsyncTask;

public class CounterTask extends AsyncTask<Integer, Integer, Integer> {
    @Override
    protected void onPreExecute() { // 1. UI thread

    }
    @Override
    protected Integer doInBackground(Integer... value) { // 2. worker thread
        return 0;
    }
    protected void onProgressUpdate(Integer... value) { // 3. UI thread

    }
    protected void onPostExecute(Integer result) { // 4. UI thread

    }
}
