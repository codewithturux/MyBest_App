package com.ubsi.mybest.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ubsi.mybest.R
import com.ubsi.mybest.data.AppDatabase
import com.ubsi.mybest.data.DummyDataGenerator
import com.ubsi.mybest.data.entity.ScheduleEntity
import com.ubsi.mybest.data.repository.AppRepository
import com.ubsi.mybest.databinding.FragmentDashboardBinding
import com.ubsi.mybest.ui.presensi.PresensiActivity
import com.ubsi.mybest.util.PreferenceManager
import com.ubsi.mybest.util.StringUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefManager: PreferenceManager
    private lateinit var repository: AppRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefManager = PreferenceManager(requireContext())
        val database = AppDatabase.getDatabase(requireContext())
        repository = AppRepository(database)
        
        setupSwipeRefresh()
        setupUI()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        viewLifecycleOwner.lifecycleScope.launch {
            setupUI()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupUI() {
        setupGreeting()
        loadUserData()
        loadAppStatus()
        loadTodayClasses()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingRes = when (hour) {
            in 0..10 -> R.string.greeting_morning
            in 11..14 -> R.string.greeting_afternoon
            in 15..18 -> R.string.greeting_evening
            else -> R.string.greeting_night
        }
        binding.tvGreeting.setText(greetingRes)
    }

    private fun loadUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = repository.getCurrentUser().firstOrNull()
            
            if (user != null) {
                val initials = StringUtils.getInitials(user.name)
                binding.tvAvatar.text = initials
                binding.tvStudentName.text = user.name
                binding.tvNim.text = StringUtils.formatNim(user.nim)
            } else {
                val name = prefManager.userName.ifEmpty { "Guest User" }
                val nim = prefManager.savedNim.ifEmpty { "00000000" }
                
                val initials = StringUtils.getInitials(name)
                binding.tvAvatar.text = initials
                binding.tvStudentName.text = name
                binding.tvNim.text = StringUtils.formatNim(nim)
            }
            
            binding.tvQuote.setText(R.string.quote_education)
            binding.tvQuoteAuthor.setText(R.string.quote_author)
        }
    }

    private fun loadAppStatus() {
        // Login Status - check if we have valid user data
        val isLoggedIn = prefManager.isLoggedIn || prefManager.isGuestMode
        binding.tvLoginStatus.text = if (isLoggedIn) getString(R.string.status_valid) else getString(R.string.status_invalid)
        binding.tvLoginStatus.setBackgroundResource(R.drawable.bg_status_badge)
        binding.tvLoginStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (isLoggedIn) resources.getColor(R.color.status_success, null)
            else resources.getColor(R.color.status_error, null)
        )

        // Web Connected - for now always show Yes (in real app, check network connectivity)
        val isWebConnected = true // TODO: Check actual network connectivity
        binding.tvWebStatus.text = if (isWebConnected) getString(R.string.status_yes) else getString(R.string.status_no)
        binding.tvWebStatus.setBackgroundResource(R.drawable.bg_status_badge)
        binding.tvWebStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (isWebConnected) resources.getColor(R.color.status_success, null)
            else resources.getColor(R.color.status_error, null)
        )
    }

    private fun loadTodayClasses() {
        viewLifecycleOwner.lifecycleScope.launch {
            val allSchedules = repository.getAllSchedules().firstOrNull() ?: emptyList()
            val todaySchedules = DummyDataGenerator.getTodaySchedules(allSchedules)
            
            binding.todayClassesContainer.removeAllViews()
            
            if (todaySchedules.isEmpty()) {
                binding.cardNoClasses.visibility = View.VISIBLE
            } else {
                binding.cardNoClasses.visibility = View.GONE
                
                todaySchedules.forEachIndexed { index, schedule ->
                    val cardView = createScheduleCard(schedule)
                    
                    // Add margin between cards
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    if (index > 0) {
                        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.spacing_sm)
                    }
                    cardView.layoutParams = layoutParams
                    
                    binding.todayClassesContainer.addView(cardView)
                }
            }
            
            // Set tips content
            binding.tvTipsContent.text = if (todaySchedules.isEmpty()) {
                "Tidak ada kuliah hari ini. Waktu yang tepat untuk belajar mandiri!"
            } else {
                "Kamu memiliki ${todaySchedules.size} kelas hari ini. Semangat belajar!"
            }
        }
    }

    private fun createScheduleCard(schedule: ScheduleEntity): View {
        val cardView = layoutInflater.inflate(R.layout.item_dashboard_schedule, binding.todayClassesContainer, false)
        
        cardView.findViewById<TextView>(R.id.tvSubjectName)?.text = schedule.subjectName
        cardView.findViewById<TextView>(R.id.tvTime)?.text = "${schedule.startTime} - ${schedule.endTime}"
        cardView.findViewById<TextView>(R.id.tvRoom)?.text = schedule.room
        
        // Set color indicator based on schedule status
        val headerColor = getScheduleColor(schedule)
        cardView.findViewById<View>(R.id.colorIndicator)?.setBackgroundColor(Color.parseColor(headerColor))
        
        cardView.setOnClickListener {
            PresensiActivity.start(requireContext(), schedule.id)
        }
        
        return cardView
    }

    private fun getScheduleColor(schedule: ScheduleEntity): String {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        
        val dayMap = mapOf(
            "Senin" to Calendar.MONDAY,
            "Selasa" to Calendar.TUESDAY,
            "Rabu" to Calendar.WEDNESDAY,
            "Kamis" to Calendar.THURSDAY,
            "Jumat" to Calendar.FRIDAY,
            "Sabtu" to Calendar.SATURDAY,
            "Minggu" to Calendar.SUNDAY
        )
        
        val scheduleDay = dayMap[schedule.day] ?: Calendar.MONDAY
        
        // Parse end time
        val endTimeParts = schedule.endTime.split(":")
        val endHour = endTimeParts.getOrNull(0)?.toIntOrNull() ?: 23
        val endMinute = endTimeParts.getOrNull(1)?.toIntOrNull() ?: 59
        
        return when {
            scheduleDay != currentDay -> "#E74C3C" // Red - future day
            currentHour > endHour || (currentHour == endHour && currentMinute > endMinute) -> "#95A5A6" // Gray - passed
            else -> "#27AE60" // Green - today, ongoing or upcoming
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

