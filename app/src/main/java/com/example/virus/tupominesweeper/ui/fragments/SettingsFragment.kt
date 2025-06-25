package com.example.virus.tupominesweeper.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.virus.tupominesweeper.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val diffs = arrayOf("Легкий", "Средний", "Сложный", "Пользовательский")
        val diff_adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, diffs)
        diff_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.DiffChoice.setAdapter(diff_adapter)
        binding.DiffChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDifficulty = diffs[position]
                when (position) {
                    0 -> settingsViewModel.setFieldSettings(20, 20, 10)
                    1 -> settingsViewModel.setFieldSettings(20, 20, 20)
                    2 -> settingsViewModel.setFieldSettings(20, 20, 30)
                    else -> settingsViewModel.setFieldSettings(20, 20, 40)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Ничего не выбрано
            }
        }

        val themes = arrayOf("Системная", "Светлая", "Тёмная")
        val theme_adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, themes)
        theme_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ThemeChoice.setAdapter(theme_adapter)

        binding.InverterSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setInvert(isChecked)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}