package org.kodluyoruz.milliyetwear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wear.widget.drawer.WearableActionDrawerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends WearableActivity implements WearableNavigationDrawerView.OnItemSelectedListener, MenuItem.OnMenuItemClickListener {

    private TextView mTextView;

    private WearableNavigationDrawerView mWearableNavigationDrawerView;
    private WearableActionDrawerView mWearableActionDrawerView;

    private ArrayList<News> mNews;
    private int mSelectedNews;

    private NewsFragment mNewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mTextView = (TextView) findViewById( R.id.text );

        // Enables Always-on
        setAmbientEnabled();

        mNews = initializeNews();
        mSelectedNews = 0;

        mNewsFragment = new NewsFragment();
        Bundle args = new Bundle();

        args.putString( "content", mNews.get( 0 ).getContent() );
        args.putString( "title", mNews.get( 0 ).getName() );

        mNewsFragment.setArguments( args );
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace( R.id.content_frame, mNewsFragment ).commit();

        mWearableNavigationDrawerView = findViewById( R.id.navigation_top_drawer );
        mWearableNavigationDrawerView.setAdapter( new NavigationAdapter(this) );
        mWearableNavigationDrawerView.getController().peekDrawer();
        mWearableNavigationDrawerView.addOnItemSelectedListener( this );

        mWearableActionDrawerView = findViewById( R.id.bottom_action_drawer );
        mWearableActionDrawerView.getController().peekDrawer();
        mWearableActionDrawerView.setOnMenuItemClickListener( this );

    }

    private ArrayList<News> initializeNews() {
        ArrayList<News> newsArrayList = new ArrayList<>();
        String[] newsArrayNames = getResources().getStringArray( R.array.news_array_names );

        for (int i = 0; i < newsArrayNames.length; i++) {
            String news = newsArrayNames[i];
            int newsResourcesID = getResources().getIdentifier( news, "array", getPackageName() );

            String[] newsInformation = getResources().getStringArray( newsResourcesID );

            newsArrayList.add( new News( newsInformation[0], newsInformation[1] ) );
        }

        return newsArrayList;

    }

    @Override
    public void onItemSelected(int position) {
        mSelectedNews = position;

        String title = mNews.get( position ).getName();
        String content = mNews.get(position).getContent();

        mNewsFragment.updateNews( title, content );
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        final int itemId = menuItem.getItemId();

        String toastMessage = "";

        switch (itemId) {
            case R.id.menu_display_on_app:
                toastMessage = "Show on app!";
                break;
            case R.id.menu_like:
                toastMessage = "Liked!";
                break;
            case R.id.menu_share:
                toastMessage = "Shared!";
                break;
        }

        mWearableActionDrawerView.getController().closeDrawer();

        if(toastMessage.length() > 0) {
            Toast.makeText( getApplicationContext(), toastMessage, Toast.LENGTH_SHORT ).show();
            return true;
        } else {
            return false;
        }
    }

    private final class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter{

        private final Context mContext;

        public NavigationAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public CharSequence getItemText(int position) {
            return mNews.get( position ).getName();
        }

        @Override
        public Drawable getItemDrawable(int i) {
            return mContext.getDrawable( R.drawable.ic_title_black_24dp );
        }

        @Override
        public int getCount() {
            return mNews.size();
        }
    }

    public static class NewsFragment extends Fragment {

        private TextView title;
        private TextView content;

        public NewsFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate( R.layout.fragment_news, container, false );

            title = rootView.findViewById( R.id.title_fragment );
            content = rootView.findViewById( R.id.content_fragment );

            title.setText( getArguments().getString( "title" ) );
            content.setText( getArguments().getString( "content" ) );

            return rootView;
        }

        public void updateNews(String title, String content) {
            this.title.setText( title );
            this.content.setText( content );
        }
    }
}
