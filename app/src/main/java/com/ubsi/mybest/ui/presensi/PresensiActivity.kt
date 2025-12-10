package com.ubsi.mybest.ui.presensi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ubsi.mybest.R
import com.ubsi.mybest.data.AppDatabase
import com.ubsi.mybest.data.entity.ScheduleEntity
import com.ubsi.mybest.data.repository.AppRepository
import com.ubsi.mybest.databinding.ActivityPresensiBinding
import com.ubsi.mybest.ui.tugas.TugasActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class PresensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresensiBinding
    private lateinit var repository: AppRepository
    private var scheduleId: Int = -1
    private var schedule: ScheduleEntity? = null

    companion object {
        const val EXTRA_SCHEDULE_ID = "schedule_id"

        fun start(context: Context, scheduleId: Int) {
            val intent = Intent(context, PresensiActivity::class.java).apply {
                putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        repository = AppRepository(database)

        scheduleId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)

        setupAppBar()
        setupListeners()
        loadScheduleData()
    }

    private fun setupAppBar() {
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setupListeners() {
        binding.btnTugas.setOnClickListener {
            schedule?.let { s ->
                TugasActivity.start(this, s.id, s.subjectName)
            }
        }

        binding.btnPresensi.setOnClickListener {
            performPresensi()
        }
    }

    private fun loadScheduleData() {
        lifecycleScope.launch {
            schedule = repository.getScheduleById(scheduleId)

            schedule?.let { s ->
                // Update UI
                binding.tvSubjectName.text = s.subjectName
                binding.tvScheduleTime.text = "${s.day} - ${s.startTime}-${s.endTime}"
                binding.tvDosen.text = "Dosen: ${s.dosen}"
                binding.tvRoom.text = "Ruang: ${s.room}"
                binding.tvKode.text = "Kode: ${s.subjectCode}"
                binding.tvSks.text = "SKS: ${s.sks}"

                // Update button state
                if (s.isAttended) {
                    binding.btnPresensi.text = getString(R.string.presensi_already)
                    binding.btnPresensi.isEnabled = false
                }

                // Generate dummy attendance records
                generateAttendanceList()
            }
        }
    }

    private fun generateAttendanceList() {
        val container = binding.attendanceListContainer
        container.removeAllViews()

        // Generate random attendance data
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var hadirCount = 0
        var izinCount = 0
        var alphaCount = 0

        // Generate 8-14 attendance records
        val recordCount = Random.nextInt(8, 15)

        for (i in 1..recordCount) {
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val date = dateFormat.format(calendar.time)

            // Random status: 85% hadir, 10% izin, 5% alpha
            val statusRandom = Random.nextInt(100)
            val (status, statusColor) = when {
                statusRandom < 85 -> {
                    hadirCount++
                    Pair("HADIR", R.color.status_success)
                }
                statusRandom < 95 -> {
                    izinCount++
                    Pair("IZIN", R.color.status_warning)
                }
                else -> {
                    alphaCount++
                    Pair("ALPHA", R.color.status_error)
                }
            }

            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_attendance, container, false)

            itemView.findViewById<TextView>(R.id.tvNumber).text = i.toString()
            itemView.findViewById<TextView>(R.id.tvDate).text = date
            itemView.findViewById<TextView>(R.id.tvPtm).text = i.toString()

            val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
            tvStatus.text = status
            tvStatus.background.setTint(ContextCompat.getColor(this, statusColor))

            // Add divider
            if (i < recordCount) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    setBackgroundColor(ContextCompat.getColor(this@PresensiActivity, R.color.divider))
                }
                container.addView(itemView)
                container.addView(divider)
            } else {
                container.addView(itemView)
            }
        }

        // Update summary counts
        binding.tvHadirCount.text = hadirCount.toString()
        binding.tvIzinCount.text = izinCount.toString()
        binding.tvAlphaCount.text = alphaCount.toString()
    }

    private fun performPresensi() {
        lifecycleScope.launch {
            schedule?.let { s ->
                repository.updateAttendance(s.id, true, System.currentTimeMillis())
                
                binding.btnPresensi.text = getString(R.string.presensi_already)
                binding.btnPresensi.isEnabled = false
                
                Toast.makeText(
                    this@PresensiActivity,
                    getString(R.string.presensi_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun AppRepository.getScheduleById(id: Int): ScheduleEntity? {
        return try {
            val database = AppDatabase.getDatabase(this@PresensiActivity)
            database.scheduleDao().getScheduleById(id)
        } catch (e: Exception) {
            null
        }
    }
}
