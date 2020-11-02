package harjoitustyo.projekti.doodlingnotepad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private File imagesFile;

    public ImageAdapter(File folderFile){
        imagesFile = folderFile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_images_relative_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File imageFile = imagesFile.listFiles()[position];
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        holder.getImageView().setImageBitmap(imageBitmap);
    }

    @Override
    public int getItemCount() {
        return imagesFile.listFiles().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view){
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageGalleryView);

        }
        public ImageView getImageView(){
            return imageView;
        }

    }
}
