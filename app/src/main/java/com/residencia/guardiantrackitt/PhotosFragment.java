package com.residencia.guardiantrackitt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;

public class PhotosFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageView[] imageViews;
    private EditText[] editTexts;
    private Button selectImageButton;

    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance(String param1, String param2) {
        PhotosFragment fragment = new PhotosFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

        imageViews = new ImageView[5];
        editTexts = new EditText[5];

        imageViews[0] = rootView.findViewById(R.id.imageView1);
        editTexts[0] = rootView.findViewById(R.id.editText1);
        // Repite lo anterior para los imageViews y editTexts restantes

        selectImageButton = rootView.findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return rootView;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                int selectedImageViewIndex = getSelectedImageViewIndex();
                if (selectedImageViewIndex != -1) {
                    imageViews[selectedImageViewIndex].setImageBitmap(bitmap);
                    String tipoFamiliar = getTipoFamiliar(selectedImageViewIndex);
                    editTexts[selectedImageViewIndex].setText(tipoFamiliar);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getSelectedImageViewIndex() {
        for (int i = 0; i < imageViews.length; i++) {
            if (imageViews[i] != null && imageViews[i].getDrawable() == null) {
                return i;
            }
        }
        return -1; // Si no hay ImageView disponible, devuelve -1
    }

    private String getTipoFamiliar(int index) {
        if (editTexts[index] != null) {
            return editTexts[index].getText().toString();
        }
        return "";
    }
}