package android.handyapps.gareth.animalalert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Gareth on 2014-11-20.
 */
public class RegistrationDialog extends DialogFragment {

    // initializing a new fragment
    static RegistrationDialog newInstance(String address){

        RegistrationDialog registerUser = new RegistrationDialog();
        Bundle args = new Bundle();
        args.putString("address",address);
        registerUser.setArguments(args);
        return registerUser;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String address = getArguments().getString("address");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View registrationLayout = inflater.inflate(R.layout.dialog_registration,null);

        TextView userAddress = (TextView)registrationLayout.findViewById(R.id.regLocation);

        userAddress.setText(address);

        builder.setView(registrationLayout)

        // Add action buttons
        .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Register the user
            }
        })
        .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // Close the dialog
                RegistrationDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}
