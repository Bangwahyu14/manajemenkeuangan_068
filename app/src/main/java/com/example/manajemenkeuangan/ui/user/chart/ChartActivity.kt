package com.example.manajemenkeuangan.ui.user.chart

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manajemenkeuangan.data.local.SessionManager
import com.example.manajemenkeuangan.data.remote.ApiConfig
import com.example.manajemenkeuangan.data.repository.TransactionRepository
import com.example.manajemenkeuangan.databinding.ActivityChartBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.DateFormatSymbols

class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Ambil data bulan/tahun yang dikirim dari HomeActivity
        val month = intent.getIntExtra("extra_month", 0)
        val year = intent.getIntExtra("extra_year", 0)

        setupHeader(month, year)
        setupChart()
        loadData(month, year)

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupHeader(month: Int, year: Int) {
        val monthName = DateFormatSymbols().shortMonths[month - 1]
        binding.tvDateInfo.text = "$monthName $year"
    }

    private fun setupChart() {
        binding.pieChart.apply {
            description.isEnabled = false // Hapus label deskripsi kecil
            rotationAngle = 0f
            isRotationEnabled = true
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            animateY(1400) // Animasi putar saat muncul

            // Konfigurasi Legend (Keterangan warna)
            legend.isEnabled = true
            legend.textSize = 14f

            // Style text tengah (jika bolong tengah)
            setCenterText("Laporan")
            setCenterTextSize(16f)
        }
    }

    private fun loadData(month: Int, year: Int) {
        val repository = TransactionRepository(ApiConfig.getApiService())
        val userId = sessionManager.getUserId()

        // Gunakan repository yang sudah ada untuk ambil data
        repository.getTransactions(userId, month, year) { list, error ->
            if (list != null) {
                var totalIncome = 0.0
                var totalExpense = 0.0

                // Hitung manual Pemasukan vs Pengeluaran
                for (trx in list) {
                    val amount = trx.amount ?: 0.0
                    if (trx.type == "pemasukan") {
                        totalIncome += amount
                    } else {
                        totalExpense += amount
                    }
                }

                updateChart(totalIncome, totalExpense)
            } else {
                Toast.makeText(this, "Gagal memuat data: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateChart(income: Double, expense: Double) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        // Masukkan data Pemasukan (Hijau)
        if (income > 0) {
            entries.add(PieEntry(income.toFloat(), "Pemasukan"))
            colors.add(Color.parseColor("#4CAF50")) // Warna Hijau
        }

        // Masukkan data Pengeluaran (Merah)
        if (expense > 0) {
            entries.add(PieEntry(expense.toFloat(), "Pengeluaran"))
            colors.add(Color.parseColor("#F44336")) // Warna Merah
        }

        if (entries.isEmpty()) {
            binding.pieChart.centerText = "Tidak ada Data"
            binding.pieChart.clear()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f // Jarak antar potongan kue
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        // Format angka jadi persen (Opsional)
        // dataSet.valueFormatter = PercentFormatter(binding.pieChart)
        // binding.pieChart.setUsePercentValues(true)

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.invalidate() // Refresh chart
    }
}