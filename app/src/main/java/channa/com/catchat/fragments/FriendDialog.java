package channa.com.catchat.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

import channa.com.catchat.R;

/**
 * Created by Nancy on 3/30/2017.
 * https://developer.android.com/reference/android/app/DialogFragment.html
 */

public class FriendDialog extends DialogFragment {

    private final static String TAG = "FriendDialog";

    public static FriendDialog newInstance(String title) {
        FriendDialog frag = new FriendDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getArguments().getString("title");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_dialog_friend, null))
                .setTitle(title)
                // Add action buttons
                .setPositiveButton(R.string.chat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "clicked chat");
                    }
                });
        return builder.create();
    }
}
