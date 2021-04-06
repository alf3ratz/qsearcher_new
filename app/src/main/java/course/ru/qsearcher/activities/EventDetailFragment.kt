package course.ru.qsearcher.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.FragmentEventDetailBinding
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EventDetailFragment : AppCompatActivity() {
    private var eventViewModel: MostPopularEventsViewModel? = null;
    private var eventDetailFragmentBinding: FragmentEventDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventDetailFragmentBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_event_detail);
        doInitialization(savedInstanceState)
    }

    private fun doInitialization(savedInstanceState: Bundle?) {
        eventViewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.java);

        getEvents(savedInstanceState)
    }

    private fun getEvents(savedInstanceState: Bundle?) {
        eventDetailFragmentBinding?.isLoading = true;
        var eventId: Int = intent.getIntExtra("id", -1);//getStringExtra("id");
        eventViewModel?.getMostPopularEvents(eventId)
            ?.observe(this, Observer { eventResponse: EventResponse? ->
                run {
                    eventDetailFragmentBinding?.isLoading = false;
                    Toast.makeText(
                        applicationContext,
                        eventResponse?.totalPages,
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
            );

//        eventViewModel?.getMostPopularEvents(eventId)?.observe {
//            eventDetailFragmentBinding?.isLoading = false;
//            Toast.makeText(applicationContext, it.getMostPopularEvents().url,Toast.LENGTH_SHORT).show();
//        }
//        };
    }
}


//class EventDetailFragment : Fragment() {
////    private var param1: String? = null
////    private var param2: String? = null
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        arguments?.let {
////            param1 = it.getString(ARG_PARAM1)
////            param2 = it.getString(ARG_PARAM2)
////        }
////    }
//    private var eventViewModel:MostPopularEventsViewModel?=null;
//    private var eventDetailFragmentBinding:FragmentEventDetailBinding?=null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        eventDetailFragmentBinding = DataBindingUtil.inflate<FragmentEventDetailBinding>(
//            inflater,
//            R.layout.fragment_event_detail,
//            container,
//            false
//        )
//        doInitialization(savedInstanceState)
//        return eventDetailFragmentBinding?.root
//    }
//    private fun doInitialization(savedInstanceState: Bundle?){
//        eventViewModel = ViewModelProvider(this).get(MostPopularEventsViewModel::class.java)
//    getEvents(savedInstanceState)
//    }
//    private fun getEvents(savedInstanceState: Bundle?){
//        eventDetailFragmentBinding?.isLoading=true
//        val bundle = this.arguments
//
//        val eventShowId: String? = bundle?.getString("name","smth wrong")//savedInstanceState?.getString("name", "smth wrong?!")
//        eventDetailFragmentBinding?.isLoading=false
//        Toast.makeText(context, eventShowId, Toast.LENGTH_SHORT)
//        Log.i("detail","должно уже")
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            EventDetailFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}