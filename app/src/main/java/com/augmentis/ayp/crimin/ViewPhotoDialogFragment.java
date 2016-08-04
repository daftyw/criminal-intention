package com.augmentis.ayp.crimin;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by Rawin on 04-Aug-16.
 */
public class ViewPhotoDialogFragment extends DialogFragment {
    private static final String PARAM_CRIME_ID = "ViewPhotoDialogFragment.ID";
    private static final String TAG = "ViewPhotoDialogFragment";

    private Crime crime;

    public static ViewPhotoDialogFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(PARAM_CRIME_ID, uuid);
        ViewPhotoDialogFragment fragment = new ViewPhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID uuid = (UUID) getArguments().getSerializable(PARAM_CRIME_ID);
        crime = CrimeLab.getInstance(getActivity()).getCrimeById(uuid);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        ImageView imageView = (ImageView) v.findViewById(R.id.dialog_image_main_view);

        File photo = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
        Log.d(TAG, "Max Width: " + imageView.getMaxWidth());
        Log.d(TAG, "Max Height: " + imageView.getMaxHeight());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        //Bitmap bm = PictureUtils.getScaledBitmap(photo.getPath(), width, height);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 500;
        Bitmap bm = BitmapFactory.decodeFile(photo.getPath(), options);
        imageView.setImageBitmap(bm);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(v);

        return builder.create();
    }
}
