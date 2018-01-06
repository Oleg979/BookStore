// Вспомогательный класс для вывода информации о книге

package sstu_team.book;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sstu_team.book.dummy.DummyContent;

public class BookDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";


    public BookDetailFragment() {
       // do nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(Metadata.names[Integer.valueOf(Metadata.currentNum)-1]);
            }
        }
    }
}


