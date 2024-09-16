package iteMate.project.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import iteMate.project.R;

public class GenericRepository {

    public static void setImageForView(Context context, String imagePath, ImageView imageView) {
        // Get the StorageReference of the image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(imagePath);

        // Check if image exists in the local cache
        File localFile = new File(context.getCacheDir(), "images/" + imagePath);
        File localDir = localFile.getParentFile();
        if (localDir != null && !localDir.exists()) {
            localDir.mkdirs();  // Create the directory if it doesn't exist
        }

        if (localFile.exists()) {
            // Load the image from the local file
            Glide.with(context)
                    .load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.placeholder_image)  // image in drawables
                    .error(R.drawable.error_image)  // image in drawables
                    .into((ImageView) imageView);
        } else {
            // Download the image from Firebase Storage and save it locally
            imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Once downloaded, load the image from the local file
                Glide.with(context)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into((ImageView) imageView);
                Log.d("ItemsDetailActivity", "Image downloaded and cached: " + localFile.getPath());
            }).addOnFailureListener(exception -> {
                // Handle the error gracefully, e.g., show a placeholder image
                Glide.with(context)
                        .load(R.drawable.error_image)
                        .into((ImageView) imageView);
                Log.e("ItemsDetailActivity", "Error getting download URL", exception);
            });
        }
    }
}
