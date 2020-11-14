package ys.app.feed.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ys.app.feed.R;
import ys.app.feed.constant.Constants;
import ys.app.feed.widget.x5.X5WebView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BatchingFragment extends Fragment {

    private View rootView, header;
    private LinearLayout ll_back; // 返回
    // x5
    private X5WebView x5_webview_batching;

    public BatchingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_batching, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ll_back = (LinearLayout) rootView.findViewById(R.id.ll_back);
        ll_back.setVisibility(View.INVISIBLE);

        // x5
        x5_webview_batching = (X5WebView) rootView.findViewById(R.id.x5_webview_batching);
        x5_webview_batching.loadUrl(Constants.url_accurate_batching);
    }

}
