package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.FragmentEventDetailBinding
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.viewmodels.MostPopularEventsViewModel

class EventDetailActivity : AppCompatActivity() {
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
        var title = intent.getStringExtra("title");
        eventViewModel?.getMostPopularEvents(34)
            ?.observe(this, Observer { eventResponse: EventResponse? ->
                run {
                    eventDetailFragmentBinding?.isLoading = false;
                    Toast.makeText(
                        applicationContext,
                        /*eventResponse?.totalPages*/title,
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