package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AudioAdapterProducer extends RecyclerView.Adapter <AudioAdapterProducer.AudioViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;



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
        final Upload uploadCurrent = mUploads.get(i);
        audioViewHolder.nameSong.setText(uploadCurrent.getName());
        audioViewHolder.nameAlbum.setText(uploadCurrent.getUsername());

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener
    {

        TextView nameSong;
        TextView nameAlbum;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            nameSong = itemView.findViewById(R.id.nameSong);
            nameAlbum = itemView.findViewById(R.id.nameAlbum);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null){
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            mListener.onWhateverClick(position);
                            return true;

                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }

                }
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            if (mListener != null){
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");

            MenuItem doWhatever = contextMenu.add(Menu.NONE,1,1,"Do Whatever");
            MenuItem delete = contextMenu.add(Menu.NONE,2,2,"Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
    }


    public interface OnItemClickListener{
        void onItemClick (int position);

        void onWhateverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}
