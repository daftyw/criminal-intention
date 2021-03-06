package com.augmentis.ayp.crimin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeDateFormat;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeFragment extends Fragment {

    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";

    private static final String DIALOG_DATE = "CrimeFragment.DIALOG_DATE";
    private static final String DIALOG_TIME = "CrimeFragment.DIALOG_DATE";
    private static final String DIALOG_IMAGE = "CrimeFragment.DIALOG_IMG";

    private static final int REQUEST_DATE = 2222;
    private static final int REQUEST_TIME = 2221;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 28197;
    private static final int REQUEST_CONTACT_SUSPECT = 29900;

    private static final int REQUEST_CAPTURE_PHOTO = 29800;
    private static final String TAG = "CrimeFragment";

    private Crime crime;
    private File photoFile;

    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private CheckBox crimeSolvedCheckbox;
    private Button crimeReportButton;
    private Button crimeSuspectButton;
    private Button crimeCallSuspect;
    private ImageView photoView;
    private ImageButton photoButton;

    private Callbacks callbacks;

    public CrimeFragment() {}

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    public UUID getCrimeId() {
        if(this.crime != null) {
            return this.crime.getId();
        }
        return null;
    }

    public void updateUI() {
        reloadCrimeFromDB();
        crimeSolvedCheckbox.setChecked(crime.isSolved());
    }

    // Callback
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    /**
     * Read ID argument the reload from DB
     */
    private void reloadCrimeFromDB() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());

        UUID crimeId =(UUID) getArguments().getSerializable(CRIME_ID);
        crime = crimeLab.getCrimeById(crimeId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        reloadCrimeFromDB();
        Log.d(TAG, "crime.getTitle()=" + crime.getTitle());

        photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        editText = (EditText) v.findViewById(R.id.crime_title);
        editText.setText(crime.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText(CrimeDateFormat.toShortDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ///
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment =
                        DatePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm, DIALOG_DATE);
            }
        });

        crimeTimeButton = (Button) v.findViewById(R.id.crime_time);
        crimeTimeButton.setText(CrimeDateFormat.toTime(getActivity(), crime.getCrimeDate()));
        crimeTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
           public void onClick(View v) {
               ///
               FragmentManager fm = getFragmentManager();
               TimePickerFragment dialogFragment =
                       TimePickerFragment.newInstance(crime.getCrimeDate());
               dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
               dialogFragment.show(fm, DIALOG_TIME);
           }
        });

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                Log.d(TAG, "Crime:" + crime.toString());
                updateCrime();
            }
        });

        crimeReportButton = (Button) v.findViewById(R.id.crime_report);
        crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain"); // MIME Type
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);

        crimeSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT_SUSPECT);
            }
        });

        if (crime.getSuspect() != null) {
            crimeSuspectButton.setText(crime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null ) {
            crimeSuspectButton.setEnabled(false);
        }

        crimeCallSuspect = (Button) v.findViewById(R.id.crime_call_suspect);
        crimeCallSuspect.setEnabled( crime.getSuspect() != null );
        crimeCallSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCallPermission()) {
                    callSuspect();
                }
            }
        });

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        photoView = (ImageView) v.findViewById(R.id.crime_photo);

        // Call camera intent
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if we can take photo
        boolean canTakePhoto = photoFile != null
                && captureImageIntent.resolveActivity(packageManager) != null;

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            Log.d(TAG, "File output at " + photoFile.getAbsolutePath());
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        // on click -> start activity for camera
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImageIntent, REQUEST_CAPTURE_PHOTO);
            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoFile.exists()) {
                    DialogFragment dialogFragment = ViewPhotoDialogFragment.newInstance(crime.getId());
                    dialogFragment.show(getFragmentManager(), DIALOG_IMAGE);
                } else {
                    Toast.makeText(getActivity(), "Please take photo first", Toast.LENGTH_LONG).show();
                }
            }
        });

        // update photo changing
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if(result != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            // set
            crime.setCrimeDate(date);
            updateCrime();
            crimeDateButton.setText(CrimeDateFormat.toShortDate(crime.getCrimeDate()));
        }

        if(requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);

            // set
            crime.setCrimeDate(date);
            updateCrime();
            crimeTimeButton.setText(CrimeDateFormat.toTime(getActivity(), crime.getCrimeDate()));
        }

        if(requestCode == REQUEST_CONTACT_SUSPECT) {
            if(data != null) {
                Uri contactUri = data.getData();
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER };

                Cursor c = getActivity()
                        .getContentResolver()
                        .query(contactUri,
                                queryFields,
                                null,
                                null,
                                null);

                try {
                    if(c.getCount() == 0) {
                        return ;
                    }

                    c.moveToFirst();
                    String suspect = c.getString(0);
                    suspect = suspect + ":" + c.getString(1);

                    crime.setSuspect(suspect);
                    updateCrime();
                    crimeSuspectButton.setText(suspect);
                    crimeCallSuspect.setEnabled(suspect != null);
                } finally {
                    c.close();
                }
            }
        }

        if(requestCode == REQUEST_CAPTURE_PHOTO) {
            updatePhotoView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //updateCrime();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.getInstance(getActivity()).deleteCrime(crime.getId());
                callbacks.onCrimeDeleted();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void callSuspect() {
        Intent i = new Intent(Intent.ACTION_CALL);
        StringTokenizer tokenizer = new StringTokenizer(crime.getSuspect(), ":");
        String name = tokenizer.nextToken();
        String phone = tokenizer.nextToken();
        Log.d(TAG, "calling " + name + "/" + phone);
        i.setData(Uri.parse("tel:" + phone));

        startActivity(i);
    }

    private void updateCrime() {
        CrimeLab.getInstance(getActivity()).updateCrime(crime); // update crime in db
        callbacks.onCrimeUpdated(crime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Granted permission
                    callSuspect();

                } else {

                    // Denied permission
                    Toast.makeText(getActivity(),
                            R.string.denied_permission_to_call,
                            Toast.LENGTH_LONG)
                            .show();
                }
                return;
            }
        }
    }

    private boolean hasCallPermission() {

        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            return false; // checking -- wait for dialog
        }

        return true; // already has permission
    }

    private String getCrimeReport() {
        String solvedString = null;

        if(crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                crime.getCrimeDate()).toString();

        String suspect = crime.getSuspect();

        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_with_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView() {
        if(photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap( photoFile.getPath(),
                    getActivity() );

            photoView.setImageBitmap(bitmap);
        }
    }
}
