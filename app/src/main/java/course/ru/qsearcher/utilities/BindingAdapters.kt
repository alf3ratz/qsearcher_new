package course.ru.qsearcher.utilities

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

    @BindingAdapter("android:imageUrl")
    fun setImageUrl(imageView: ImageView, URL: String) {
        try {
            Picasso.get().load(URL).noFade().into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView.animate().setDuration(300).alpha(1f).start()
                }

                override fun onError(e: Exception) {
                }
            })
        } catch (e: Exception) {
        }
    }
