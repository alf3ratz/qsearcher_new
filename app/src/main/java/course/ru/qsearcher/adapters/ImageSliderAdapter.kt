package course.ru.qsearcher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ItemContainerSliderImageBinding

class ImageSliderAdapter(sliderImages: ArrayList<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {
    private var sliderImages: ArrayList<String>? = null
    private var layoutInflater: LayoutInflater? = null

    init {
        this.sliderImages = sliderImages
    }

    inner class ImageSliderViewHolder(itemContainerSliderImageBinding: ItemContainerSliderImageBinding) :
        RecyclerView.ViewHolder(itemContainerSliderImageBinding.root) {
        private var itemContainerSliderImageBinding: ItemContainerSliderImageBinding? = null

        init {
            this.itemContainerSliderImageBinding = itemContainerSliderImageBinding
        }

        fun bindSliderImage(imageUrl: String) {
            itemContainerSliderImageBinding?.imageUrl = imageUrl
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val sliderImageBinding: ItemContainerSliderImageBinding = DataBindingUtil.inflate(
            layoutInflater!!, R.layout.item_container_slider_image, parent, false
        )
        return ImageSliderViewHolder(sliderImageBinding)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        sliderImages?.get(position)
            ?.let { holder.bindSliderImage(it) }

    }

    override fun getItemCount(): Int {
        return sliderImages!!.size
    }
}