package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class AdapterAudioDownload extends RecyclerView.Adapter <AdapterAudioDownload.AudioViewHolder>{
    private Context mContext;
    private List<Upload> mUploads;
    private StorageReference mStorageRef;

    private int STORAGE_PERMISSION_CODE = 1;

    AdapterAudioDownload(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.audio_adapter_download, viewGroup, false);
        return new AdapterAudioDownload.AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AudioViewHolder audioViewHolder, int i) {
        final Upload uploadCurrent = mUploads.get(i);
        audioViewHolder.nameSong.setText(uploadCurrent.getName());
        audioViewHolder.nameAlbum.setText(uploadCurrent.getName());

        audioViewHolder.downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadfile(uploadCurrent.getName(), audioViewHolder.progressBar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class AudioViewHolder extends RecyclerView.ViewHolder{

        TextView nameSong;
        TextView nameAlbum;
        ImageView downloadbutton;
        ProgressBar progressBar;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            nameSong = itemView.findViewById(R.id.nameSongdl);
            nameAlbum = itemView.findViewById(R.id.nameAlbumdl);
            downloadbutton = itemView.findViewById(R.id.downloadbutton);
            progressBar = itemView.findViewById(R.id.progressBarDownloads);

        }
    }

    private void downloadfile(final String name, final ProgressBar progressBar ) {
        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        StorageReference nameRef = mStorageRef.child(name+".mp3");


        File rootPath = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)), "Synarc");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File localFile = new File(rootPath,name+".mp3");


        nameRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Handler handler =  new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                    }
                }, 500);

                Toast.makeText(mContext,
                        name+ " Downloaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int)progress);
            }
        });

    }



}

