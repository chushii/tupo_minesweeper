package com.example.virus.tupominesweeper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.databinding.FragmentLeaderboardBinding
import com.example.virus.tupominesweeper.stuff.RecordManager
import com.example.virus.tupominesweeper.viewmodels.LeaderboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val leaderboardViewModel =
            ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val diffs = arrayOf("Легкий", "Средний", "Сложный")
        val diffAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diffs).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.DiffChoice.adapter = diffAdapter

        val listAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>()
        )
        binding.Leaderboard.adapter = listAdapter

        binding.DiffChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val records = RecordManager.loadRecords(requireContext(), position)
                val formatted = records.map { record ->
                    "${formatTime(record.score)} (${formatDate(record.timestamp)})"
                }
                listAdapter.clear()
                listAdapter.addAll(formatted)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return root
    }

    fun formatTime(millis: Long): String {
        val seconds = (millis / 1000)
        val h = (seconds / 3600).toInt()
        val m = ((seconds % 3600) / 60).toInt()
        val s = (seconds % 60).toInt()
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}