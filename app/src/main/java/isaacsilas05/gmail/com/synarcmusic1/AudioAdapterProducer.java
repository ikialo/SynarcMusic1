package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AudioAdapterProducer extends RecyclerView.Adapter <AudioAdapterProducer.AudioViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;



    AudioAdapterProducer(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.audio_adapter_producer, viewGroup, false);
        return new AudioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder audioViewHolder, int i) {
        Upload uploadCurrent = mUploads.get(i);
        audioViewHolder.nameSong.setText(uploadCurrent.getName());
        audioViewHolder.nameAlbum.setText(uploadCurrent.getName());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder{

        TextView nameSong;
        TextView nameAlbum;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            nameSong = itemView.findViewById(R.id.nameSong);
            nameAlbum = itemView.findViewById(R.id.nameAlbum);

        }
    }
}
