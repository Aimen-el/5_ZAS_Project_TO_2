package com.semantic.ecare_android_v2.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.semantic.ecare_android_v2.R;
import com.semantic.ecare_android_v2.object.NoteModel;
import com.semantic.ecare_android_v2.util.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteDialogActivity extends Activity {

	private static final int VOICE_RECOGNITION_REQUEST_CODE_RECORD = 1234;
	private static final int VOICE_RECOGNITION_REQUEST_CODE_COMPLETE = 4321;
	public static double [] ARRAY_LAT_LNG_PATIENT;
	private NoteModel note;
	private Button takeNoteButton;
	private Button keyboardEditButton;
	private Button cancelDialogButton;
	private Button saveDialogButton;
	private Button deleteNoteButton;
	private Button completeNoteButton;
	private TextView noteTextView;
	private TextView noteAddressTextView;
	private TextView noteDateTextView;
	private Context context = this;
	private AlertDialog validateDialogTakeNote;
	private AlertDialog validateDialogCompleteNote;
	private AlertDialog keyboardEditDialog;
	private String noteTaken = "";
	private EditText inputFieldNoteEdit;
	private Button AddressButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_note_dialog);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    this.note = (NoteModel) extras.get(Constants.NOTEMODEL_KEY);
		}
		this.AddressButton = (Button)findViewById(R.id.openmaps);
		this.noteTextView = (TextView)findViewById(R.id.noteTextView);
		this.noteAddressTextView = (TextView)findViewById(R.id.addressTextView);
		this.noteDateTextView = (TextView)findViewById(R.id.noteDateTextView);
		updateNoteTextView();
		this.noteTaken = note.getNote();
		this.setFinishOnTouchOutside(false);
		this.completeNoteButton=(Button)findViewById(R.id.completeNoteButton);
		this.takeNoteButton=(Button)findViewById(R.id.rerecordNoteButton);
		this.keyboardEditButton=(Button)findViewById(R.id.keyboardEditNoteButton);
		this.deleteNoteButton=(Button)findViewById(R.id.deleteNoteButton);
		this.cancelDialogButton=(Button)findViewById(R.id.cancelDialogButton);
		this.saveDialogButton=(Button)findViewById(R.id.saveDialogButton);
		this.inputFieldNoteEdit = new EditText(context);

		if(!note.getNote().isEmpty())
		{
			deleteNoteButton.setVisibility(View.VISIBLE);
			keyboardEditButton.setVisibility(View.VISIBLE);
			completeNoteButton.setVisibility(View.VISIBLE);
		}
		
		initiateValidateDialogTakeNote();
		initiateValidateDialogCompleteNote();
		initiateKeyboardEditDialog();
		
		this.takeNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takeNote();
				deleteNoteButton.setVisibility(View.VISIBLE);
				keyboardEditButton.setVisibility(View.VISIBLE);
				completeNoteButton.setVisibility(View.VISIBLE);
			}
		});
		
		this.completeNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				completeNote();
			}
		});

		
		this.keyboardEditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputFieldNoteEdit.setText(noteTaken);
				keyboardEditDialog.show();
			}
		});
		
		this.cancelDialogButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	int resultCode = Constants.CANCEL_NOTE;
		    	Intent resultIntent = new Intent();
		    	resultIntent.putExtra(Constants.NOTEMODEL_KEY, note);
		    	setResult(resultCode, resultIntent);
		        finish();
		    }
		});
		
		this.deleteNoteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteNote();
				updateNoteTextView();
				deleteNoteButton.setVisibility(View.INVISIBLE);
				keyboardEditButton.setVisibility(View.INVISIBLE);
				completeNoteButton.setVisibility(View.INVISIBLE);
			}
		});
		
		this.saveDialogButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	int resultCode = Constants.SAVE_NOTE;
		    	Intent resultIntent = new Intent();
		    	resultIntent.putExtra(Constants.NOTEMODEL_KEY, note);
		    	setResult(resultCode, resultIntent);
		        finish();
		    }
		});

		this.AddressButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
				Intent intent = new Intent(NoteDialogActivity.this, MapsActivity.class);
				startActivity(intent);

		}
	});
	}
	
	private void updateNoteTextView() {
			if(note.getNote().isEmpty()){
				noteTextView.setText("");
				noteDateTextView.setText("");
				noteAddressTextView.setText("");

				noteTextView.setVisibility(View.GONE);
				noteAddressTextView.setVisibility(View.GONE);
				noteDateTextView.setVisibility(View.GONE);
			} else {
				noteTextView.setText(note.getNote());
				noteAddressTextView.setText(note.getAddress());

				if(note.getAddress()!=null) {
					ARRAY_LAT_LNG_PATIENT = findPatientLocation(note.getAddress());
					Log.d("Latitude "+ note.getNote(),Double.toString(ARRAY_LAT_LNG_PATIENT[0]));
					Log.d("Longitude "+note.getNote(),Double.toString(ARRAY_LAT_LNG_PATIENT[1]));
				}

				noteDateTextView.setText(note.getNoteDate());
				noteTextView.setVisibility(View.VISIBLE);
				noteAddressTextView.setVisibility(View.VISIBLE);
				noteDateTextView.setVisibility(View.VISIBLE);
			}
	}

	private void initiateKeyboardEditDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Editer la note");
		builder.setView(inputFieldNoteEdit);
		
		builder.setPositiveButton("Sauvegarder", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   noteTaken = inputFieldNoteEdit.getText().toString();
	        	   updateNote(noteTaken);
	        	   updateNoteTextView();
	        	   dialog.dismiss();
	           }
	       });
		
		builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   inputFieldNoteEdit.setText(noteTaken);
	        	   dialog.cancel();
	           }
	    });
		
		keyboardEditDialog = builder.create();
	}
	
	private void deleteNote(){
		this.note.setNote("");
		this.note.setNoteDate("");
		this.note.setHasBeenEdited(true);
	}
	
	private void initiateValidateDialogTakeNote(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Enregistrer la note ?");

		builder.setPositiveButton("Sauvegarder", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   updateNote(noteTaken);
	        	   updateNoteTextView();
	        	   dialog.dismiss();
	           }
	       });
		
		builder.setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
				noteTaken = note.getNote();
				takeNote();
			}
			
		});
		
		builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               dialog.cancel();
	               noteTaken = note.getNote();
	           }
	    });
		
		validateDialogTakeNote = builder.create();
	}
	
	private void initiateValidateDialogCompleteNote(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Enregistrer la note ?");

		builder.setPositiveButton("Sauvegarder", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   updateNote(noteTaken);
	        	   updateNoteTextView();
	        	   dialog.dismiss();
	           }
	       });
		
		builder.setNeutralButton("Recommencer", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
				noteTaken = note.getNote();
				completeNote();
			}
			
		});
		
		builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               dialog.cancel();
	               noteTaken = note.getNote();
	           }
	    });
		
		validateDialogCompleteNote = builder.create();
	}
	
	@SuppressLint("NewApi")
	private void takeNote() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Enregistrer une note vocale");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE_RECORD);
	}
	
	@SuppressLint("NewApi")
	private void completeNote() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Complter une note vocale");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE_COMPLETE);
	}
	
	/* Updates the not on the object AND in the database */
	private void updateNote(String newNote){
		note.setNote(newNote);
		note.setNoteDate(getDateTime());
		note.setHasBeenEdited(true);
	}
	
	private String getDateTime(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	/* User has spoken, analyse answer */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE_RECORD
				&& resultCode == RESULT_OK) {

			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			this.noteTaken = matches.get(0).toString();
						
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					validateDialogTakeNote.setMessage(noteTaken);
					validateDialogTakeNote.show();
				}
			});
		}
		else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE_COMPLETE
				&& resultCode == RESULT_OK) {

			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			this.noteTaken += " " + matches.get(0).toString();
						
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					validateDialogCompleteNote.setMessage(noteTaken);
					validateDialogCompleteNote.show();
				}
			});
		}
	}

	public double[] findPatientLocation(String patientAddress){

		Geocoder gc = new Geocoder(context);
		double[] tabCord = new double[]{0.0,0.0};

		if(gc.isPresent()){
				List<Address> list;
				try {
					list = gc.getFromLocationName(patientAddress, 1);

						if (list != null) {
							Address address = list.get(0);
							tabCord[0] = address.getLatitude();
							tabCord[1] = address.getLongitude();
						}
				}
				catch (IOException e) {
					Log.d("trying to find location: ",e.getMessage());
				}
	}
			return tabCord;
	}
}
